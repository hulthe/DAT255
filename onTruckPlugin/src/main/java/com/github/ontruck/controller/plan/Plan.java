package com.github.ontruck.controller.plan;

import java.util.*;

/**
 * This class contains a set of instructions to be executed.
 */
public class Plan implements Cloneable {
	private final Queue<Instruction> instructions;
	private Boolean isExecuting = false;

	public Plan() {
		this.instructions = new LinkedList<>();
	}

	public Plan(Instruction... instructions) {
		this.instructions = new LinkedList<>(Arrays.asList(instructions));
	}

	public void add(Instruction instruction) throws IllegalStateException {
		if(!isExecuting) {
			synchronized (isExecuting) {
				if (!isExecuting) {
					instructions.add(instruction);
					return;
				}
			}
		}
		throw new IllegalStateException("Instructions cannot be added to Plan after execution has begun.");
	}

	public Instruction poll() {
		if (!isExecuting) {
			synchronized (isExecuting) {
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
