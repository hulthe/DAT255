package com.github.ontruck;

public class AutonomousController extends Thread{

	private final IDriver driver;
	private final PlanExecutor planExecutor;

	public AutonomousController(IDriver driver) {
		this.driver = driver;
		this.planExecutor = new PlanExecutor(this.driver);
		this.planExecutor.start();
	}

	@Override
	public void run(){
		while (!isInterrupted()) {
			// get old speed
			// calculate new speed
			int newSpeed = 0;

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
