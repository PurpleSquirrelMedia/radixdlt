/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Radix DLT Ltd licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.radixdlt;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.radixdlt.counters.SystemCounters;
import com.radixdlt.crypto.Hasher;
import com.radixdlt.environment.EventDispatcher;
import com.radixdlt.mempool.LocalMempool;
import com.radixdlt.mempool.Mempool;
import com.radixdlt.mempool.MempoolAddSuccess;

import java.util.Random;

public class LedgerLocalMempoolModule extends AbstractModule {
	private final int maxSize;

	public LedgerLocalMempoolModule(int maxSize) {
		this.maxSize = maxSize;
	}

	@Provides
	@Singleton
	Mempool localMempool(
		Hasher hasher,
		SystemCounters counters,
		EventDispatcher<MempoolAddSuccess> mempoolAddedCommandEventDispatcher,
		Random random
	) {
		return new LocalMempool(maxSize, hasher, counters, mempoolAddedCommandEventDispatcher, random);
	}
}