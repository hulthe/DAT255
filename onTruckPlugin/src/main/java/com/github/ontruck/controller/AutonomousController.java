package com.github.ontruck.controller;

import com.github.ontruck.controller.plan.Instruction;
import com.github.ontruck.controller.plan.Plan;
import com.github.ontruck.controller.plan.PlanExecutor;
import com.github.ontruck.moped.IDistanceSensor;
import com.github.ontruck.util.Tuple;

/**
 * This class is used for Adaptive Cruise Control,
 * ergo maintaining driver speed, not exceeding the speed of an object in front of it.
 */
public class AutonomousController extends Thread {

	private final IDistanceSensor sensor;
	private final PlanExecutor executor;

	//TODO: here we need the distance to the object in front
	private int currentDistance;
	public static final int GOAL_DISTANCE = 40;
	private static final byte MAX_POWER = 20;
	private static final long loopDelay = 10;
	private long latestSensorTimeStamp = 0;
	private boolean haveJumpedOneSensorBatch = false;

	/**
	 * @param sensor The data provider for the front distance sensor.
	 */
	public AutonomousController(IDistanceSensor sensor, PlanExecutor executor) {
		this.sensor = sensor;
		currentDistance = sensor.getLatestFilteredDistance().getY();
		this.executor = executor;
	}

	//Returns false if speed does not need to be altered, return true if speed has been altered.
	private Plan generatePlan() {

		// Accepted difference between current distance and goal distance (to prevent constant acceleration,
		// this depends on the accuracy of the distance measurement)
		int distanceMargin = 20;

		//How much distance must have changed since last measurement for us to care
		int relativeVelocity = relativeVelocity();

		//System.out.printf("Δd: [%4d]", relativeVelocity);

		int relativeVelocityLimit = 2;

		//current distance may never be smaller than goal distance, but it can be greater to the margin.
		if (currentDistance >= GOAL_DISTANCE && currentDistance < GOAL_DISTANCE + distanceMargin) {
			if (relativeVelocity > relativeVelocityLimit) { //If distance was just decreased a lot, decrease speed
				return new Plan(
						new Instruction(
								Instruction.InstructionType.DecreaseSpeed,
								MAX_POWER
						)
				);
			} else if (relativeVelocity < -relativeVelocityLimit) { //If distance was just increased a lot, increase speed
				return new Plan(
						new Instruction(
								Instruction.InstructionType.IncreaseSpeed,
								MAX_POWER
						)
				);
			} else { //Distance was good and has not just changed a lot, is good
				return new Plan();
			}
			//currentDistance is greater than GOAL_DISTANCE
		} else if (currentDistance > GOAL_DISTANCE + distanceMargin) {
			// If difference is significantly negative, we do not need to accelerate more
			if (relativeVelocity > relativeVelocityLimit) {
				return new Plan();
			} else {
				return new Plan(
						new Instruction(
								Instruction.InstructionType.IncreaseSpeed,
								MAX_POWER
						)
				);
			}
		} else if (currentDistance < GOAL_DISTANCE) { //currentDistance is smaller than GOAL_DISTANCE
			if (relativeVelocity < -relativeVelocityLimit) {
				return new Plan();
			} else {
				return new Plan(
						new Instruction(
							Instruction.InstructionType.Drive,
							(byte)0
						)
				);
			}
		}
		// Do nothing
		return new Plan();
	}

	//This function calculates the velocity relative to the followed object over the last two measures
	private int relativeVelocity() {
		try {
			// FIXME: factor in the time in this method
			//long timeDiffLong =
			//		sensor.getFilteredDistance(2).getX()-
			//		sensor.getFilteredDistance(0).getX();
			//int timeDiff = toIntExact(timeDiffLong);

			int distanceDiff =
					sensor.getFilteredDistance(2).getY()-
					sensor.getFilteredDistance(0).getY();

			//if(timeDiff == 0) {
			//	return 0;
			//}

			return distanceDiff;
		} catch(IndexOutOfBoundsException e) {
			return 0;
		}
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

			makePlan();
		}
	}

	//Creates a plan according to sensor data
	void makePlan() {
		Tuple<Long, Integer> latestSensorRead = sensor.getLatestFilteredDistance();
		if(latestSensorRead.getX() != latestSensorTimeStamp) {
			if(haveJumpedOneSensorBatch) {
				currentDistance = latestSensorRead.getY();


				Plan plan = generatePlan();
				//System.out.printf(" d: [%4d] rd: [%4d] ", currentDistance, sensor.getLatestRawDistance().getY());
				//System.out.printf("%s%n", plan.toString());
				executor.newPlan(plan);

				haveJumpedOneSensorBatch = false;
			} else {
				haveJumpedOneSensorBatch = true;
			}

			latestSensorTimeStamp = latestSensorRead.getX();
		}
	}
}
