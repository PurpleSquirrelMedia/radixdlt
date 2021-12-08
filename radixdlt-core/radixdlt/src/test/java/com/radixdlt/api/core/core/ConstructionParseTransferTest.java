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

package com.radixdlt.api.core.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.radixdlt.SingleNodeAndPeersDeterministicNetworkModule;
import com.radixdlt.api.core.core.handlers.ConstructionParseHandler;
import com.radixdlt.api.core.core.model.CoreModelMapper;
import com.radixdlt.api.core.core.model.EntityOperation;
import com.radixdlt.api.core.core.model.NotEnoughNativeTokensForFeesException;
import com.radixdlt.api.core.core.model.OperationTxBuilder;
import com.radixdlt.api.core.core.model.ResourceOperation;
import com.radixdlt.api.core.core.model.TokenResource;
import com.radixdlt.api.core.core.model.entities.AccountVaultEntity;
import com.radixdlt.api.core.core.openapitools.model.ConstructionParseRequest;
import com.radixdlt.api.core.core.openapitools.model.NetworkIdentifier;
import com.radixdlt.api.core.core.openapitools.model.Operation;
import com.radixdlt.application.system.FeeTable;
import com.radixdlt.application.tokens.Amount;
import com.radixdlt.consensus.bft.Self;
import com.radixdlt.crypto.ECKeyPair;
import com.radixdlt.crypto.ECPublicKey;
import com.radixdlt.engine.RadixEngine;
import com.radixdlt.environment.deterministic.SingleNodeDeterministicRunner;
import com.radixdlt.identifiers.REAddr;
import com.radixdlt.mempool.MempoolConfig;
import com.radixdlt.networks.NetworkId;
import com.radixdlt.qualifier.NumPeers;
import com.radixdlt.statecomputer.LedgerAndBFTProof;
import com.radixdlt.statecomputer.checkpoint.MockedGenesisModule;
import com.radixdlt.statecomputer.forks.Forks;
import com.radixdlt.statecomputer.forks.ForksModule;
import com.radixdlt.statecomputer.forks.MainnetForkConfigsModule;
import com.radixdlt.statecomputer.forks.RERulesConfig;
import com.radixdlt.statecomputer.forks.RadixEngineForksLatestOnlyModule;
import com.radixdlt.store.DatabaseLocation;
import com.radixdlt.utils.Bytes;
import com.radixdlt.utils.PrivateKeys;
import com.radixdlt.utils.UInt256;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstructionParseTransferTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private static final ECKeyPair TEST_KEY = PrivateKeys.ofNumeric(1);

	private final Amount totalTokenAmount = Amount.ofTokens(110);
	private final Amount stakeAmount = Amount.ofTokens(10);
	private final Amount liquidAmount = Amount.ofSubunits(
		totalTokenAmount.toSubunits().subtract(stakeAmount.toSubunits())
	);
	private final UInt256 toTransfer = liquidAmount.toSubunits().subtract(Amount.ofTokens(1).toSubunits());

	@Inject
	private ConstructionParseHandler sut;
	@Inject
	private CoreModelMapper coreModelMapper;
	@Inject
	@Self
	private ECPublicKey self;
	@Inject
	private SingleNodeDeterministicRunner runner;
	@Inject
	private RadixEngine<LedgerAndBFTProof> radixEngine;
	@Inject
	private Forks forks;

	@Before
	public void setup() {
		var injector = Guice.createInjector(
			MempoolConfig.asModule(1000, 10),
			new MainnetForkConfigsModule(),
			new RadixEngineForksLatestOnlyModule(
				RERulesConfig.testingDefault()
					.overrideFeeTable(FeeTable.create(Amount.ofSubunits(UInt256.ONE), Map.of()))
			),
			new ForksModule(),
			new SingleNodeAndPeersDeterministicNetworkModule(TEST_KEY),
			new MockedGenesisModule(
				Set.of(TEST_KEY.getPublicKey()),
				totalTokenAmount,
				stakeAmount
			),
			new AbstractModule() {
				@Override
				protected void configure() {
					bindConstant().annotatedWith(NumPeers.class).to(0);
					bindConstant().annotatedWith(DatabaseLocation.class).to(folder.getRoot().getAbsolutePath());
					bindConstant().annotatedWith(NetworkId.class).to(99);
				}
			}
		);
		injector.injectMembers(this);
	}

	private byte[] buildUnsignedTransferTxn(REAddr from, REAddr to) throws Exception {

		var entityOperationGroups =
			List.of(List.of(
				EntityOperation.from(
					AccountVaultEntity.from(from),
					ResourceOperation.withdraw(
						TokenResource.from("xrd", REAddr.ofNativeToken()),
						toTransfer
					)
				),
				EntityOperation.from(
					AccountVaultEntity.from(to),
					ResourceOperation.deposit(
						TokenResource.from("xrd", REAddr.ofNativeToken()),
						toTransfer
					)
				)
			));
		var operationTxBuilder = new OperationTxBuilder(null, entityOperationGroups, forks);
		var builder = radixEngine.constructWithFees(
			operationTxBuilder, false, from, NotEnoughNativeTokensForFeesException::new
		);
		var unsignedTransaction = builder.buildForExternalSign();
		return unsignedTransaction.blob();
	}

	@Test
	public void parsing_transaction_with_transfer_should_have_proper_substates() throws Exception {
		// Arrange
		runner.start();
		var accountAddress = REAddr.ofPubKeyAccount(self);
		var otherAddress = REAddr.ofPubKeyAccount(PrivateKeys.ofNumeric(2).getPublicKey());
		var unsignedTxn = buildUnsignedTransferTxn(accountAddress, otherAddress);
		var request = new ConstructionParseRequest()
			.signed(false)
			.networkIdentifier(new NetworkIdentifier().network("localnet"))
			.transaction(Bytes.toHexString(unsignedTxn));

		// Act
		var response = sut.handleRequest(request);

		// Assert
		assertThat(response.getMetadata()).isNotNull();
		assertThat(response.getMetadata().getMessage()).isNull();
		var feeValue = new BigInteger(response.getMetadata().getFee().getValue());
		assertThat(feeValue).isGreaterThan(BigInteger.ZERO);
		var entityHoldings = response.getOperationGroups().stream()
			.flatMap(g -> g.getOperations().stream())
			.peek(op -> {
				assertThat(op.getSubstate()).isNotNull();
				assertThat(op.getSubstate().getSubstateIdentifier()).isNotNull();
				assertThat(op.getAmount()).isNotNull();
				assertThat(op.getAmount().getResourceIdentifier()).isEqualTo(coreModelMapper.nativeToken());
			})
			.collect(Collectors.groupingBy(
				Operation::getEntityIdentifier,
				Collectors.mapping(
					op -> new BigInteger(op.getAmount().getValue()),
					Collectors.reducing(BigInteger.ZERO, BigInteger::add)
				)
			));

		var accountEntityIdentifier = coreModelMapper.entityIdentifier(accountAddress);
		var otherEntityIdentifier = coreModelMapper.entityIdentifier(otherAddress);
		var transferAmount = new BigInteger(toTransfer.toString());
		var expectedChange = transferAmount.negate().subtract(feeValue);
		assertThat(entityHoldings).containsExactlyInAnyOrderEntriesOf(
			Map.of(accountEntityIdentifier, expectedChange, otherEntityIdentifier, transferAmount)
		);
	}
}
