package com.github.ontruck.controller.plan;

import com.github.ontruck.driver.IDriver;
import sun.misc.Lock;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This class steps through and executed each Instruction in a Plan.
 */
public class PlanExecutor extends Thread {

	private final IDriver driver;

	private Plan plan = null;
	private Plan newPlan;
	private Object newPlanLock = new Lock();

	public PlanExecutor(IDriver driver) {
		this.driver = driver;
	}

	/**
	 * Set a new Plan to be executed. Interrupts execution of the current Plan.
	 * @param plan New Plan
	 */
	public void newPlan(Plan plan) {
		synchronized (this.newPlanLock) {
			this.newPlan = plan;
			if(this.isAlive()) {
				this.interrupt();
			} else {
				this.plan = this.newPlan;
			}
		}
	}

	public Plan getPlan() {
		return plan;
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
			synchronized (this.newPlanLock) {
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
					executeInstruction(instruction);
				}
			} else {
				try {
					Thread.sleep(9001);
				} catch (InterruptedException e) {
					synchronized (this.newPlanLock) {
						if (newPlan == null) {
							break; // Exit thread
						}
					}
					// Carry on
				}
			}
		}
	}

	/**
	 * Executes the given instruction as a command, most often sent to the driver.
	 * @param instruction The given instruction.
	 */
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
			case IncreaseSpeed: {
				byte powerLimit = (byte) instruction.getValue();
				if(powerLimit <= 0 || powerLimit > Math.abs(driver.getLastPowerValue())) {
					driver.increaseSpeed();
				}
			}	break;
			case DecreaseSpeed: {
				byte powerLimit = (byte) instruction.getValue();
				if(powerLimit <= 0 || powerLimit > Math.abs(driver.getLastPowerValue())) {
					driver.decreaseSpeed();
				}
			}	break;
			default:
				throw new NotImplementedException();
		}
	}
}
