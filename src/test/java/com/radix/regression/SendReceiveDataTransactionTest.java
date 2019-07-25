package com.radix.regression;

import com.radixdlt.client.core.RadixEnv;
import io.reactivex.disposables.Disposable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.radixdlt.client.application.RadixApplicationAPI;
import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.translate.Action;
import com.radixdlt.client.application.translate.ActionExecutionException;
import com.radixdlt.client.application.translate.data.DecryptedMessage;
import com.radixdlt.client.application.translate.data.DecryptedMessage.EncryptionState;
import com.radixdlt.client.application.translate.data.SendMessageAction;
import com.radixdlt.client.atommodel.accounts.RadixAddress;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy;

/**
 * RLAU-162, RLAU-88, RLAU-89
 */
public class SendReceiveDataTransactionTest {
	@Test
	public void given_an_account_owner_listening_to_own_messages__when_owner_sends_a_message_from_another_account_to_itself__then_the_client_should_be_notified_of_error_and_not_receive_any_message() throws Exception {

		// Given account owner listening to own messages
		RadixApplicationAPI api = RadixApplicationAPI.create(RadixEnv.getBootstrapConfig(), RadixIdentities.createNew());
		TestObserver<DecryptedMessage> messageListener = TestObserver.create(Util.loggingObserver("MessageListener"));
		Disposable d = api.pull();
		api.observeMessages().subscribe(messageListener);

		// When owner sends message from another account
		RadixAddress sourceAddress = api.getAddress(RadixIdentities.createNew().getPublicKey());
		Action sendMessageAction = SendMessageAction.create(sourceAddress, api.getAddress(), new byte[] {0}, false);
		Completable sendMessageStatus = api.execute(sendMessageAction).toCompletable();

		// Then client should be notified of error
		TestObserver<Object> submissionObserver = TestObserver.create(Util.loggingObserver("Submission"));
		sendMessageStatus.subscribe(submissionObserver);
		submissionObserver.awaitTerminalEvent();
		submissionObserver.assertError(ActionExecutionException.class);

		// And not receive any messages
		messageListener.await(30, TimeUnit.SECONDS);
		messageListener
			.assertNoErrors()
			.assertEmpty()
			.dispose();

		d.dispose();
	}

	@Test
	public void given_two_account_owners_listening_to_own_messages__when_one_sends_a_message_to_the_other__then_both_should_receive_message() {

		// Given two account owners listening to own messages
		TestObserver<DecryptedMessage> messageListener1 = new TestObserver<>(Util.loggingObserver("MessageListener1"));
		TestObserver<DecryptedMessage> messageListener2 = new TestObserver<>(Util.loggingObserver("MessageListener2"));
		RadixApplicationAPI api1 = RadixApplicationAPI.create(RadixEnv.getBootstrapConfig(), RadixIdentities.createNew());
		RadixApplicationAPI api2 = RadixApplicationAPI.create(RadixEnv.getBootstrapConfig(), RadixIdentities.createNew());
		api1.observeMessages().subscribe(messageListener1);
		api2.observeMessages().subscribe(messageListener2);

		Disposable d1 = api1.pull();
		Disposable d2 = api2.pull();

		// When one sends a message to the other
		byte[] message = new byte[] {1, 2, 3, 4};
		Completable sendMessageStatus = api1.sendMessage(api2.getAddress(), message, false).toCompletable();

		// Then both owners should receive the message
		sendMessageStatus.blockingAwait();
		messageListener1.awaitCount(1, TestWaitStrategy.SLEEP_10MS, 30000)
			.assertValueAt(0, msg -> Arrays.equals(message, msg.getData()))
			.assertValueAt(0, msg -> msg.getFrom().equals(api1.getAddress()))
			.assertValueAt(0, msg -> msg.getTo().equals(api2.getAddress()))
			.assertValueAt(0, msg -> msg.getEncryptionState().equals(EncryptionState.NOT_ENCRYPTED))
			.dispose();
		messageListener2.awaitCount(1, TestWaitStrategy.SLEEP_10MS, 30000)
			.assertValueAt(0, msg -> Arrays.equals(message, msg.getData()))
			.assertValueAt(0, msg -> msg.getFrom().equals(api1.getAddress()))
			.assertValueAt(0, msg -> msg.getTo().equals(api2.getAddress()))
			.assertValueAt(0, msg -> msg.getEncryptionState().equals(EncryptionState.NOT_ENCRYPTED))
			.dispose();
		d1.dispose();
		d2.dispose();
	}

	@Test
	public void given_an_account_owner_listening_to_own_messages__when_owner_sends_message_to_itself__then_owner_should_receive_message() {

		// Given an account owner listening to own messages
		TestObserver<DecryptedMessage> messageListener = new TestObserver<>(Util.loggingObserver("MessageListener"));
		RadixApplicationAPI api = RadixApplicationAPI.create(RadixEnv.getBootstrapConfig(), RadixIdentities.createNew());
		Disposable d = api.pull();
		api.observeMessages().subscribe(messageListener);

		// When owner sends message to himself
		byte[] message = new byte[] {1, 2, 3, 4};
		Completable sendMessageStatus = api.sendMessage(message, false).toCompletable();

		// Then owner should receive the message
		sendMessageStatus.blockingAwait();
		messageListener.awaitCount(1, TestWaitStrategy.SLEEP_10MS, 30000)
			.assertValueAt(0, msg -> Arrays.equals(message, msg.getData()))
			.assertValueAt(0, msg -> msg.getFrom().equals(api.getAddress()))
			.assertValueAt(0, msg -> msg.getTo().equals(api.getAddress()))
			.assertValueAt(0, msg -> msg.getEncryptionState().equals(EncryptionState.NOT_ENCRYPTED))
			.dispose();
		d.dispose();
	}

	@Test
	public void given_a_client_listening_to_messages_in_another_account__when_other_account_sends_message_to_itself__then_client_should_receive_message() {

		// Given a client listening to messages in another account
		TestObserver<DecryptedMessage> clientListener = new TestObserver<>(Util.loggingObserver("MessageListener"));
		RadixApplicationAPI clientApi = RadixApplicationAPI.create(RadixEnv.getBootstrapConfig(), RadixIdentities.createNew());
		RadixApplicationAPI otherAccount = RadixApplicationAPI.create(RadixEnv.getBootstrapConfig(), RadixIdentities.createNew());
		clientApi.observeMessages(otherAccount.getAddress()).subscribe(clientListener);
		Disposable d = clientApi.pull(otherAccount.getAddress());

		// When the other account sends message to itself
		byte[] message = new byte[] {1, 2, 3, 4};
		Completable sendMessageStatus = otherAccount.sendMessage(message, false).toCompletable();

		// Then client should receive the message
		sendMessageStatus.blockingAwait();
		clientListener.awaitCount(1, TestWaitStrategy.SLEEP_10MS, 30000)
			.assertValueAt(0, msg -> Arrays.equals(message, msg.getData()))
			.assertValueAt(0, msg -> msg.getFrom().equals(otherAccount.getAddress()))
			.assertValueAt(0, msg -> msg.getTo().equals(otherAccount.getAddress()))
			.assertValueAt(0, msg -> msg.getEncryptionState().equals(EncryptionState.NOT_ENCRYPTED))
			.dispose();

		d.dispose();
	}
}
