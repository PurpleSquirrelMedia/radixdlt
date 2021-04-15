/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Radix DLT Ltd licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.radixdlt.constraintmachine;

import com.radixdlt.atom.SubstateId;
import org.bouncycastle.util.encoders.Hex;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * Unparsed Low level instruction into Radix Engine
 */
public final class REInstruction {
	public enum REOp {
		UP((byte) 1, OptionalInt.empty(), Spin.NEUTRAL, Spin.UP),
		VDOWN((byte) 2, OptionalInt.empty(), Spin.UP, Spin.DOWN),
		DOWN((byte) 3, OptionalInt.of(SubstateId.BYTES), Spin.UP, Spin.DOWN),
		LDOWN((byte) 4, OptionalInt.of(Integer.BYTES), Spin.UP, Spin.DOWN),
		READ((byte) 5, OptionalInt.of(SubstateId.BYTES), Spin.UP, Spin.UP),
		LREAD((byte) 6, OptionalInt.of(Integer.BYTES), Spin.UP, Spin.UP),
		MSG((byte) 7, OptionalInt.empty(), null, null),
		END((byte) 0, OptionalInt.of(0), null, null);

		private final OptionalInt fixedLength;
		private final Spin checkSpin;
		private final Spin nextSpin;
		private final byte opCode;

		REOp(byte opCode, OptionalInt fixedLength, Spin checkSpin, Spin nextSpin) {
			this.opCode = opCode;
			this.fixedLength = fixedLength;
			this.checkSpin = checkSpin;
			this.nextSpin = nextSpin;
		}

		public ByteBuffer toData(ByteBuffer buf) {
			if (fixedLength.isPresent()) {
				return buf.limit(buf.position() + fixedLength.getAsInt());
			} else {
				var lengthByte = buf.get();
				int length = Byte.toUnsignedInt(lengthByte);
				return buf.limit(buf.position() + length);
			}
		}

		public byte opCode() {
			return opCode;
		}

		static REOp fromByte(byte op) {
			for (var microOp : REOp.values()) {
				if (microOp.opCode == op) {
					return microOp;
				}
			}

			throw new IllegalArgumentException("Unknown opcode: " + op);
		}
	}

	private final REOp operation;
	private final byte[] fullBytes;

	private REInstruction(REOp operation, byte[] fullBytes) {
		this.operation = operation;
		this.fullBytes = fullBytes;
	}

	public REOp getMicroOp() {
		return operation;
	}

	public ByteBuffer getData() {
		var b = ByteBuffer.wrap(fullBytes, 1, fullBytes.length - 1);
		return operation.toData(b);
	}

	public byte[] getBytes() {
		return fullBytes;
	}

	public boolean hasSubstate() {
		return operation.checkSpin != null;
	}

	public boolean isPush() {
		return operation.nextSpin != null && !operation.nextSpin.equals(operation.checkSpin);
	}

	public Spin getCheckSpin() {
		return operation.checkSpin;
	}

	public Spin getNextSpin() {
		return operation.nextSpin;
	}

	public static REInstruction create(byte[] instruction) {
		var microOp = REOp.fromByte(instruction[0]);
		return new REInstruction(microOp, instruction);
	}

	@Override
	public String toString() {
		return String.format("%s %s",
			operation,
			Hex.toHexString(fullBytes)
		);
	}

	@Override
	public int hashCode() {
		return Objects.hash(operation, Arrays.hashCode(fullBytes));
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof REInstruction)) {
			return false;
		}

		var other = (REInstruction) o;
		return Objects.equals(this.operation, other.operation)
			&& Arrays.equals(this.fullBytes, other.fullBytes);
	}
}