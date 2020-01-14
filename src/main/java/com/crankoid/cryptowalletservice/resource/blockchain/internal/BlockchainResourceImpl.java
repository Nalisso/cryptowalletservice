package com.crankoid.cryptowalletservice.resource.blockchain.internal;

import com.crankoid.cryptowalletservice.resource.blockchain.api.BlockchainResource;
import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockchainResourceImpl implements BlockchainResource {

    private final BlockchainService blockchainService;

    public BlockchainResourceImpl(BlockchainService blockchainService){
        this.blockchainService = blockchainService;
    }

    @Override
    public String updateBlockchainFile() {
        blockchainService.updateBlockchainFile();
        return "Local blockchain updated";
    }
}
