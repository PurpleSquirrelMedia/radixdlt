/*
 * (C) Copyright 2021 Radix DLT Ltd
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
 *
 */

package com.radixdlt.atom.actions;

import com.radixdlt.atom.TxAction;
import com.radixdlt.atom.TxBuilder;
import com.radixdlt.atom.TxBuilderException;
import com.radixdlt.atommodel.validators.ValidatorParticle;

import java.util.Optional;

public final class RegisterAsValidator implements TxAction {
	@Override
	public void execute(TxBuilder txBuilder) throws TxBuilderException {
		var address = txBuilder.getAddressOrFail("Must have an address to register.");

		txBuilder.swap(
			ValidatorParticle.class,
			p -> p.getAddress().equals(address) && !p.isRegisteredForNextEpoch(),
			Optional.of(new ValidatorParticle(address, false)),
			"Already a validator"
		).with(
			substateDown -> new ValidatorParticle(address, true, substateDown.getName(), substateDown.getUrl())
		);
	}
}