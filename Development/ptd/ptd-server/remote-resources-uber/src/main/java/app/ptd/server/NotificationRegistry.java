/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.ptd.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 *
 * @author mvolejnik
 */
public class NotificationRegistry {
   
      private String receiveMulticastMessage(MulticastSocket socket) throws IOException {
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        while (true){
          socket.receive(recv);
          //var message = String(recv.getData(), 0, recv.getLength());
        }
    }
}
