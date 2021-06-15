/*
 * (C) Copyright 2021 Radix DLT Ltd
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

package com.radixdlt.api.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoMap;
import com.google.inject.multibindings.StringMapKey;
import com.radixdlt.api.Controller;
import com.radixdlt.api.JsonRpcHandler;
import com.radixdlt.api.controller.JsonRpcController;
import com.radixdlt.api.faucet.FaucetToken;
import com.radixdlt.api.handler.FaucetHandler;
import com.radixdlt.api.qualifier.NodeServer;
import com.radixdlt.api.qualifier.FaucetEndpoint;
import com.radixdlt.api.server.JsonRpcServer;
import com.radixdlt.atom.Substate;
import com.radixdlt.atommodel.tokens.state.TokenResource;
import com.radixdlt.constraintmachine.REInstruction;
import com.radixdlt.constraintmachine.TxnParseException;
import com.radixdlt.engine.parser.REParser;
import com.radixdlt.identifiers.REAddr;
import com.radixdlt.ledger.VerifiedTxnsAndProof;
import com.radixdlt.statecomputer.checkpoint.Genesis;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FaucetEndpointModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(FaucetHandler.class).in(Scopes.SINGLETON);
	}

	@NodeServer
	@ProvidesIntoMap
	@StringMapKey("/faucet")
	public Controller faucetController(@FaucetEndpoint JsonRpcServer jsonRpcServer) {
		return new JsonRpcController(jsonRpcServer);
	}

	@FaucetEndpoint
	@Provides
	public JsonRpcServer jsonRpcServer(@FaucetEndpoint Map<String, JsonRpcHandler> handlers) {
		return new JsonRpcServer(handlers);
	}

	@FaucetEndpoint
	@ProvidesIntoMap
	@StringMapKey("faucet.request_tokens")
	public JsonRpcHandler faucetRequestTokens(FaucetHandler faucetHandler) {
		return faucetHandler::requestTokens;
	}

	@Provides
	@FaucetToken
	@Singleton
	public Set<REAddr> tokens(
		REParser parser,
		@Genesis VerifiedTxnsAndProof genesis
	) {
		return genesis.getTxns().stream()
			.flatMap(txn -> {
				try {
					var parsed = parser.parse(txn);
					return parsed.instructions().stream()
						.map(REInstruction::getData)
						.filter(Substate.class::isInstance)
						.map(Substate.class::cast)
						.map(Substate::getParticle)
						.filter(TokenResource.class::isInstance)
						.map(TokenResource.class::cast)
						.map(TokenResource::getAddr);
				} catch (TxnParseException e) {
					throw new IllegalStateException(e);
				}
			}).collect(Collectors.toSet());
	}
}
