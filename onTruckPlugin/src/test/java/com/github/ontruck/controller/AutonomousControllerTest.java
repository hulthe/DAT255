package com.github.ontruck.controller;

import com.github.ontruck.controller.AutonomousController;
import com.github.ontruck.controller.plan.Instruction;
import com.github.ontruck.controller.plan.PlanExecutor;
import com.github.ontruck.moped.*;
import com.github.ontruck.util.Tuple;
import org.junit.Test;

import static com.github.ontruck.controller.AutonomousController.GOAL_DISTANCE;
import static org.junit.Assert.*;

public class AutonomousControllerTest {

	private MockDriver driver = new MockDriver();

	@Test
	public void testConstantSpeed() {
		//Test if ACC properly maintains speed when distance is uniformly correct
		PlanExecutor planExecutor = new PlanExecutor(driver);
		MockDistanceSensor distanceSensor = new MockDistanceSensor(
				new Tuple<>((long) 0, GOAL_DISTANCE),
				new Tuple<>((long) 1, GOAL_DISTANCE),
				new Tuple<>((long) 2, GOAL_DISTANCE),
				new Tuple<>((long) 3, GOAL_DISTANCE),
				new Tuple<>((long) 4, GOAL_DISTANCE),
				new Tuple<>((long) 5, GOAL_DISTANCE),
				new Tuple<>((long) 6, GOAL_DISTANCE),
				new Tuple<>((long) 7, GOAL_DISTANCE)
		);
		AutonomousController autonomousController = new AutonomousController(distanceSensor, planExecutor);

		autonomousController.makePlan();

		distanceSensor.addData(new Tuple<>((long) 8, GOAL_DISTANCE));
		autonomousController.makePlan();

		assertNotNull("AutonomousController has set a plan", planExecutor.getPlan());
		assertNull("AutonomousController set an empty plan", planExecutor.getPlan().poll());
	}

	@Test
	public void testTooLowSpeed() {
		//Test if ACC increases speed when distance is too long
		PlanExecutor planExecutor = new PlanExecutor(driver);
		MockDistanceSensor distanceSensor = new MockDistanceSensor(
				new Tuple<>((long) 0, GOAL_DISTANCE),
				new Tuple<>((long) 4, GOAL_DISTANCE + 3),
				new Tuple<>((long) 5, GOAL_DISTANCE + 6),
				new Tuple<>((long) 6, GOAL_DISTANCE + 9),
				new Tuple<>((long) 7, GOAL_DISTANCE + 12)
		);
		AutonomousController autonomousController = new AutonomousController(distanceSensor, planExecutor);

		autonomousController.makePlan();

		distanceSensor.addData(new Tuple<>((long)8, GOAL_DISTANCE + 15));
		autonomousController.makePlan();

		assertNotNull("AutonomousController has set a plan", planExecutor.getPlan());
		Instruction instruction = planExecutor.getPlan().poll();
		assertNotNull("AutonomousController did not set an empty plan", instruction);
		assertEquals("AutonomousController sent IncreaseSpeed instruction", instruction.getType(), Instruction.InstructionType.IncreaseSpeed);
	}

	@Test
	public void testTooHighSpeed()	{
		//Test if ACC decreases speed when distance is closing in
		PlanExecutor planExecutor = new PlanExecutor(driver);
		MockDistanceSensor distanceSensor = new MockDistanceSensor(
				new Tuple<>((long) 0, GOAL_DISTANCE + 18),
				new Tuple<>((long) 4, GOAL_DISTANCE + 15),
				new Tuple<>((long) 5, GOAL_DISTANCE + 12),
				new Tuple<>((long) 6, GOAL_DISTANCE + 9),
				new Tuple<>((long) 7, GOAL_DISTANCE + 5)
		);
		AutonomousController autonomousController = new AutonomousController(distanceSensor, planExecutor);

		autonomousController.makePlan();

		distanceSensor.addData(new Tuple<>((long)8, GOAL_DISTANCE + 2));
		autonomousController.makePlan();

		assertNotNull("AutonomousController has set a plan", planExecutor.getPlan());
		Instruction instruction = planExecutor.getPlan().poll();
		assertNotNull("AutonomousController did not set an empty plan", instruction);
		assertEquals("AutonomousController sent DecreaseSpeed instruction", instruction.getType(), Instruction.InstructionType.DecreaseSpeed);
	}

	@Test
	public void testRecoverFromTooClose()	{
		//Test if ACC maintains speed when too close but already slowing down
		PlanExecutor planExecutor = new PlanExecutor(driver);
		MockDistanceSensor distanceSensor = new MockDistanceSensor(
				new Tuple<>((long) 0, GOAL_DISTANCE - 20),
				new Tuple<>((long) 4, GOAL_DISTANCE - 15),
				new Tuple<>((long) 5, GOAL_DISTANCE - 10),
				new Tuple<>((long) 6, GOAL_DISTANCE - 8),
				new Tuple<>((long) 7, GOAL_DISTANCE - 5)
		);
		AutonomousController autonomousController = new AutonomousController(distanceSensor, planExecutor);

		autonomousController.makePlan();

		distanceSensor.addData(new Tuple<>((long)8, GOAL_DISTANCE - 2));
		autonomousController.makePlan();

		assertNotNull("AutonomousController has set a plan", planExecutor.getPlan());
		assertNull("AutonomousController set an empty plan", planExecutor.getPlan().poll());
	}

	@Test
	public void testRecoverFromTooFar()	{
		//Test if ACC maintains speed when too far away but already speeding up
		PlanExecutor planExecutor = new PlanExecutor(driver);
		MockDistanceSensor distanceSensor = new MockDistanceSensor(
				new Tuple<>((long) 0, GOAL_DISTANCE + 90),
				new Tuple<>((long) 4, GOAL_DISTANCE + 90),
				new Tuple<>((long) 5, GOAL_DISTANCE + 80),
				new Tuple<>((long) 6, GOAL_DISTANCE + 70),
				new Tuple<>((long) 7, GOAL_DISTANCE + 60)
		);
		AutonomousController autonomousController = new AutonomousController(distanceSensor, planExecutor);

		autonomousController.makePlan();

		distanceSensor.addData(new Tuple<>((long)8, GOAL_DISTANCE + 50));
		autonomousController.makePlan();

		assertNotNull("AutonomousController has set a plan", planExecutor.getPlan());
		assertNull("AutonomousController set an empty plan", planExecutor.getPlan().poll());
	}

	@Test
	public void testFullStop() {
		//Test if ACC brakes when distance is a little too short
		PlanExecutor planExecutor = new PlanExecutor(driver);
		MockDistanceSensor distanceSensor = new MockDistanceSensor(
				new Tuple<>((long) 0, GOAL_DISTANCE + 9),
				new Tuple<>((long) 4, GOAL_DISTANCE + 6),
				new Tuple<>((long) 5, GOAL_DISTANCE + 3),
				new Tuple<>((long) 6, GOAL_DISTANCE + 0),
				new Tuple<>((long) 7, GOAL_DISTANCE - 3)
		);
		AutonomousController autonomousController = new AutonomousController(distanceSensor, planExecutor);

		autonomousController.makePlan();

		distanceSensor.addData(new Tuple<>((long)8, GOAL_DISTANCE - 6));
		autonomousController.makePlan();

		assertNotNull("AutonomousController has set a plan", planExecutor.getPlan());
		Instruction instruction = planExecutor.getPlan().poll();
		assertNotNull("AutonomousController did not set an empty plan", instruction);
		assertEquals("AutonomousController sent Drive instruction", instruction.getType(), Instruction.InstructionType.Drive);
		assertEquals("AutonomousController set Drive value to 0", instruction.getValue(), (byte)0);

	}
}
