package com.github.ontruck;

public class AutonomousController extends Thread{

	private final IDriver driver;
	private final PlanExecutor planExecutor;

	int newSpeed = 0;
	//TODO: here we need the distance to the object in front
	int lastDistance = 0;
	int currentDistance = 0;
	int distanceDiff = currentDistance - lastDistance;
	int goalDistance = 0;
	// Accepted difference between current distance and goal distance (to prevent constant acceleration, this depends on the accuracy of the distance measurement)
	int margin = 20;
	long loopDelay = 800;


	public AutonomousController(IDriver driver) {
		this.driver = driver;
		this.planExecutor = new PlanExecutor(this.driver);
		this.planExecutor.start();
	}

	@Override
	public void run(){

		while (!isInterrupted()) {

			// delay
			try {
				this.sleep(loopDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//Increase/decrease speed is called only if the distance to the car in front has decreased or increased significally since last interval
			if (goalDistance < currentDistance + margin && goalDistance > currentDistance - margin) {
				if (distanceDiff < 2) {
					continue;
				}
			} else if (currentDistance > goalDistance) { //currentDistance is greater than goalDistance
				if (distanceDiff > 2) { //If difference is significantly positive, we do not need to accelerate more
					continue;
				} else {
					driver.increaseSpeed();
				}
			} else { //currentDistance is smaller than goalDistance
				if (distanceDiff < -2) { //If difference is significantly negative, we do not need to accelerate more
					continue;
				} else {
					driver.decreaseSpeed();
				}
			}

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
