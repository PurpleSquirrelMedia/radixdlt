package com.radix.regression;

import com.radixdlt.client.application.RadixApplicationAPI;
import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.translate.tokens.CreateTokenAction;
import com.radixdlt.client.application.translate.tokens.TokenUnitConversions;
import com.radixdlt.client.core.Bootstrap;
import com.radixdlt.client.core.RadixUniverse;
import com.radixdlt.client.core.network.actions.SubmitAtomAction;
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy;
import io.reactivex.observers.TestObserver;
import java.math.BigDecimal;
import org.junit.BeforeClass;
import org.junit.Test;

public class MintTooManyTokensOfDifferentTypesTest {
	@BeforeClass
	public static void setup() {
		if (!RadixUniverse.isInstantiated()) {
			RadixUniverse.bootstrap(Bootstrap.BETANET);
		}
	}

	@Test
	public void given_a_token_with_max_supply_created_in_one_account__when_another_token_with_max_supply_is_created_in_another_account__then_the_client_should_be_notified_of_success() {
		RadixApplicationAPI api0 = RadixApplicationAPI.create(RadixIdentities.createNew());
		RadixApplicationAPI api1 = RadixApplicationAPI.create(RadixIdentities.createNew());

		createToken(api0)
			.awaitCount(4, TestWaitStrategy.SLEEP_100MS, 10000)
			.assertSubscribed()
			.assertNoTimeout()
			.assertNoErrors();

		createToken(api1)
			.awaitCount(4, TestWaitStrategy.SLEEP_100MS, 10000)
			.assertSubscribed()
			.assertNoTimeout()
			.assertNoErrors();
	}

	private static TestObserver createToken(RadixApplicationAPI api) {
		TestObserver<SubmitAtomAction> observer = new TestObserver<>();
		api.createToken(
			"TestToken",
			"TEST",
			"TestToken",
			BigDecimal.valueOf(2).pow(256).subtract(BigDecimal.ONE).scaleByPowerOfTen(-18),
			TokenUnitConversions.getMinimumGranularity(),
			CreateTokenAction.TokenSupplyType.MUTABLE
		)
			.toObservable()
			.doOnNext(System.out::println)
			.subscribe(observer);

		return observer;
	}
}
