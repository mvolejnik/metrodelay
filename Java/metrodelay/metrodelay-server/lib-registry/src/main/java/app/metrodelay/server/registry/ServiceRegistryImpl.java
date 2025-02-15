/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.metrodelay.server.registry;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mvolejnik
 */
public class ServiceRegistryImpl implements ServiceRegistry {

  private static URI URN;
  private static URL MSG_URL;
  public static final String MULTICAST_ADDRESS = "233.146.53.48";
  public static final int MULTICAST_PORT = 6839;
  private static final Duration SO_TIMEOUT = Duration.ofMinutes(5);
  private final Map<URI, URL> registry = Collections.synchronizedMap(new HashMap<URI, URL>());
  private final String multicastAddress;
  private final int multicastPort;
  private static final Logger l = LogManager.getLogger(ServiceRegistryImpl.class);

  public ServiceRegistryImpl(String multicastAddress, int multicastPort) {
    this.multicastAddress = multicastAddress;
    this.multicastPort = multicastPort;
  }

  @Override
  public Optional<URL> get(URI serviceUri) {
    return Optional.ofNullable(registry.get(serviceUri));
  }

  public void init() throws IOException, InterruptedException, ExecutionException {
    Executors.newSingleThreadExecutor().execute(() -> {
      try (final MulticastSocket socket = new MulticastSocket(MULTICAST_PORT);) {
        var networkInterface = networkInterface();
        if (networkInterface.isEmpty()) {
          throw new IllegalStateException("NetworkInterface not available");
        }
        l.info("bound to multicast interface '{}', address '{}', multicast port '{}'", networkInterface.get().getName(), multicastAddress, multicastPort);
        socket.joinGroup(new InetSocketAddress(MULTICAST_ADDRESS, MULTICAST_PORT), networkInterface.get());
        socket.setSoTimeout((int)SO_TIMEOUT.toMillis());
        while (true) {
          try {
            var payload = receiveMulticastMessage(socket);
            l.info("received '{}'", payload);
            var message = ServiceRegistryMessage.fromJson("register", payload);
            registry.put(message.uri(), message.url());
          } catch (SocketTimeoutException ex) {
            l.warn("no multicast packet received in last '{}'", SO_TIMEOUT);
          } catch (IOException ex) {
            l.fatal("registry task failed", ex);
            throw new RuntimeException("registry task failed", ex);
          } catch (URISyntaxException ex) {
            l.error("registry message processing failed", ex);
          }
        }
      } catch (IOException ex) {
        l.fatal("unable to init service registry");
        throw new RuntimeException("Unable to init service registry", ex);
      }
    });
  }

  private Optional<NetworkInterface> networkInterface() throws SocketException {
    final var multicastInterfaces = NetworkInterface.networkInterfaces()
              .filter(ServiceRegistryImpl::isUp)
              .filter(ServiceRegistryImpl::supportsMulticast)
              .toList();
      if (multicastInterfaces.isEmpty()) {
        l.error("Multicast not allowed for any up interface, allow it using 'ip l set ?? multicast on (or sudo ifconfig ?? multicast)', available interfaces '{}'",
                NetworkInterface.networkInterfaces().map(NetworkInterface::getName).collect(Collectors.joining(",")));
      }
      return multicastInterfaces.stream()
              .filter(ServiceRegistryImpl::isLoopback)
              .findAny()
              .or(() -> Optional.of(multicastInterfaces.getFirst()));
  }

  private String receiveMulticastMessage(MulticastSocket socket) throws IOException {
    byte[] buf = new byte[1000];
    DatagramPacket recv = new DatagramPacket(buf, buf.length);
    socket.receive(recv);
    return new String(recv.getData(), 0, recv.getLength());
  }
  
  private static boolean isUp(NetworkInterface networkInterface) {
    try {
      return networkInterface.isUp();
    } catch (SocketException ex) {
      l.info("ni is down '%s'", networkInterface.getName());
      return false;
    }
  }

  private static boolean isLoopback(NetworkInterface networkInterface) {
    try {
      return networkInterface.isLoopback();
    } catch (SocketException ex) {
      l.debug("ni is not loopback '%s'", networkInterface.getName());
      return false;
    }
  }

  private static boolean supportsMulticast(NetworkInterface networkInterface) {
    try {
      return networkInterface.supportsMulticast();
    } catch (SocketException ex) {
      l.debug("ni is not multicast '%s'", networkInterface.getName());
      return false;
    }
  }
}
