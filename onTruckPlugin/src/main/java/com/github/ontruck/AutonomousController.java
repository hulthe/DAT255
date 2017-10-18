package com.github.ontruck;

public class AutonomousController extends Thread {

    private final DistanceSensor sensor;
    private final PlanExecutor executor;

    //TODO: here we need the distance to the object in front
    int lastDistance;
    int currentDistance;
    int goalDistance = 30;
    long loopDelay = 800;

    public AutonomousController(DistanceSensor sensor, PlanExecutor executor) {
        this.sensor = sensor;
        lastDistance  = sensor.getLatesteFilteredDistance();
        currentDistance = sensor.getLatesteFilteredDistance();
        this.executor = executor;    int lastDistance = sensor.getLatesteFilteredDistance();

    }

    //Returns false if speed does not need to be altered, return true if speed has been altered.
    private Plan generatePlan() {

        // Accepted difference between current distance and goal distance (to prevent constant acceleration, this depends on the accuracy of the distance measurement)
        int distanceMargin = 20;

        //How much distance must have changed since last measurement for us to care
        int distanceDifferenceMargin = 4;

        int distanceDiff = currentDistance - lastDistance;
        //Increase/decrease speed is called only if the distance to the car in front has decreased or increased significally since last interval
        if (goalDistance < currentDistance + distanceMargin && goalDistance > currentDistance - distanceMargin) {
            if (distanceDiff < -distanceDifferenceMargin) { //If distance was just decreased a lot, decrease speed
				return new Plan(
					new Instruction(
						Instruction.InstructionType.DecreaseSpeed,
						null
					)
				);
            } else if (distanceDiff > distanceDifferenceMargin) { //If distance was just increased a lot, increase speed
				return new Plan(
					new Instruction(
						Instruction.InstructionType.IncreaseSpeed,
						null
					)
				);
            } else { //Distance was good and has not just changed a lot, is good
				return new Plan();
            }
        } else if (currentDistance > goalDistance + distanceMargin) { //currentDistance is greater than goalDistance
            if (distanceDiff < -distanceDifferenceMargin) { //If difference is significantly negative, we do not need to accelerate more
				return new Plan();
            } else {
				return new Plan(
					new Instruction(
						Instruction.InstructionType.IncreaseSpeed,
						null
					)
				);
            }
        } else if (currentDistance < goalDistance - distanceMargin) { //currentDistance is smaller than goalDistance
            //If difference is significantly positive, we do not need to accelerate more
            if (distanceDiff > distanceDifferenceMargin) {
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

    private int distanceDiffMargin(){
        return 0;
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
            currentDistance=sensor.getLatesteFilteredDistance();
            lastDistance = sensor.getFilteredDistance(1).getY();
            executor.newPlan(generatePlan());
        }
    }
}
