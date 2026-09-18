package edu.eci.dosw.tdd.skyrescue.center;

import edu.eci.dosw.tdd.skyrescue.drone.Drone;
import edu.eci.dosw.tdd.skyrescue.operator.RescueOperator;
import edu.eci.dosw.tdd.skyrescue.mission.Mission;
import edu.eci.dosw.tdd.skyrescue.mission.MissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RescueCenterTest {

    private RescueCenter center;

    @BeforeEach
    void setUp() {
        center = new RescueCenter();
    }

    // tests addDrone

    @Test
    void shouldRegisterDroneWhenDataIsValid() {
        Drone drone = new Drone("D-101", "Rescue-X", 50);
        boolean result = center.addDrone(drone);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenDroneIsNull() {
        boolean result = center.addDrone(null);
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenDroneIdIsEmpty() {
        Drone drone = new Drone("", "Rescue-X", 50);
        boolean result = center.addDrone(drone);
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenDroneIdAlreadyExists() {
        Drone drone1 = new Drone("D-101", "Rescue-X", 50);
        Drone drone2 = new Drone("D-101", "Rescue-Y", 80);
        center.addDrone(drone1);
        boolean result = center.addDrone(drone2);
        assertFalse(result);
    }

    // tests assignMission

    @Test
    void shouldAssignMissionWhenDataIsValid() {
        Drone drone = new Drone("D-101", "Rescue-X", 50);
        RescueOperator operator = new RescueOperator("OP-1", "Carlos");

        center.addDrone(drone);
        center.addOperator(operator);

        Mission mission = center.assignMission("OP-1", "D-101", "Zona Norte", 30);

        assertNotNull(mission);
        assertEquals(MissionStatus.ACTIVE, mission.getStatus());
        assertFalse(drone.isAvailable());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenDroneDoesNotExist() {
        RescueOperator operator = new RescueOperator("OP-1", "Carlos");
        center.addOperator(operator);

        assertThrows(IllegalArgumentException.class, () -> {
            center.assignMission("OP-1", "D-999", "Zona Norte", 30);
        });
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenDroneIsBusy() {
        Drone drone = new Drone("D-101", "Rescue-X", 50);
        RescueOperator operator1 = new RescueOperator("OP-1", "Carlos");
        RescueOperator operator2 = new RescueOperator("OP-2", "Ana");
        center.addDrone(drone);
        center.addOperator(operator1);
        center.addOperator(operator2);

        // Asigna la primera misión, dejando el dron no disponible
        center.assignMission("OP-1", "D-101", "Zona Norte", 30);

        // Intenta asignar una segunda misión con el mismo dron
        assertThrows(IllegalStateException.class, () -> {
            center.assignMission("OP-2", "D-101", "Zona Sur", 15);
        });
    }

    // tests completeMission
    @Test
    void shouldCompleteActiveMissionSuccessfully() {
        Drone drone = new Drone("D-101", "Rescue-X", 50);
        RescueOperator operator = new RescueOperator("OP-1", "Carlos");
        center.addDrone(drone);
        center.addOperator(operator);
        Mission mission = center.assignMission("OP-1", "D-101", "Zona Norte", 30);

        center.completeMission(mission.getId());

        assertEquals(MissionStatus.COMPLETED, mission.getStatus());
        assertNotNull(mission.getEndDate());
        assertTrue(drone.isAvailable());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenMissionDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> {
            center.completeMission("M-999");
        });
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenCompletingAlreadyCompletedMission() {
        Drone drone = new Drone("D-102", "Rescue-Y", 60);
        RescueOperator operator = new RescueOperator("OP-2", "Ana");
        center.addDrone(drone);
        center.addOperator(operator);
        Mission mission = center.assignMission("OP-2", "D-102", "Zona Sur", 15);

        center.completeMission(mission.getId());

        assertThrows(IllegalStateException.class, () -> {
            center.completeMission(mission.getId());
        });
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenMissionIdIsInvalid() {
        IllegalArgumentException exceptionNull = assertThrows(
                IllegalArgumentException.class,
                () -> center.completeMission(null));

        assertEquals(
                "El id de la misión no puede ser nulo o vacío.",
                exceptionNull.getMessage());

        IllegalArgumentException exceptionEmpty = assertThrows(
                IllegalArgumentException.class,
                () -> center.completeMission(" "));

        assertEquals(
                "El id de la misión no puede ser nulo o vacío.",
                exceptionEmpty.getMessage());
    }

}