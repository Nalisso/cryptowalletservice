package com.crankoid.cryptowalletservice.resource.blockchain.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/blockchain")
public interface BlockchainResource {

    @GetMapping("/update")
    String updateBlockchainFile();

}
