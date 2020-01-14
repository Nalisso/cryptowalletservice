package com.crankoid.cryptowalletservice.wallet.internal.utilities;

import org.bitcoinj.core.NetworkParameters;

public interface NetworkStrategy {

    public NetworkParameters getNetwork();
}
