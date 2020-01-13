package com.crankoid.cryptowalletservice.wallet.internal;

import com.crankoid.cryptowalletservice.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.wallet.api.dto.WalletInfoDTO;
import com.crankoid.cryptowalletservice.wallet.api.dto.WalletInfoInsecureDTO;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class WalletResourceImpl implements WalletResource {

    ECKey ecKey = null;

    @Override
    public WalletInfoInsecureDTO generateWallet(String userId) {
        WalletInfoInsecureDTO walletInfoInsecureDTO = new WalletInfoInsecureDTO();
        ecKey = new ECKey();
        walletInfoInsecureDTO.setPrivateKey(ecKey.getPrivateKeyAsHex());
        walletInfoInsecureDTO.setWalletInfoDTO(getWalletInformation(userId));
        return walletInfoInsecureDTO;
    }

    @Override
    public WalletInfoDTO getWalletInformation(String userId) {
        WalletInfoDTO walletInfoDTO = new WalletInfoDTO();
        ecKey = new ECKey();
        Address address = Address.fromKey(TestNet3Params.get(), ecKey, Script.ScriptType.P2WPKH);
        walletInfoDTO.setPublicKey(ecKey.getPublicKeyAsHex());
        walletInfoDTO.setAddress(address.getHash());
        walletInfoDTO.setAvailableSatoshis(0L);
        return walletInfoDTO;
    }
}
