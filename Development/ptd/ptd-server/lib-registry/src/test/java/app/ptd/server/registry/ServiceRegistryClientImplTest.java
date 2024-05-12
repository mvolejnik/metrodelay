package app.ptd.server.registry;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;

public class ServiceRegistryClientImplTest {

    private static final URI URN;
    private static URL MSG_URL;
    private static final String MULTICAST_ADDRESS = "239.255.1.1";
    private static final int MULTICAST_PORT = 5678;

    static {
        try {
            URN = new URI("urn:service:test");
            MSG_URL = new URL("https://localhost:8443/test/");
        } catch (URISyntaxException | MalformedURLException ex) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Test of register method, of class ServiceRegistryClientImpl.
     */
    @Disabled
    @Test
    void testRegister() throws UnknownHostException, IOException, InterruptedException, ExecutionException, TimeoutException {
        ServiceRegistryClientImpl instance = new ServiceRegistryClientImpl(new InetSocketAddress(MULTICAST_ADDRESS, MULTICAST_PORT), URN, MSG_URL);
        try (final MulticastSocket socket = new MulticastSocket(MULTICAST_PORT);) {
            Optional<NetworkInterface> networkInterface = networkInterface();
            assertTrue(networkInterface.isPresent(), "Network interface should be available (try 'sudo ip l set lo multicast on' or 'sudo ifconfig lo multicast')");
            socket.joinGroup(new InetSocketAddress(MULTICAST_ADDRESS, MULTICAST_PORT), networkInterface.get());
            Future<String> f = Executors.newSingleThreadExecutor().submit(() -> {
                return receiveMulticastMessage(socket);
            });
            instance.register();
            String s = f.get(1, TimeUnit.SECONDS);
            String expectedJson = """
                                  {"register":{"srv":"%s","url":"%s"}}""".formatted(URN, MSG_URL);
            assertEquals(expectedJson, s, "Unexpected multicast message");
            socket.leaveGroup(new InetSocketAddress(MULTICAST_ADDRESS, MULTICAST_PORT), networkInterface.get());
        } catch (SocketException ex) {
            Logger.getLogger(ServiceRegistryClientImplTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of unregister method, of class ServiceRegistryClientImpl.
     */
    @Disabled
    @Test
    void testUnregister() throws UnknownHostException, IOException, InterruptedException, ExecutionException, TimeoutException {
        System.out.println("unregister");
        ServiceRegistryClientImpl instance = new ServiceRegistryClientImpl(new InetSocketAddress(MULTICAST_ADDRESS, MULTICAST_PORT), URN, MSG_URL);
        try (final MulticastSocket socket = new MulticastSocket(MULTICAST_PORT);) {
            Optional<NetworkInterface> networkInterface = networkInterface();
            assertTrue(networkInterface.isPresent(), "Network interface should be available (try 'sudo ip l set lo multicast on' or 'sudo ifconfig lo multicast')");
            socket.joinGroup(new InetSocketAddress(MULTICAST_ADDRESS, MULTICAST_PORT), networkInterface.get());
            Future<String> f = Executors.newSingleThreadExecutor().submit(() -> {
                return receiveMulticastMessage(socket);
            });
            instance.unregister();
            String s = f.get(1, TimeUnit.SECONDS);
            String expectedJson = "{\"unregister\":{\"srv\":\"" + URN + "\",\"url\":\"" + MSG_URL + "\"}}";
            assertEquals(expectedJson, s, "Unexpected multicast message");
            socket.leaveGroup(new InetSocketAddress(MULTICAST_ADDRESS, MULTICAST_PORT), networkInterface.get());
        }
    }

    private String receiveMulticastMessage(MulticastSocket socket) throws IOException {
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        socket.receive(recv);
        return new String(recv.getData(), 0, recv.getLength());
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
}
