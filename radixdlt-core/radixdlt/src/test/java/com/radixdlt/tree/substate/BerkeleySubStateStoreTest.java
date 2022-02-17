/* Copyright 2021 Radix Publishing Ltd incorporated in Jersey (Channel Islands).
 *
 * Licensed under the Radix License, Version 1.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at:
 *
 * radixfoundation.org/licenses/LICENSE-v1
 *
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

package com.radixdlt.tree.substate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.common.primitives.Longs;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.radixdlt.SingleNodeAndPeersDeterministicNetworkModule;
import com.radixdlt.application.system.FeeTable;
import com.radixdlt.application.tokens.Amount;
import com.radixdlt.application.validators.state.ValidatorRegisteredCopy;
import com.radixdlt.consensus.bft.Self;
import com.radixdlt.crypto.ECKeyPair;
import com.radixdlt.environment.deterministic.SingleNodeDeterministicRunner;
import com.radixdlt.mempool.MempoolConfig;
import com.radixdlt.network.p2p.RadixNodeUri;
import com.radixdlt.networks.NetworkId;
import com.radixdlt.statecomputer.checkpoint.MockedGenesisModule;
import com.radixdlt.statecomputer.forks.ForksModule;
import com.radixdlt.statecomputer.forks.MainnetForkConfigsModule;
import com.radixdlt.statecomputer.forks.RERulesConfig;
import com.radixdlt.statecomputer.forks.RadixEngineForksLatestOnlyModule;
import com.radixdlt.store.DatabaseLocation;
import com.radixdlt.store.berkeley.BerkeleyAdditionalStore;
import com.radixdlt.tree.PMT;
import com.radixdlt.utils.PrivateKeys;
import com.radixdlt.utils.UInt256;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BerkeleySubStateStoreTest {
  private static final ECKeyPair TEST_KEY = PrivateKeys.ofNumeric(1);

  @Rule public TemporaryFolder folder = new TemporaryFolder();
  @Inject private SingleNodeDeterministicRunner runner;
  @Inject private BerkeleySubStateStoreSpy berkeleySubStateStore;
  private final Amount totalTokenAmount = Amount.ofTokens(110);
  private final Amount stakeAmount = Amount.ofTokens(10);

  public static final int CACHE_MAXIMUM_SIZE = 1_000;

  @Before
  public void setup() {
    var injector =
        Guice.createInjector(
            MempoolConfig.asModule(10, 10),
            new MainnetForkConfigsModule(),
            new RadixEngineForksLatestOnlyModule(
                RERulesConfig.testingDefault()
                    .overrideFeeTable(
                        FeeTable.create(
                            Amount.ofSubunits(UInt256.ONE),
                            Map.of(
                                ValidatorRegisteredCopy.class, Amount.ofSubunits(UInt256.ONE))))),
            new ForksModule(),
            new SingleNodeAndPeersDeterministicNetworkModule(TEST_KEY, 1),
            new MockedGenesisModule(Set.of(TEST_KEY.getPublicKey()), totalTokenAmount, stakeAmount),
            new AbstractModule() {
              @Override
              protected void configure() {
                bind(BerkeleySubStateStoreSpy.class).in(Scopes.SINGLETON);
                Multibinder.newSetBinder(binder(), BerkeleyAdditionalStore.class)
                    .addBinding()
                    .to(BerkeleySubStateStoreSpy.class);
                bindConstant()
                    .annotatedWith(DatabaseLocation.class)
                    .to(folder.getRoot().getAbsolutePath());
                bindConstant().annotatedWith(NetworkId.class).to(99);
                var selfUri =
                    RadixNodeUri.fromPubKeyAndAddress(
                        99, TEST_KEY.getPublicKey(), "localhost", 23456);
                bind(RadixNodeUri.class).annotatedWith(Self.class).toInstance(selfUri);
              }
            });
    injector.injectMembers(this);
  }

  @Test
  public void
      when_substate_store_process_state_updates__then_tree_must_contain_the_latest_state_of_each_substate() {
    runner.start();

    var berkeleyStorage =
        new BerkeleyStorage(this.berkeleySubStateStore.getSubStateTreeDatabase(), null);
    var pmt =
        new PMT(
            berkeleyStorage,
            this.berkeleySubStateStore.getRootHash(),
            this.berkeleySubStateStore.getSize());
    SubStateTree subStateTree = new SubStateTree(pmt);
    for (var upREStateUpdateSubStateID : this.berkeleySubStateStore.getUpREStateUpdates()) {
      assertNotNull(subStateTree.get(upREStateUpdateSubStateID));
    }
    for (var downREStateUpdateSubStateID : this.berkeleySubStateStore.getDownREStateUpdates()) {
      assertNull(subStateTree.get(downREStateUpdateSubStateID));
    }

    Database epochRootHashDatabase = this.berkeleySubStateStore.getEpochRootHashDatabase();
    Cursor cursor = epochRootHashDatabase.openCursor(null, null);

    var key = new DatabaseEntry();
    var value = new DatabaseEntry();
    cursor.getLast(key, value, null);
    long expectedEpochNumber = 1L;
    Assert.assertEquals(expectedEpochNumber, Longs.fromByteArray(key.getData()));
  }
}
