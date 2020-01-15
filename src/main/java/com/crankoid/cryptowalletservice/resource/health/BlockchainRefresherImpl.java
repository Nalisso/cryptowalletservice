package com.crankoid.cryptowalletservice.resource.health;

import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/blockchain-utils")
public class BlockchainRefresherImpl {

    private final BlockchainService blockchainService;

    BlockchainRefresherImpl(BlockchainService blockchainService){
        this.blockchainService = blockchainService;
    }

    @GetMapping("/force-update")
    public void forceUpdateBlockchain(){
        blockchainService.replayBlockchain(null, null);
    }
}
