/*
 *
 *  * (C) Copyright 2020 Radix DLT Ltd
 *  *
 *  * Radix DLT Ltd licenses this file to you under the Apache License,
 *  * Version 2.0 (the "License"); you may not use this file except in
 *  * compliance with the License.  You may obtain a copy of the
 *  * License at
 *  *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  * either express or implied.  See the License for the specific
 *  * language governing permissions and limitations under the License.
 *
 */

package com.radixdlt.crypto.hdwallet;
import com.google.common.annotations.VisibleForTesting;
import com.radixdlt.SecurityCritical;
import com.radixdlt.crypto.CryptoException;
import com.radixdlt.crypto.ECKeyPair;
import com.radixdlt.utils.Bytes;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.bitcoinj.core.Utils.WHITESPACE_SPLITTER;

@SecurityCritical({ SecurityCritical.SecurityKind.KEY_GENERATION })
public final class HDWalletProviderBitcoinJ implements HDWallet {

	/**
	 * A BIP32 extended root key.
	 */
	private final DeterministicKey bip32ExtendedRootKey;

	private final DeterministicHierarchy deterministicHierarchy;

	@VisibleForTesting
	HDWalletProviderBitcoinJ(DeterministicKey bip32ExtendedRootKey) {
		this.bip32ExtendedRootKey = bip32ExtendedRootKey;
		this.deterministicHierarchy = new DeterministicHierarchy(bip32ExtendedRootKey);
	}

	public HDWalletProviderBitcoinJ(byte[] seed) {
		this(HDKeyDerivation.createMasterPrivateKey(seed));
	}

	public HDWalletProviderBitcoinJ(String seedHex) {
		this(Bytes.fromHexString(seedHex));
	}

	public HDWalletProviderBitcoinJ(List<String> mnemonicWords, String passphrase) {
		this(MnemonicCode.toSeed(mnemonicWords, passphrase));
	}

	public HDWalletProviderBitcoinJ(List<String> mnemonicWords) {
		this(mnemonicWords, "");
	}

	public HDWalletProviderBitcoinJ(String mnemonicString, String passphrase) {
		this(WHITESPACE_SPLITTER.splitToList(mnemonicString), passphrase);
	}

	public static HDWalletProviderBitcoinJ mnemonicNoPassphrase(String mnemonicString) {
		return new HDWalletProviderBitcoinJ(mnemonicString, "");
	}

	private static ChildNumber childNumberFromString(String component) {
		boolean isHardened = false;
		if (component.endsWith("'") || component.endsWith("H")) {
			component = component.substring(0, component.length() - 1);
			isHardened = true;
		}
		int componentInt = Integer.parseInt(component);
		return new ChildNumber(componentInt, isHardened);
	}

	private static List<ChildNumber> hdPathFromString(String path) {
		if (path.startsWith("m/")) {
			path = path.substring(2);
		}
		String[] pathComponentsAsStrings = path.split("/");
		return Arrays.stream(pathComponentsAsStrings).map(HDWalletProviderBitcoinJ::childNumberFromString).collect(Collectors.toList());
	}

	private DeterministicKey deriveKeyForPath(List<ChildNumber> path) {
		return deterministicHierarchy.deriveChild(
				path.subList(0, path.size() - 1),
				false,
				true,
				path.get(path.size() - 1)
		);
	}

	@VisibleForTesting
	String rootPrivateKeyHex() {
		return Bytes.toHexString(bip32ExtendedRootKey.getPrivKeyBytes());
	}

	@VisibleForTesting
	String rootPublicKeyHex() {
		return Bytes.toHexString(bip32ExtendedRootKey.getPubKey());
	}

	public HDKeyPair deriveKeyAtPath(String path) {
		List<ChildNumber> hdPath = hdPathFromString(path);
		DeterministicKey childKey = deriveKeyForPath(hdPath);
		try {
			ECKeyPair ecKeyPair = new ECKeyPair(childKey.getPrivKeyBytes());
			return new HDKeyPair(ecKeyPair, path);
		} catch (CryptoException e) {
			throw new IllegalStateException("Failed to generate ECKeyPair", e);
		}
	}

}
