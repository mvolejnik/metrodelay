package app.metrodelay.server.registry;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author mvolejnik
 */
public class ServiceRegistryClientImpl implements ServiceRegistryClient {

  private InetSocketAddress inetSocketAddress;
  private URI uri;
  private URL url;
  private Charset UTF_8 = Charset.forName("UTF-8");
  private static final Logger l = LogManager.getLogger(ServiceRegistryClientImpl.class);

  public ServiceRegistryClientImpl(InetSocketAddress inetSocketAddress, URI serviceUri, URL url) {
    this.inetSocketAddress = inetSocketAddress;
    this.uri = serviceUri;
    this.url = url;
    l.info("service registry client for '{}' {}", uri, inetSocketAddress);
  }

  public URI getServiceUri() {
    return uri;
  }

  @Override
  public void register() {
    sendMulticastMessage(new ServiceRegistryMessage(uri, url).toJson("register").getBytes(UTF_8));
  }

  @Override
  public void unregister() {
    sendMulticastMessage(new ServiceRegistryMessage(uri, url).toJson("unregister").getBytes(UTF_8));
  }

  private void sendMulticastMessage(byte[] message) {
    try {
      final var multicastInterfaces = NetworkInterface.networkInterfaces()
              .filter(ServiceRegistryClientImpl::isUp)
              .filter(ServiceRegistryClientImpl::supportsMulticast)
              .toList();
      if (multicastInterfaces.isEmpty()) {
        l.error("Multicast not allowed for any up interface, allow it using 'ip l set ?? multicast on (or sudo ifconfig ?? multicast)', available interfaces '{}'",
                NetworkInterface.networkInterfaces().map(NetworkInterface::getName).collect(Collectors.joining(",")));
      }
      final var multicastInterface = multicastInterfaces.stream()
              .filter(ServiceRegistryClientImpl::isLoopback)
              .findAny()
              .orElse(multicastInterfaces.getFirst());
      DatagramSocket sender = new DatagramSocket(new InetSocketAddress(0));
      sender.setOption(StandardSocketOptions.IP_MULTICAST_IF, multicastInterface);
      sender.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 0);
      InetSocketAddress dest = new InetSocketAddress(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
      DatagramPacket hi = new DatagramPacket(message, message.length, dest);
      sender.send(hi);
      l.debug("multicast message sent via '{}'", multicastInterface.getName());
    } catch (IOException ex) {
      l.error("Unable to obtain network interface.", ex);
    }
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
