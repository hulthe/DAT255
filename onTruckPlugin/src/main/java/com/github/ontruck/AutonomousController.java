package com.github.ontruck;

public class AutonomousController extends Thread {

    private final IDriver driver;
    private final PlanExecutor planExecutor;

    //TODO: here we need the distance to the object in front
    int lastDistance = 0;
    int currentDistance = 0;
    int goalDistance = 0;
    long loopDelay = 800;

    public AutonomousController(IDriver driver) {
        this.driver = driver;
        this.planExecutor = new PlanExecutor(this.driver);
        this.planExecutor.start();
    }

    //Returns false if speed does not need to be altered, return true if speed has been altered.
    public boolean checkAndAlterDistance(int lastDistance, int currentDistance, int goalDistance) {

        // Accepted difference between current distance and goal distance (to prevent constant acceleration, this depends on the accuracy of the distance measurement)
        int distanceMargin = 20;

        //How much distance must have changed since last measurement for us to care
        int distanceDifferenceMargin = 4;

        int distanceDiff = currentDistance - lastDistance;
        //Increase/decrease speed is called only if the distance to the car in front has decreased or increased significally since last interval
        if (goalDistance < currentDistance + distanceMargin && goalDistance > currentDistance - distanceMargin) {
            if (distanceDiff < -distanceDifferenceMargin) { //If distance was just decreased a lot, decrease speed
                driver.decreaseSpeed();
                return true;
            } else if (distanceDiff > distanceDifferenceMargin) { //If distance was just increased a lot, increase speed
                driver.increaseSpeed();
                return true;
            } else { //Distance was good and has not just changed a lot, is good
                return false;
            }
        } else if (currentDistance > goalDistance + distanceMargin) { //currentDistance is greater than goalDistance
            if (distanceDiff > distanceDifferenceMargin) { //If difference is significantly positive, we do not need to accelerate more
                return false;
            } else {
                driver.increaseSpeed();
                return true;
            }
        } else if (currentDistance < goalDistance - distanceMargin) { //currentDistance is smaller than goalDistance
            if (distanceDiff < -distanceDifferenceMargin) { //If difference is significantly negative, we do not need to accelerate more
                return false;
            } else {
                driver.decreaseSpeed();
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {

        int newSpeed = 0;

        while (!isInterrupted()) {

            // delay
            try {
                this.sleep(loopDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (checkAndAlterDistance(lastDistance, currentDistance, goalDistance)) { //If distance is altered, send new instruction and plan

                newSpeed = driver.getLastPowerValue();

                // new Instruction
                Instruction instruction = new Instruction(
                        Instruction.InstructionType.Drive,
                        newSpeed
                );

                // new Plan
                Plan plan = new Plan(instruction);

                // send new plan
                planExecutor.newPlan(plan);
            }
        }
    }
}
