package org.radix.api.services;

import com.google.common.collect.EvictingQueue;
import com.radixdlt.common.Atom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.radixdlt.ledger.Ledger;
import com.radixdlt.ledger.LedgerCursor;
import com.radixdlt.ledger.LedgerIndex;
import com.radixdlt.ledger.LedgerSearchMode;
import com.radixdlt.middleware2.converters.AtomToBinaryConverter;
import com.radixdlt.middleware2.processing.RadixEngineAtomProcessor;
import com.radixdlt.middleware2.store.EngineAtomIndices;
import com.radixdlt.serialization.DsonOutput;
import com.radixdlt.serialization.Serialization;
import com.radixdlt.ledger.LedgerEntry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.radix.api.observable.AtomEventDto;
import org.radix.api.observable.AtomEventDto.AtomEventType;
import org.radix.api.AtomQuery;
import org.radix.api.observable.AtomEventObserver;
import org.radix.api.observable.Disposable;
import org.radix.api.observable.ObservedAtomEvents;
import org.radix.api.observable.Observable;
import org.radix.atoms.events.AtomEvent;
import org.radix.atoms.events.AtomExceptionEvent;
import org.radix.atoms.events.AtomStoredEvent;
import com.radixdlt.common.AID;
import org.radix.events.Events;
import org.radix.shards.Shards;

import static com.radixdlt.tempo.store.berkeley.LedgerEntryIndices.SHARD_INDEX_PREFIX;

public class AtomsService {
	private static int  NUMBER_OF_THREADS = 8;
	/**
	 * Some of these may block for a short while so keep a few.
	 * TODO: remove the blocking
	 */
	private final static ExecutorService executorService = Executors.newFixedThreadPool(
		NUMBER_OF_THREADS,
		new ThreadFactoryBuilder().setNameFormat("AtomsService-%d").build()
	);

	private final Set<AtomEventObserver> atomEventObservers = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final ConcurrentHashMap<AID, List<AtomStatusListener>> singleAtomObservers = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<AID, List<SingleAtomListener>> deleteOnEventSingleAtomObservers = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<AtomEventDto.AtomEventType, Long> atomEventCount = new ConcurrentHashMap<>();
	private final Serialization serialization = Serialization.getDefault();

	private final Object lock = new Object();
	private final EvictingQueue<String> eventRingBuffer = EvictingQueue.create(64);
	private final RadixEngineAtomProcessor radixEngineAtomProcessor;
	private final AtomToBinaryConverter atomToBinaryConverter;
	private final Ledger ledger;

	public AtomsService(Ledger ledger, RadixEngineAtomProcessor radixEngineAtomProcessor, AtomToBinaryConverter atomToBinaryConverter) {
		this.radixEngineAtomProcessor = Objects.requireNonNull(radixEngineAtomProcessor);
		this.ledger = Objects.requireNonNull(ledger);
		this.atomToBinaryConverter = Objects.requireNonNull(atomToBinaryConverter);

		Events.getInstance().register(AtomEvent.class, (event) -> {
			executorService.submit(() -> {
				if (!(event instanceof AtomEvent)) {
					return;
				}

				final AtomEvent atomEvent = (AtomEvent) event;
				final Atom atom = atomEvent.getAtom();

				// TODO: Clean this up
				final String eventName;

				if (event instanceof AtomStoredEvent) {
					final AtomStoredEvent storedEvent = (AtomStoredEvent) atomEvent;
					eventName = "STORED";

					this.atomEventObservers.forEach(observer -> observer.tryNext(storedEvent));

					List<SingleAtomListener> subscribers = this.deleteOnEventSingleAtomObservers.remove(atom.getAID());
					if (subscribers != null) {
						Iterator<SingleAtomListener> i = subscribers.iterator();
						if (i.hasNext()) {
							i.next().onStored(true);
							while (i.hasNext()) {
								i.next().onStored(false);
							}
						}
					}

					for (AtomStatusListener atomStatusListener : this.singleAtomObservers.getOrDefault(atom.getAID(), Collections.emptyList())) {
						atomStatusListener.onStored();
					}

					this.atomEventCount.merge(AtomEventType.STORE, 1L, Long::sum);
				} else {
					eventName = "UNKNOWN";
				}

				synchronized (lock) {
					eventRingBuffer.add(System.currentTimeMillis() + " " + eventName + " " + atomEvent.getAtom().getAID());
				}
			});
		});

		Events.getInstance().register(AtomExceptionEvent.class, event -> {
			executorService.submit(() -> {
				final AtomExceptionEvent exceptionEvent = (AtomExceptionEvent) event;
				synchronized (lock) {
					eventRingBuffer.add(
						System.currentTimeMillis() + " EXCEPTION " + exceptionEvent.getAtomId()
							+ " " + exceptionEvent.getException().getClass().getName());
				}

				List<SingleAtomListener> subscribers = this.deleteOnEventSingleAtomObservers.remove(exceptionEvent.getAtomId());
				if (subscribers != null) {
					Throwable exception = exceptionEvent.getException();
					subscribers.forEach(subscriber -> subscriber.onError(exceptionEvent.getAtomId(), exception));
				}

				for (AtomStatusListener singleAtomListener : this.singleAtomObservers.getOrDefault(exceptionEvent.getAtomId(), Collections.emptyList())) {
					Throwable exception = exceptionEvent.getException();
					singleAtomListener.onError(exception);
				}
			});
		});
	}

	/**
	 * Get a list of the most recent events
	 * @return list of event strings
	 */
	public List<String> getEvents() {
		synchronized (lock) {
			return new ArrayList<>(eventRingBuffer);
		}
	}

	public Map<AtomEventType, Long> getAtomEventCount() {
		return Collections.unmodifiableMap(atomEventCount);
	}

	public AID submitAtom(JSONObject atom, SingleAtomListener subscriber) {
		return radixEngineAtomProcessor.process(atom, Optional.of(new RadixEngineAtomProcessor.ProcessorAtomEventListener() {
			@Override
			public void onDeserializationCompleted(AID atomId) {
				if (subscriber != null) {
					deleteOnEventSingleAtomObservers.compute(atomId, (hid, oldSubscribers) -> {
						List<SingleAtomListener> subscribers = oldSubscribers == null ? new ArrayList<>() : oldSubscribers;
						subscribers.add(subscriber);
						return subscribers;
					});
				}
			}
		}));
	}

	public Disposable subscribeAtomStatusNotifications(AID aid, AtomStatusListener subscriber) {
		this.singleAtomObservers.compute(aid, (hid, oldSubscribers) -> {
			List<AtomStatusListener> subscribers = oldSubscribers == null ? new ArrayList<>() : oldSubscribers;
			subscribers.add(subscriber);
			return subscribers;
		});

		return () -> this.singleAtomObservers.get(aid).remove(subscriber);
	}

	public Observable<ObservedAtomEvents> getAtomEvents(AtomQuery atomQuery) {
		return observer -> {
			final AtomEventObserver atomEventObserver = new AtomEventObserver(atomQuery, observer, executorService, ledger, atomToBinaryConverter);
			atomEventObserver.start();
			this.atomEventObservers.add(atomEventObserver);

			return () -> {
				this.atomEventObservers.remove(atomEventObserver);
				atomEventObserver.cancel();
			};
		};
	}

	public long getWaitingCount() {
		return this.atomEventObservers.stream().map(AtomEventObserver::isDone).filter(done -> !done).count();
	}

	public JSONObject getAtomsByShardRange(String from, String to) throws JSONException {
		JSONObject result = new JSONObject();

		JSONArray array = new JSONArray();
		for (
				long shard = Shards.fromGroup(Integer.parseInt(from), (1 << 20)).getLow();
				shard < Shards.fromGroup(Integer.parseInt(to == null ? from : to), (1 << 20)).getHigh();
				shard++
		) {
			LedgerIndex fromIndex = LedgerIndex.from(EngineAtomIndices.toByteArray(SHARD_INDEX_PREFIX, shard));
			LedgerCursor searchResultCursor = ledger.search(LedgerIndex.LedgerIndexType.DUPLICATE, fromIndex, LedgerSearchMode.RANGE);
			AID atomId;
			while ((atomId = searchResultCursor.get()) != null) {
				array.put(atomId.toString());
			}
		}
		result.put("hids", array);
		return result;
	}

	public JSONObject getAtomsByAtomId(AID atomId) throws JSONException {
		Optional<LedgerEntry> ledgerEntryOptional = ledger.get(atomId);
		if (ledgerEntryOptional.isPresent()) {
			LedgerEntry ledgerEntry = ledgerEntryOptional.get();
			Atom atom = atomToBinaryConverter.toAtom(ledgerEntry.getContent());
			return serialization.toJsonObject(atom, DsonOutput.Output.API);
		}
		throw new RuntimeException("Atom not found");
	}
}
