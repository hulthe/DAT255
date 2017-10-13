package com.github.ontruck;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This class steps through and executed each Instruction in a Plan.
 */
public class PlanExecutor extends Thread {

	private final IDriver driver;

	private Plan plan = null;
	private Plan newPlan = null;

	public PlanExecutor(IDriver driver) {
		this.driver = driver;
	}

	/**
	 * Set a new Plan to be executed. Interrupts execution of the current Plan.
	 * @param plan New Plan
	 */
	public void newPlan(Plan plan) {
		synchronized (this.newPlan) {
			this.newPlan = plan;
			this.interrupt();
		}
	}

	/**
	 * Begin execution of the current Plan.
	 * <p>
	 * Will step through every Instruction in the current Plan until all Instruction:s have been depleted.
	 * <p>
	 * After execution the thread will sleep. Calling newPlan(...) will resume execution.
	 */
	@Override
	public void run() {
		while(true) {

			// Check if a new plan has been set
			synchronized (newPlan) {
				if(newPlan != null) {
					plan = newPlan;
					newPlan = null;
				}
			}

			if(plan != null) {
				Instruction instruction = plan.poll();
				if (instruction == null) {
					this.plan = null;
				} else {
					executeInstruction(plan.poll());
				}
			} else {
				try {
					Thread.sleep(9001);
				} catch (InterruptedException e) {
					synchronized (newPlan) {
						if (newPlan == null) {
							break; // Exit thread
						}
					}
					// Carry on
				}
			}
		}
	}

	private void executeInstruction(Instruction instruction) {
		switch (instruction.getType()) {
			case Brake:
				driver.brake((byte)instruction.getValue());
				break;
			case Drive:
				driver.power((byte)instruction.getValue());
				break;
			case Steer:
				driver.steer((byte)instruction.getValue());
				break;
			case Sleep:
				try {
					Thread.sleep((long)instruction.getValue());
				} catch (InterruptedException e) {
					this.interrupt(); // Make next blocking call handle interrupts
				}
				break;
			default:
				throw new NotImplementedException();
		}
	}
}
