/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.metrodelay.server.registry;

/**
 *
 * @author mvolejnik
 */
public class RegistryException extends Exception{

  public RegistryException(String message) {
    super(message);
  }

  public RegistryException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
