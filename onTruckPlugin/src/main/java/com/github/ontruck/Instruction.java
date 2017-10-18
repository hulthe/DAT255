package com.github.ontruck;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This class defines an instruction to be executed by a PlanExecutor.
 */

class Instruction {
	private final InstructionType type;
	private final Object value;

	/**
	 * @param type The type of instruction to be executed (e.g. Steer)
	 * @param value An reference to the value to be applied by the instruction (e.g. Steering Angle)
	 * @throws IllegalArgumentException Exception is thrown when the reference type doesn't match the desired type (e.g. A Byte was expected, received an Int)
	 */
	public Instruction(InstructionType type, Object value) throws IllegalArgumentException {
		this.type = type;
		switch (type) {
			case Brake:
				if(!(value instanceof Byte)) {
					throw new IllegalArgumentException("Type of value for Brake instruction must be Byte");
				}
				break;
			case Drive:
				if(!(value instanceof Byte)) {
					throw new IllegalArgumentException("Type of value for Drive instruction must be Byte");
				}
				break;
			case Steer:
				if(!(value instanceof Byte)) {
					throw new IllegalArgumentException("Type of value for Steer instruction must be Byte");
				}
				break;
			case Sleep:
				if(!(value instanceof Long)) {
					throw new IllegalArgumentException("Type of value for Sleep instruction must be Long");
				}
				break;
			case IncreaseSpeed:
				// We do not care about value
				break;
			case DecreaseSpeed:
				// We do not care about value
				break;
			default:
				throw new NotImplementedException();
		}
		this.value = value;
	}

	public InstructionType getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	enum InstructionType {
		Brake,
		Drive,
		Steer,
		Sleep,
		IncreaseSpeed,
		DecreaseSpeed
	}
}
