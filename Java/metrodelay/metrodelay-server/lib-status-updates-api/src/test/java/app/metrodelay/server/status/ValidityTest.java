/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package app.metrodelay.server.status;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author mvolejnik
 */
class ValidityTest {

  @Test
  void ofFuture() {
    var validity = Validity.of(Instant.now().plusSeconds(30));
    assertEquals(Validity.FUTURE, validity);
    assertFalse(validity.valid());
  }
  
  @Test
  void ofOngoingWithoutEnd() {
    var validity = Validity.of(Instant.now().minusSeconds(1));
    assertEquals(Validity.ONGOING, validity);
    assertTrue(validity.valid());
  }
  
  @Test
  void ofOngoingWithEnd() {
    var validity = Validity.of(Instant.now().minusSeconds(1), Instant.now().plusSeconds(30));
    assertEquals(Validity.ONGOING, validity);
    assertTrue(validity.valid());
  }

  @Test
  void ofPast() {
    var validity = Validity.of(Instant.now().minusSeconds(30), Instant.now().minusSeconds(1));
    assertEquals(Validity.PAST, validity);
    assertFalse(validity.valid());
  }
  
  @Test
  void ofUnknown() {
    var validity = Validity.of((Instant) null);
    assertEquals(Validity.ONGOING, validity);
    assertTrue(validity.valid());
  }
}
