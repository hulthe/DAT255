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
			case Sleep:
				if(!(value instanceof Long)) {
					throw new IllegalArgumentException(String.format("Type of value for %s must be Long", type));
				}
				break;
			case Drive:
			case Brake:
			case Steer:
			case IncreaseSpeed:
			case DecreaseSpeed:
				if(!(value instanceof Byte)) {
					throw new IllegalArgumentException(String.format("Type of value for %s must be Byte", type));
				}
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

	@Override
	public String toString() {
		return type.toString();
	}
}
