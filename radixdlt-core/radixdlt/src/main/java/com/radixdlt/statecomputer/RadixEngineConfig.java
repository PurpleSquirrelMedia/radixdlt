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

package com.radixdlt.statecomputer;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.radixdlt.consensus.bft.View;

// TODO: Move this info into genesis
public final class RadixEngineConfig {

	private RadixEngineConfig() {
		throw new IllegalStateException("Cannot instantiate.");
	}

	public static Module createModule(int minValidators, int maxValidators, long ceilingView) {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bindConstant().annotatedWith(MinValidators.class).to(minValidators);
				bindConstant().annotatedWith(MaxValidators.class).to(maxValidators);
				bind(View.class).annotatedWith(EpochCeilingView.class).toInstance(View.of(ceilingView));
			}
		};
	}
}