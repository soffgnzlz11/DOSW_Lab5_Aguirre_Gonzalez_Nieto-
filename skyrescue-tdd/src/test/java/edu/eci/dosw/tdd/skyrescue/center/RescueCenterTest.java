package edu.eci.dosw.tdd.skyrescue.center;

import edu.eci.dosw.tdd.skyrescue.drone.Drone;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RescueCenterTest {

    @Test
    void shouldRegisterDroneWhenDataIsValid() {
        // Arrange
        RescueCenter center = new RescueCenter();
        Drone drone = new Drone("D-101", "Rescue-X", 50);

        // Act
        boolean result = center.addDrone(drone);

        // Assert
        assertTrue(result);
    }
}