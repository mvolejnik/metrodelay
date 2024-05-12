package app.ptd.server.registry;

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
    }
    
    public URI getServiceUri(){
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
    
    private void sendMulticastMessage(byte[] message){
        try {
            final var loopbackInterface = NetworkInterface.networkInterfaces()
                .filter(ni -> isLoopback(ni))
                .findAny()
                .get();
            DatagramSocket sender = new DatagramSocket(new InetSocketAddress(0));
            sender.setOption(StandardSocketOptions.IP_MULTICAST_IF, loopbackInterface);
            sender.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 0);
            InetSocketAddress dest = new InetSocketAddress(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
            DatagramPacket hi = new DatagramPacket(message, message.length, dest);
            sender.send(hi);
        } catch (IOException ex) {
            l.error("Unable to obtain network interface.", ex);  
        }
    }
    
    private boolean isLoopback(NetworkInterface networkInterface){
      try {
          if (networkInterface.isLoopback() && networkInterface.isUp()) {
            if (networkInterface.supportsMulticast()){
              return true;
            } else {
              l.warn("Multicast not allowed for loopback interface, allow it using 'ip l set {} multicast on (or sudo ifconfig {} multicast)'", networkInterface.getName(), networkInterface.getName());
            }
          }
          return false;
      } catch (SocketException ex) {
          return false;
      }
    }
}
