package edu.eci.dosw.tdd.skyrescue.center;

import edu.eci.dosw.tdd.skyrescue.drone.Drone;
import edu.eci.dosw.tdd.skyrescue.mission.Mission;
import edu.eci.dosw.tdd.skyrescue.operator.RescueOperator;
import edu.eci.dosw.tdd.skyrescue.mission.MissionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinates drones, operators and emergency missions.
 */
public class RescueCenter {

    private final List<RescueOperator> operators;
    private final Map<String, Drone> drones;
    private final List<Mission> missions;

    public RescueCenter() {
        this.operators = new ArrayList<>();
        this.drones = new HashMap<>();
        this.missions = new ArrayList<>();
    }

    /**
     * Registers a drone in the rescue center.
     *
     * Rules:
     * - The drone cannot be null.
     * - The drone id cannot be null or blank.
     * - Two drones cannot have the same id.
     * - A valid drone is stored as available.
     *
     * @param drone drone to register.
     * @return true if it was registered; false otherwise.
     */
    public boolean addDrone(Drone drone) {
        if (drone == null || drone.getId() == null || drone.getId().trim().isEmpty()) {
            return false;
        }
        if (drones.containsKey(drone.getId())) {
            return false;
        }
        drones.put(drone.getId(), drone);
        return true;
    }

    /**
     * Assigns an emergency mission to an operator and an available drone.
     *
     * Rules:
     * - operatorId, droneId and location must be valid.
     * - The operator must exist.
     * - The drone must exist and be available.
     * - distanceKm must be greater than zero.
     * - distanceKm cannot exceed the drone maxRangeKm.
     * - The same operator cannot have two ACTIVE missions.
     * - On success, create an ACTIVE mission with the current date.
     * - On success, the selected drone becomes unavailable.
     * - The created mission must be stored in the center.
     *
     * Suggested error policy:
     * - Invalid/nonexistent data -> IllegalArgumentException.
     * - Valid resource but invalid state -> IllegalStateException.
     *
     * @param operatorId operator identifier.
     * @param droneId    drone identifier.
     * @param location   emergency location description.
     * @param distanceKm mission distance in kilometers.
     * @return created mission.
     */
    public Mission assignMission(
            String operatorId,
            String droneId,
            String location,
            int distanceKm) {
        if (!drones.containsKey(droneId)) {
            throw new IllegalArgumentException("El dron especificado no existe.");
        }

        Drone drone = drones.get(droneId);

        // Agregar validación faltante:
        if (!drone.isAvailable()) {
            throw new IllegalStateException("El dron ya se encuentra asignado a otra misión.");
        }

        RescueOperator operator = operators.stream()
                .filter(op -> op.getId().equals(operatorId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El operador especificado no existe."));

        Mission mission = new Mission(
                "M-" + (missions.size() + 1),
                location,
                distanceKm,
                drone,
                operator,
                java.time.LocalDateTime.now(),
                MissionStatus.ACTIVE);

        drone.setAvailable(false);
        missions.add(mission);
        return mission;
    }

    /**
     * Completes an active mission.
     *
     * Rules:
     * - missionId must be valid.
     * - The mission must exist.
     * - An already COMPLETED mission cannot be completed again.
     * - The mission status changes to COMPLETED.
     * - The end date is the current date/time.
     * - The drone assigned to the mission becomes available again.
     *
     * Suggested error policy:
     * - Invalid/nonexistent mission -> IllegalArgumentException.
     * - Mission already completed -> IllegalStateException.
     *
     * @param missionId mission identifier.
     * @return completed mission.
     */
    public Mission completeMission(String missionId) {
        // TODO Implement using TDD.
        return null;
    }

    public boolean addOperator(RescueOperator operator) {
        return operators.add(operator);
    }
}