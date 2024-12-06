/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.ptd.server.registry;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author mvolejnik
 */
public class ServiceRegistryImpl implements ServiceRegistry {

  private static URI URN;
  private static URL MSG_URL;
  private static final String MULTICAST_ADDRESS = "239.255.1.1";
  private static final int MULTICAST_PORT = 5678;

  @Override
  public Optional<URL> get(URI serviceUri) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  public void init() throws IOException, InterruptedException, ExecutionException, TimeoutException{
    try (final MulticastSocket socket = new MulticastSocket(MULTICAST_PORT);) {
       var networkInterface = networkInterface(); // TODO to init
       if (networkInterface.isEmpty()){
         throw new IllegalStateException("NetworkInterface not available");
       }
       while (true) {
        socket.joinGroup(new InetSocketAddress(MULTICAST_ADDRESS, MULTICAST_PORT), networkInterface.get());
             Future<String> f = Executors.newSingleThreadExecutor().submit(() -> {
                 return receiveMulticastMessage(socket);
             });
             String s = f.get(1, TimeUnit.SECONDS);
       }
    }
  }

  private Optional<NetworkInterface> networkInterface() throws SocketException {
    return NetworkInterface.networkInterfaces()
            .filter(ni -> {
              try {
                return ni.isLoopback() && ni.isUp() && ni.supportsMulticast();
              } catch (SocketException ex) {
                return false;
              }
            }).findAny();
  }
  
  private String receiveMulticastMessage(MulticastSocket socket) throws IOException {
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        socket.receive(recv);
        return new String(recv.getData(), 0, recv.getLength());
    }
}
