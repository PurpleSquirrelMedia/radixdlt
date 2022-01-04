package com.radixdlt.store.tree;

import java.nio.ByteBuffer;

import static com.radixdlt.store.tree.TreeUtils.applyPrefix;

public class PMTExt extends PMTNode {

	private static final int EVEN_PREFIX = 0;
	private static final int ODD_PREFIX = 1;
	private byte[] prefixedKey;
	// TODO: explicit test for Nibble prefix! Check java Endianness
	byte[] getEvenPrefix() {
		return ByteBuffer.allocate(8).putInt(EVEN_PREFIX).array();
	}
	byte[] getOddPrefix() {
		return ByteBuffer.allocate(4).putInt(ODD_PREFIX).array();
	}

	PMTExt(PMTKey allNibbles, byte[] newHashPointer) {
		this(null, allNibbles, newHashPointer);
	}

	PMTExt(PMTKey branchNibble, PMTKey keyNibbles, byte[] newHashPointer) {
		nodeType = NodeType.EXTENSION; // refactor to casting or pattern
		this.branchNibble = branchNibble;
		this.keyNibbles = keyNibbles;
		this.value = newHashPointer;
	}

	public byte[] serialize() {
		// INFO: It doesn't make sense for Extension to have empty key-part.
		//       We rewrite hash pointer to Branches' nibble position
		if (keyNibbles.isEmpty()) {
			return this.getValue();
		} else {
			this.prefixedKey = applyPrefix(this.getKey().getKey(), getOddPrefix(), getEvenPrefix());

			// TODO: serialize, RLP?
			this.serialized = "ext".getBytes();
			return this.serialized;
		}
	}
}
