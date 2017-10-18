package com.github.ontruck;

import static java.lang.Math.toIntExact;

public class AutonomousController extends Thread {

    private final DistanceSensor sensor;
    private final PlanExecutor executor;

    //TODO: here we need the distance to the object in front
    int currentDistance;
    int goalDistance = 30;
    long loopDelay = 800;

    public AutonomousController(DistanceSensor sensor, PlanExecutor executor) {
        this.sensor = sensor;
        currentDistance = sensor.getLatestFilteredDistance();
        this.executor = executor;
    }

    //Returns false if speed does not need to be altered, return true if speed has been altered.
    private Plan generatePlan() {

        // Accepted difference between current distance and goal distance (to prevent constant acceleration, this depends on the accuracy of the distance measurement)
        int distanceMargin = 20;

        //How much distance must have changed since last measurement for us to care
        int relativeVelocity = relativeVelocity();

        int relativeVelocityLimit = 2;

        //current distance may never be smaller than goal distance, but it can be greater to the margin.
        if (currentDistance >= goalDistance && currentDistance < goalDistance + distanceMargin) {
            if (relativeVelocity < -relativeVelocityLimit) { //If distance was just decreased a lot, decrease speed
                return new Plan(
                        new Instruction(
                                Instruction.InstructionType.DecreaseSpeed,
                                null
                        )
                );
            } else if (relativeVelocity > relativeVelocityLimit) { //If distance was just increased a lot, increase speed
                return new Plan(
                        new Instruction(
                                Instruction.InstructionType.IncreaseSpeed,
                                null
                        )
                );
            } else { //Distance was good and has not just changed a lot, is good
                return new Plan();
            }
            //currentDistance is greater than goalDistance
        } else if (currentDistance > goalDistance + distanceMargin) {
            //If difference is significantly negative, we do not need to accelerate more
            if (relativeVelocity < -relativeVelocityLimit) {
                return new Plan();
            } else {
                return new Plan(
                        new Instruction(
                                Instruction.InstructionType.IncreaseSpeed,
                                null
                        )
                );
            }
        } else if (currentDistance < goalDistance) { //currentDistance is smaller than goalDistance
            if (relativeVelocity > relativeVelocityLimit) {
                return new Plan();
            } else {
                return new Plan(
                        new Instruction(
                                Instruction.InstructionType.DecreaseSpeed,
                                null
                        )
                );
            }
        }
        // Do nothing
        return new Plan();
    }

    //This function calculates the velocity relative to the followed object over the last five measures
    private int relativeVelocity() {
        long timeDiffLong = sensor.getFilteredDistance(0).getX() - sensor.getFilteredDistance(4).getX();
        int timeDiff = toIntExact(timeDiffLong);
        int distanceDiff = sensor.getLatesteFilteredDistance() - sensor.getFilteredDistance(4).getY();
        return distanceDiff / timeDiff;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {

            // delay
            try {
                this.sleep(loopDelay);
            } catch (InterruptedException e) {
                this.interrupt();
            }
            currentDistance = sensor.getLatestFilteredDistance();
            executor.newPlan(generatePlan());
        }
    }
}
