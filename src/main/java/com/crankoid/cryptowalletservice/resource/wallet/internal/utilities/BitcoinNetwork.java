package com.crankoid.cryptowalletservice.resource.wallet.internal.utilities;

import org.bitcoinj.core.NetworkParameters;

public class BitcoinNetwork {

    private static final NetworkStrategy networkStrategy = new TestNetworkStrategy();

    public static NetworkParameters get(){
        return networkStrategy.getNetwork();
    }

}
