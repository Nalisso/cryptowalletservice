package com.crankoid.cryptowalletservice.resource.wallet.internal.utilities;

import org.bitcoinj.core.NetworkParameters;

interface NetworkStrategy {

    NetworkParameters getNetwork();
}
