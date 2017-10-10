package com.github.ontruck;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

class Instruction {
	private final InstructionType type;
	private final Object value;

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
	}
}
