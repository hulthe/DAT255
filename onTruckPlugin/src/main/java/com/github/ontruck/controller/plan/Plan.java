package com.github.ontruck.controller.plan;

import java.util.*;

/**
 * This class contains a set of instructions to be executed.
 */
public class Plan implements Cloneable {
	private final Queue<Instruction> instructions;
	private final Object isExecutingLock = new Object();
	private Boolean isExecuting = false;

	public Plan() {
		this.instructions = new LinkedList<>();
	}

	public Plan(Instruction... instructions) {
		this.instructions = new LinkedList<>(Arrays.asList(instructions));
	}

	/**
	 * Add an {@link Instruction} to the Plan instruction queue
	 * @param instruction The given instruction.
	 * @throws IllegalStateException Exception is thrown if this method is called after {@link Plan#poll()} has been called.
	 */
	public void add(Instruction instruction) throws IllegalStateException {
		if(!isExecuting) {
			synchronized (isExecutingLock) {
				if (!isExecuting) {
					instructions.add(instruction);
					return;
				}
			}
		}
		throw new IllegalStateException("Instructions cannot be added to Plan after execution has begun.");
	}

	/**
	 * Extract and removes the first {@link Instruction} from the Plan queue.
	 * <p>This puts a lock on {@link Plan#add(Instruction)} so that no more {@link Instruction}:s can be added
	 * @return The first {@link Instruction}
	 */
	public Instruction poll() {
		if (!isExecuting) {
			synchronized (isExecutingLock) {
				isExecuting = true;
			}
		}
		return instructions.poll();
	}

	@Override
	public Plan clone() {
		return new Plan(instructions.toArray(new Instruction[0]));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("(Plan)[");
		boolean first = true;
		for (Instruction instruction : instructions) {
			builder.append(first ? "" : ", ");
			builder.append(instruction.toString());
			first = false;
		}
		builder.append(']');
		return builder.toString();
	}
}
