/*
 * Copyright 2021 Radix Publishing Ltd incorporated in Jersey (Channel Islands).
 * Licensed under the Radix License, Version 1.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at:
 *
 * radixfoundation.org/licenses/LICENSE-v1
 * The Licensor hereby grants permission for the Canonical version of the Work to be
 * published, distributed and used under or by reference to the Licensor’s trademark
 * Radix ® and use of any unregistered trade names, logos or get-up.
 *
 * The Licensor provides the Work (and each Contributor provides its Contributions) on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied,
 * including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT,
 * MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Whilst the Work is capable of being deployed, used and adopted (instantiated) to create
 * a distributed ledger it is your responsibility to test and validate the code, together
 * with all logic and performance of that code under all foreseeable scenarios.
 *
 * The Licensor does not make or purport to make and hereby excludes liability for all
 * and any representation, warranty or undertaking in any form whatsoever, whether express
 * or implied, to any entity or person, including any representation, warranty or
 * undertaking, as to the functionality security use, value or other characteristics of
 * any distributed ledger nor in respect the functioning or value of any tokens which may
 * be created stored or transferred using the Work. The Licensor does not warrant that the
 * Work or any use of the Work complies with any law or regulation in any territory where
 * it may be implemented or used or that it will be appropriate for any specific purpose.
 *
 * Neither the licensor nor any current or former employees, officers, directors, partners,
 * trustees, representatives, agents, advisors, contractors, or volunteers of the Licensor
 * shall be liable for any direct or indirect, special, incidental, consequential or other
 * losses of any kind, in tort, contract or otherwise (including but not limited to loss
 * of revenue, income or profits, or loss of use or data, or loss of reputation, or loss
 * of any economic or other opportunity of whatsoever nature or howsoever arising), arising
 * out of or in connection with (without limitation of any use, misuse, of any ledger system
 * or use made or its functionality or any performance or operation of any code or protocol
 * caused by bugs or programming or logic errors or otherwise);
 *
 * A. any offer, purchase, holding, use, sale, exchange or transmission of any
 * cryptographic keys, tokens or assets created, exchanged, stored or arising from any
 * interaction with the Work;
 *
 * B. any failure in a transmission or loss of any token or assets keys or other digital
 * artefacts due to errors in transmission;
 *
 * C. bugs, hacks, logic errors or faults in the Work or any communication;
 *
 * D. system software or apparatus including but not limited to losses caused by errors
 * in holding or transmitting tokens by any third-party;
 *
 * E. breaches or failure of security including hacker attacks, loss or disclosure of
 * password, loss of private key, unauthorised use or misuse of such passwords or keys;
 *
 * F. any losses including loss of anticipated savings or other benefits resulting from
 * use of the Work or any changes to the Work (however implemented).
 *
 * You are solely responsible for; testing, validating and evaluation of all operation
 * logic, functionality, security and appropriateness of using the Work for any commercial
 * or non-commercial purpose and for any reproduction or redistribution by You of the
 * Work. You assume all risks associated with Your use of the Work and the exercise of
 * permissions under this License.
 */

package com.radixdlt.api.core.network;

import com.google.inject.Inject;
import com.radixdlt.api.archive.ApiHandler;
import com.radixdlt.api.archive.InvalidParametersException;
import com.radixdlt.api.archive.JsonObjectReader;
import com.radixdlt.atom.Txn;
import com.radixdlt.consensus.bft.Self;
import com.radixdlt.crypto.ECPublicKey;
import com.radixdlt.crypto.HashUtils;
import com.radixdlt.identifiers.REAddr;
import com.radixdlt.ledger.AccumulatorState;
import com.radixdlt.ledger.LedgerAccumulator;
import com.radixdlt.networks.Addressing;
import com.radixdlt.networks.Network;
import com.radixdlt.networks.NetworkId;
import com.radixdlt.statecomputer.checkpoint.Genesis;
import com.radixdlt.systeminfo.InMemorySystemInfo;
import com.radixdlt.utils.Bytes;
import org.json.JSONObject;

import static com.radixdlt.api.util.JsonRpcUtil.jsonObject;

final class NetworkStatusHandler implements ApiHandler<NetworkIdentifier> {
	private final Network network;
	private final REAddr accountAddress;
	private final ECPublicKey validatorKey;
	private final InMemorySystemInfo inMemorySystemInfo;
	private final AccumulatorState genesisAccumulatorState;
	private final Addressing addressing;

	@Inject
	NetworkStatusHandler(
		@NetworkId int networkId,
		@Self REAddr accountAddress,
		@Self ECPublicKey validatorKey,
		InMemorySystemInfo inMemorySystemInfo,
		@Genesis Txn genesisTxn,
		LedgerAccumulator ledgerAccumulator,
		Addressing addressing
	) {
		this.network = Network.ofId(networkId).orElseThrow();
		this.accountAddress = accountAddress;
		this.validatorKey = validatorKey;
		this.inMemorySystemInfo = inMemorySystemInfo;
		this.genesisAccumulatorState = ledgerAccumulator.accumulate(
			new AccumulatorState(0, HashUtils.zero256()), genesisTxn.getId().asHashCode()
		);
		this.addressing = addressing;
	}

	@Override
	public NetworkIdentifier parseRequest(JsonObjectReader reader) throws InvalidParametersException {
		return reader.getJsonObject("network_identifier", NetworkIdentifier::from);
	}

	@Override
	public JSONObject handleRequest(NetworkIdentifier request) {
		if (!request.getNetwork().equals(this.network)) {
			throw new IllegalStateException();
		}

		var currentProof = inMemorySystemInfo.getCurrentProof();
		return jsonObject()
			.put("pre_genesis_state_identifier", new JSONObject()
				.put("state_version", 0)
				.put("transaction_accumulator", Bytes.toHexString(HashUtils.zero256().asBytes()))
			)
			.put("genesis_state_identifier", new JSONObject()
				.put("state_version", genesisAccumulatorState.getStateVersion())
				.put("transaction_accumulator", Bytes.toHexString(genesisAccumulatorState.getAccumulatorHash().asBytes()))
			)
			.put("current_state_identifier", new JSONObject()
				.put("state_version", currentProof.getStateVersion())
				.put("transaction_accumulator", Bytes.toHexString(currentProof.getAccumulatorState().getAccumulatorHash().asBytes()))
			)
			.put("current_state_epoch", currentProof.getEpoch())
			.put("current_state_round", currentProof.getView().number())
			.put("current_state_timestamp", currentProof.timestamp())
			.put("node_identifiers", new JSONObject()
				.put("account_address_identifier", new JSONObject()
					.put("address", addressing.forAccounts().of(accountAddress))
				)
				.put("validator_address_identifier", new JSONObject()
					.put("address", addressing.forValidators().of(validatorKey))
				)
				.put("public_key", new JSONObject()
					.put("hex", validatorKey.toHex())
				)
			);
	}
}