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

package com.radixdlt.statecomputer.forks;

import static com.radixdlt.statecomputer.forks.RERulesVersion.OLYMPIA_V1;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.radixdlt.consensus.LedgerProof;
import com.radixdlt.consensus.bft.BFTNode;
import com.radixdlt.consensus.bft.BFTValidator;
import com.radixdlt.consensus.bft.BFTValidatorSet;
import com.radixdlt.statecomputer.LedgerAndBFTProof;
import com.radixdlt.sync.CommittedReader;
import com.radixdlt.utils.UInt256;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.Test;

public final class ForksTest {

  @Test
  public void should_fail_when_two_forks_with_the_same_hash() {
    final var fork1 =
        new FixedEpochForkConfig("fork1", OLYMPIA_V1.create(RERulesConfig.testingDefault()), 0L);
    final var fork2 =
        new FixedEpochForkConfig("fork1", OLYMPIA_V1.create(RERulesConfig.testingDefault()), 1L);

    final var exception =
        assertThrows(IllegalArgumentException.class, () -> Forks.create(Set.of(fork1, fork2)));

    assertTrue(exception.getMessage().contains("duplicate name"));
  }

  @Test
  public void should_fail_when_no_genesis() {
    final var fork1 =
        new FixedEpochForkConfig("fork1", OLYMPIA_V1.create(RERulesConfig.testingDefault()), 1L);

    final var exception =
        assertThrows(IllegalArgumentException.class, () -> Forks.create(Set.of(fork1)));

    assertTrue(exception.getMessage().contains("must start at epoch"));
  }

  @Test
  public void should_fail_when_duplicate_epoch() {
    final var fork1 =
        new FixedEpochForkConfig("fork1", OLYMPIA_V1.create(RERulesConfig.testingDefault()), 0L);
    final var fork2 =
        new FixedEpochForkConfig("fork2", OLYMPIA_V1.create(RERulesConfig.testingDefault()), 2L);
    final var fork3 =
        new FixedEpochForkConfig("fork3", OLYMPIA_V1.create(RERulesConfig.testingDefault()), 2L);

    final var exception =
        assertThrows(
            IllegalArgumentException.class, () -> Forks.create(Set.of(fork1, fork2, fork3)));

    assertTrue(exception.getMessage().contains("duplicate epoch"));
  }

  @Test
  public void forks_should_respect_candidate_epoch_limits() {
    final var candidate =
        new CandidateForkConfig(
            "candidate",
            OLYMPIA_V1.create(RERulesConfig.testingDefault()),
            (short) 8000,
            3L,
            5L,
            1);

    final var countedForksVotes =
        votesFor(candidate, candidate.requiredStake() /* fork has just enough stake votes */);

    assertFalse(
        Forks.testCandidate(
            candidate,
            proofForCandidate(
                1L /* next epoch = 2; minEpoch <!= 2 <= maxEpoch */, countedForksVotes)));
    assertTrue(
        Forks.testCandidate(
            candidate,
            proofForCandidate(
                2L /* next epoch = 3; minEpoch <= 3 <= maxEpoch */, countedForksVotes)));
    assertTrue(
        Forks.testCandidate(
            candidate,
            proofForCandidate(
                3L /* next epoch = 4; minEpoch <= 4 <= maxEpoch */, countedForksVotes)));
    assertTrue(
        Forks.testCandidate(
            candidate,
            proofForCandidate(
                4L /* next epoch = 5; minEpoch <= 5 <= maxEpoch */, countedForksVotes)));
    assertFalse(
        Forks.testCandidate(
            candidate,
            proofForCandidate(
                5L /* next epoch = 6; minEpoch <= 6 <!= maxEpoch */, countedForksVotes)));
  }

  @Test
  public void forks_should_respect_candidate_required_stake() {
    final var candidate =
        new CandidateForkConfig(
            "candidate",
            OLYMPIA_V1.create(RERulesConfig.testingDefault()),
            (short) 8000,
            3L,
            5L,
            1);

    assertFalse(
        Forks.testCandidate(
            candidate,
            proofForCandidate(
                candidate.minEpoch() - 1,
                votesFor(
                    candidate, (short) (candidate.requiredStake() - 1) /* too little stake */))));

    assertTrue(
        Forks.testCandidate(
            candidate,
            proofForCandidate(
                candidate.minEpoch() - 1,
                votesFor(candidate, candidate.requiredStake() /* just enough stake */))));

    assertTrue(
        Forks.testCandidate(
            candidate,
            proofForCandidate(
                candidate.minEpoch() - 1,
                votesFor(
                    candidate, (short) (candidate.requiredStake() + 1) /* more than required */))));
  }

  private LedgerAndBFTProof proofForCandidate(
      long epoch, ImmutableMap<HashCode, Short> countedForksVotes) {
    final var ledgerProof = mock(LedgerProof.class);
    // value is not used, but optional needs to be present in proof (test for epoch boundary)
    final var validatorSet =
        BFTValidatorSet.from(Stream.of(BFTValidator.from(BFTNode.random(), UInt256.ONE)));
    when(ledgerProof.getNextValidatorSet()).thenReturn(Optional.of(validatorSet));

    when(ledgerProof.getEpoch()).thenReturn(epoch);

    return LedgerAndBFTProof.create(ledgerProof).withCountedForksVotes(countedForksVotes);
  }

  private ImmutableMap<HashCode, Short> votesFor(CandidateForkConfig forkConfig, short votes) {
    return ImmutableMap.of(CandidateForkVote.forkConfigParamsHash(forkConfig), votes);
  }

  @Test
  public void forks_should_signal_ledger_inconsistency_when_db_entry_is_missing() {
    final var fork1 =
        new FixedEpochForkConfig("fork1", OLYMPIA_V1.create(RERulesConfig.testingDefault()), 0L);
    final var fork2 =
        new FixedEpochForkConfig("fork2", OLYMPIA_V1.create(RERulesConfig.testingDefault()), 10L);

    final var forks = Forks.create(Set.of(fork1, fork2));

    final var committedReader = mock(CommittedReader.class);
    final var forksEpochStore = mock(ForksEpochStore.class);

    // latest epoch is 11, so fork2 should be stored...
    final var proof = proofAtEpoch(11L);
    when(committedReader.getLastProof()).thenReturn(Optional.of(proof));

    // ...but it isn't
    when(forksEpochStore.getStoredForks()).thenReturn(ImmutableMap.of(0L, fork1.name()));

    final var exception =
        assertThrows(
            IllegalStateException.class, () -> forks.init(committedReader, forksEpochStore));

    assertTrue(exception.getMessage().toLowerCase().contains("forks inconsistency"));
  }

  @Test
  public void forks_should_signal_ledger_inconsistency_when_config_is_missing() {
    final var fork1 =
        new FixedEpochForkConfig("fork1", OLYMPIA_V1.create(RERulesConfig.testingDefault()), 0L);

    final var forks = Forks.create(Set.of(fork1));

    final var committedReader = mock(CommittedReader.class);
    final var forksEpochStore = mock(ForksEpochStore.class);

    final var proof = proofAtEpoch(11L);
    when(committedReader.getLastProof()).thenReturn(Optional.of(proof));

    when(forksEpochStore.getStoredForks())
        .thenReturn(
            ImmutableMap.of(
                0L,
                fork1.name(),
                10L,
                "fork2" /* fork2 was executed at epoch 10 according to the ledger */));

    final var exception =
        assertThrows(
            IllegalStateException.class, () -> forks.init(committedReader, forksEpochStore));

    assertTrue(exception.getMessage().toLowerCase().contains("forks inconsistency"));
  }

  private LedgerProof proofAtEpoch(long epoch) {
    final var ledgerProof = mock(LedgerProof.class);
    when(ledgerProof.getEpoch()).thenReturn(epoch);
    return ledgerProof;
  }
}
