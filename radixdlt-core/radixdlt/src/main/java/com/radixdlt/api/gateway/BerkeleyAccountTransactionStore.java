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

package com.radixdlt.api.gateway;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.radixdlt.api.gateway.openapitools.JSON;
import com.radixdlt.api.gateway.openapitools.model.AccountTransaction;
import com.radixdlt.application.system.state.RoundData;
import com.radixdlt.application.system.state.ValidatorStakeData;
import com.radixdlt.application.tokens.state.TokenResourceMetadata;
import com.radixdlt.atom.SubstateTypeId;
import com.radixdlt.constraintmachine.Particle;
import com.radixdlt.constraintmachine.REProcessedTxn;
import com.radixdlt.constraintmachine.RawSubstateBytes;
import com.radixdlt.constraintmachine.SystemMapKey;
import com.radixdlt.crypto.ECPublicKey;
import com.radixdlt.engine.RadixEngine;
import com.radixdlt.identifiers.AID;
import com.radixdlt.identifiers.REAddr;
import com.radixdlt.serialization.DeserializeException;
import com.radixdlt.statecomputer.LedgerAndBFTProof;
import com.radixdlt.store.DatabaseEnvironment;
import com.radixdlt.store.berkeley.BerkeleyAdditionalStore;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Transaction;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static com.google.common.primitives.UnsignedBytes.lexicographicalComparator;
import static com.sleepycat.je.OperationStatus.SUCCESS;

public final class BerkeleyAccountTransactionStore implements BerkeleyAdditionalStore {
	private static final String TRANSACTIONS_DB = "radix.versioned_transactions";
	private Database transactions;
	private final ModelMapper modelMapper;
	private final Provider<RadixEngine<LedgerAndBFTProof>> radixEngineProvider;
	private final AtomicReference<Instant> timestamp = new AtomicReference<>();

	@Inject
	public BerkeleyAccountTransactionStore(
		ModelMapper modelMapper,
		Provider<RadixEngine<LedgerAndBFTProof>> radixEngineProvider
	) {
		this.modelMapper = modelMapper;
		this.radixEngineProvider = radixEngineProvider;
	}

	@Override
	public void open(DatabaseEnvironment dbEnv) {
		var env = dbEnv.getEnvironment();
		transactions = env.openDatabase(null, TRANSACTIONS_DB, new DatabaseConfig()
			.setAllowCreate(true)
			.setTransactional(true)
			.setKeyPrefixing(true)
			.setBtreeComparator(lexicographicalComparator())
		);
	}

	@Override
	public void close() {
		if (transactions != null) {
			transactions.close();
		}
	}

	public Optional<AccountTransaction> get(AID txnId) {
		var key = new DatabaseEntry(txnId.getBytes());
		var value = new DatabaseEntry();
		var result = transactions.get(null, key, value, null);
		if (!result.equals(SUCCESS)) {
			return Optional.empty();
		}
		try {
			var transaction = JSON.getDefault().getMapper().readValue(value.getData(), AccountTransaction.class);
			return Optional.of(transaction);
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	private Particle deserialize(byte[] data) {
		var deserialization = radixEngineProvider.get().getSubstateDeserialization();
		try {
			return deserialization.deserialize(data);
		} catch (DeserializeException e) {
			throw new IllegalStateException();
		}
	}

	@Override
	public void process(
		Transaction dbTxn,
		REProcessedTxn txn,
		long stateVersion,
		Function<SystemMapKey, Optional<RawSubstateBytes>> mapper
	) {
		txn.stateUpdates()
			.filter(u -> u.getParsed() instanceof RoundData)
			.map(u -> (RoundData) u.getParsed())
			.filter(r -> r.getTimestamp() > 0)
			.map(RoundData::asInstant)
			.forEach(timestamp::set);

		Function<REAddr, String> addressToSymbol = addr -> {
			var mapKey = SystemMapKey.ofResourceData(addr, SubstateTypeId.TOKEN_RESOURCE_METADATA.id());
			var data = mapper.apply(mapKey).orElseThrow().getData();
			// TODO: This is a bit of a hack to require deserialization, figure out correct abstraction
			var metadata = (TokenResourceMetadata) deserialize(data);
			return metadata.getSymbol();
		};

		Function<ECPublicKey, ValidatorStakeData> getValidatorStake = key -> {
			var validatorDataKey = SystemMapKey.ofSystem(SubstateTypeId.VALIDATOR_STAKE_DATA.id(), key.getCompressedBytes());
			var data = mapper.apply(validatorDataKey).orElseThrow().getData();
			// TODO: This is a bit of a hack to require deserialization, figure out correct abstraction
			return (ValidatorStakeData) deserialize(data);
		};

		var accountTransaction = modelMapper.accountTransaction(
			txn, timestamp.get(), addressToSymbol, getValidatorStake
		);

		try {
			var key = new DatabaseEntry(txn.getTxnId().getBytes());
			var bytes = JSON.getDefault().getMapper().writeValueAsBytes(accountTransaction);
			var value = new DatabaseEntry(bytes);
			transactions.putNoOverwrite(dbTxn, key, value);
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}
}