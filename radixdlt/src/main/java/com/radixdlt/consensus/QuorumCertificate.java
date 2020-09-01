/*
 *  (C) Copyright 2020 Radix DLT Ltd
 *
 *  Radix DLT Ltd licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License.  You may obtain a copy of the
 *  License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied.  See the License for the specific
 *  language governing permissions and limitations under the License.
 */

package com.radixdlt.consensus;

import com.radixdlt.consensus.bft.View;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.radixdlt.serialization.DsonOutput;
import com.radixdlt.serialization.SerializerConstants;
import com.radixdlt.serialization.SerializerDummy;
import com.radixdlt.serialization.SerializerId2;
import com.radixdlt.serialization.DsonOutput.Output;
import java.util.Optional;

@SerializerId2("consensus.qc")
public final class QuorumCertificate {
	@JsonProperty(SerializerConstants.SERIALIZER_NAME)
	@DsonOutput(value = {Output.API, Output.WIRE, Output.PERSIST})
	SerializerDummy serializer = SerializerDummy.DUMMY;

	@JsonProperty("signatures")
	@DsonOutput(Output.ALL)
	private final TimestampedECDSASignatures signatures;

	@JsonProperty("vote_data")
	@DsonOutput(Output.ALL)
	private final VoteData voteData;

	@JsonCreator
	public QuorumCertificate(
		@JsonProperty("vote_data") VoteData voteData,
		@JsonProperty("signatures") TimestampedECDSASignatures signatures
	) {
		this.voteData = Objects.requireNonNull(voteData);
		this.signatures = Objects.requireNonNull(signatures);
	}

	/**
	 * Create a mocked QC for genesis vertex
	 * @param genesisVertex the vertex to create a qc for
	 * @return a mocked QC
	 */
	public static QuorumCertificate ofGenesis(Vertex genesisVertex) {
		if (!genesisVertex.getView().isGenesis()) {
			throw new IllegalArgumentException(String.format("Vertex is not genesis: %s", genesisVertex));
		}

		CommandHeader commandHeader = CommandHeader.ofGenesisVertex(genesisVertex);
		final VoteData voteData = new VoteData(commandHeader, commandHeader, commandHeader);
		return new QuorumCertificate(voteData, new TimestampedECDSASignatures());
	}

	public View getView() {
		return voteData.getProposed().getView();
	}

	public CommandHeader getProposed() {
		return voteData.getProposed();
	}

	public CommandHeader getParent() {
		return voteData.getParent();
	}

	public Optional<CommandHeader> getCommitted() {
		return voteData.getCommitted();
	}

	public Optional<VerifiedCommittedHeader> toProof() {
		return voteData.getCommitted().map(committed -> new VerifiedCommittedHeader(
			voteData.getProposed(),
			voteData.getParent(),
			committed,
			signatures
		));
	}

	public VoteData getVoteData() {
		return voteData;
	}

	public TimestampedECDSASignatures getTimestampedSignatures() {
		return signatures;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		QuorumCertificate that = (QuorumCertificate) o;
		return Objects.equals(signatures, that.signatures)
			&& Objects.equals(voteData, that.voteData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(signatures, voteData);
	}

	@Override
	public String toString() {
		return String.format("QC{view=%s}", this.getView());
	}
}
