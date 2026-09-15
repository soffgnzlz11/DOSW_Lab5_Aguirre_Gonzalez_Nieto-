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
    @Test
    void shouldReturnFalseWhenDroneIsNull() {
        // Act
        boolean result = center.addDrone(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenDroneIdIsEmpty() {
        // Arrange
        Drone drone = new Drone("", "Rescue-X", 50);

        // Act
        boolean result = center.addDrone(drone);

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenDroneIdAlreadyExists() {
        // Arrange
        Drone drone1 = new Drone("D-101", "Rescue-X", 50);
        Drone drone2 = new Drone("D-101", "Rescue-Y", 80);
        center.addDrone(drone1);

        // Act
        boolean result = center.addDrone(drone2);

        // Assert
        assertFalse(result);
    }

}