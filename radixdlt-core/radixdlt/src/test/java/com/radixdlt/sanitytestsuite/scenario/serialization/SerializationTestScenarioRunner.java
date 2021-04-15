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

package com.radixdlt.sanitytestsuite.scenario.serialization;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.radixdlt.DefaultSerialization;
import com.radixdlt.atommodel.system.SystemParticle;
import com.radixdlt.atommodel.tokens.StakedTokensParticle;
import com.radixdlt.atommodel.tokens.TokenDefinitionParticle;
import com.radixdlt.atommodel.tokens.TokensParticle;
import com.radixdlt.atommodel.validators.ValidatorParticle;
import com.radixdlt.atomos.RRIParticle;
import com.radixdlt.sanitytestsuite.scenario.SanityTestScenarioRunner;
import com.radixdlt.sanitytestsuite.utility.ArgumentsExtractor;
import com.radixdlt.serialization.DsonOutput;
import com.radixdlt.serialization.Serialization;
import com.radixdlt.utils.JSONFormatter;
import org.bouncycastle.util.encoders.Hex;

import java.util.Map;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class SerializationTestScenarioRunner extends SanityTestScenarioRunner<SerializationTestVector> {

    private final Serialization serialization = DefaultSerialization.getInstance();

    private final Map<String, Function<Map<String, Object>, Object>> constructorMap;

    public SerializationTestScenarioRunner() {
        Map<String, Function<Map<String, Object>, Object>> mutableMap = Maps.newHashMap();
        mutableMap.put("radix.particles.transferrable_tokens", SerializationTestScenarioRunner::makeTransferrableTokensParticle);
        mutableMap.put("radix.particles.fixed_supply_token_definition", SerializationTestScenarioRunner::makeFixedSupplyTokenDefinitionParticle);
        mutableMap.put("radix.particles.staked_tokens", SerializationTestScenarioRunner::makeStakedTokensParticle);
        mutableMap.put("radix.particles.rri", SerializationTestScenarioRunner::makeRRIParticle);
        mutableMap.put("radix.particles.registered_validator", SerializationTestScenarioRunner::makeRegisteredValidatorParticle);
        mutableMap.put("radix.particles.system_particle", SerializationTestScenarioRunner::makeSystemParticle);
        constructorMap = ImmutableMap.copyOf(mutableMap);
    }

    @Override
    public String testScenarioIdentifier() {
        return "serialization_radix_models";
    }

    @Override
    public Class<SerializationTestVector> testVectorType() {
        return SerializationTestVector.class;
    }

    private static TokenDefinitionParticle makeFixedSupplyTokenDefinitionParticle(final Map<String, Object> arguments) {
        var argsExtractor = ArgumentsExtractor.from(arguments);
        var ttp = new TokenDefinitionParticle(
                argsExtractor.asRRI("rri"),
                argsExtractor.asString("name"),
                argsExtractor.asString("description"),
                argsExtractor.asString("iconUrl"),
                argsExtractor.asString("url"),
                argsExtractor.asUInt256("supply")
            );
        assertTrue(argsExtractor.isFinished());
        return ttp;
    }

    private static StakedTokensParticle makeStakedTokensParticle(final Map<String, Object> arguments) {
        var argsExtractor = ArgumentsExtractor.from(arguments);
        var ttp = new StakedTokensParticle(
            argsExtractor.asRadixAddress("delegateAddress"),
            argsExtractor.asRadixAddress("address"),
            argsExtractor.asUInt256("amount")
        );

        assertTrue(argsExtractor.isFinished());
        return ttp;
    }

    private static TokensParticle makeTransferrableTokensParticle(final Map<String, Object> arguments) {
        var argsExtractor = ArgumentsExtractor.from(arguments);
        var ttp = new TokensParticle(
            argsExtractor.asRadixAddress("address"),
            argsExtractor.asUInt256("amount"),
            argsExtractor.asRRI("tokenDefinitionReference"),
    true
        );
        assertTrue(argsExtractor.isFinished());
        return ttp;
    }

    private static RRIParticle makeRRIParticle(final Map<String, Object> arguments) {
        var argsExtractor = ArgumentsExtractor.from(arguments);
        var rrip = new RRIParticle(argsExtractor.asRRI("rri"));
        assertTrue(argsExtractor.isFinished());
        return rrip;
    }

    private static ValidatorParticle makeRegisteredValidatorParticle(final Map<String, Object> arguments) {
        var argsExtractor = ArgumentsExtractor.from(arguments);
        var rvp = new ValidatorParticle(
            argsExtractor.asRadixAddress("address"), true
        );
        assertTrue(argsExtractor.isFinished());
        return rvp;
    }

    private static SystemParticle makeSystemParticle(final Map<String, Object> arguments) {
        var argsExtractor = ArgumentsExtractor.from(arguments);
        var sp = new SystemParticle(
                argsExtractor.asLong("epoch"),
                argsExtractor.asLong("view"),
                argsExtractor.asLong("timestamp")
        );
        assertTrue(argsExtractor.isFinished());
        return sp;
    }

    @Override
    public void doRunTestVector(SerializationTestVector testVector) throws AssertionError {
        var produced = ofNullable(constructorMap.get(testVector.input.typeSerialization))
                .map(constructor -> constructor.apply(testVector.input.arguments))
                .map(model -> {
                    String expectedDsonAllHex = testVector.expected.dson.get("all").toString();
                    String dsonAllHex = Hex.toHexString(serialization.toDson(model, DsonOutput.Output.ALL));
                    assertEquals("DSON (all) mismatch", expectedDsonAllHex, dsonAllHex);
                    return model;
                })
                .map(model -> serialization.toJson(model, DsonOutput.Output.HASH))
                .map(JSONFormatter::sortPrettyPrintJSONString)
                .orElseThrow(() -> new IllegalStateException("Cant find constructor for " + testVector.input.typeSerialization));
        String expected = JSONFormatter.sortPrettyPrintJSONString(testVector.expected.jsonPrettyPrinted);
        assertEquals(expected, produced);
    }

}