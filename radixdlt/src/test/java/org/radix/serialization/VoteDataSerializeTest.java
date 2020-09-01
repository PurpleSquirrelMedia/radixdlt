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

package org.radix.serialization;

import com.radixdlt.consensus.CommandOutput;
import com.radixdlt.consensus.VoteData;
import com.radixdlt.consensus.bft.View;
import com.radixdlt.consensus.CommandHeader;
import com.radixdlt.crypto.Hash;

public class VoteDataSerializeTest extends SerializeObject<VoteData> {
	public VoteDataSerializeTest() {
		super(VoteData.class, VoteDataSerializeTest::get);
	}

	private static VoteData get() {
		View view = View.of(1234567890L);

		CommandOutput commandOutput = CommandOutput.create(0, 0L, false);
		CommandHeader committed = new CommandHeader(0, view, Hash.random(), Hash.random(), commandOutput);
		CommandHeader parent = new CommandHeader(0, view.next(), Hash.random(), Hash.random(), commandOutput);
		CommandHeader proposed = new CommandHeader(0, view.next().next(), Hash.random(), Hash.random(), commandOutput);
		return new VoteData(proposed, parent, committed);
	}
}