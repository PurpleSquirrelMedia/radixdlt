package com.radixdlt.store.tree;

import com.google.common.base.Objects;

import java.util.Arrays;

public final class PMTBranch extends PMTNode {

	public static final int NUMBER_OF_NIBBLES = 16;

	private byte[][] slices;
	private int slicesCounter = 0; // INFO: for removal

	record PMTBranchChild(PMTKey branchNibble, byte[] representation) {

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			PMTBranchChild that = (PMTBranchChild) o;
			return Objects.equal(branchNibble, that.branchNibble)
					&& Arrays.equals(representation, that.representation);
		}

		@Override
		public int hashCode() {
			int result = Objects.hashCode(branchNibble);
			result = 31 * result + Arrays.hashCode(representation);
			return result;

		}

		@Override
		public String toString() {
			return "PMTBranchChild{"
					+ "branchNibble=" + branchNibble
					+ ", representation=" + Arrays.toString(representation)
					+ '}';
		}
	}

	 public PMTBranch(byte[][] slices, byte[] value) {
		this.nodeType = NodeType.BRANCH;
		this.slices = slices;
		this.value = value;
	}

	public PMTBranch(byte[] value, PMTBranchChild... nextNode) {
		this.nodeType = NodeType.BRANCH;
		this.slices = new byte[NUMBER_OF_NIBBLES][];
		Arrays.fill(slices, new byte[0]);
		Arrays.stream(nextNode).forEach(l -> setNibble(l));
		if (value != null) {
			this.value = value;
		}
	}

	public byte[][] getSlices() {
		return slices;
	}

	public byte[] getNextHash(PMTKey key) {
		var nib = key.getRaw()[0];
		return slices[nib];
	}

	public PMTBranch setNibble(PMTBranchChild nextNode) {
		var sliceKey = nextNode.branchNibble.getFirstNibbleValue();
		if (this.slices[sliceKey] == null) {
			slicesCounter++;
		}
		this.slices[sliceKey] = nextNode.representation;
		return this;
	}

	public PMTBranch copyForEdit() {
		try {
			return (PMTBranch) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new IllegalStateException("Can't clone branch for edits");
		}
	}
}
