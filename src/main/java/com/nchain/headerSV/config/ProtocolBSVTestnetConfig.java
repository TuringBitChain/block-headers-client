package com.nchain.headerSV.config;

import com.nchain.jcl.net.network.config.NetworkConfig;
import com.nchain.jcl.net.protocol.config.*;
import com.nchain.jcl.net.protocol.handlers.blacklist.BlacklistHandlerConfig;
import com.nchain.jcl.net.protocol.handlers.block.BlockDownloaderHandlerConfig;
import com.nchain.jcl.net.protocol.handlers.discovery.DiscoveryHandlerConfig;
import com.nchain.jcl.net.protocol.handlers.handshake.HandshakeHandlerConfig;
import com.nchain.jcl.net.protocol.handlers.message.MessageHandlerConfig;
import com.nchain.jcl.net.protocol.handlers.pingPong.PingPongHandlerConfig;

/*
 *   @author m.jose
 *
 *  Copyright (c) 2018-2020 nChain Ltd
 *  @date 14/09/2020, 12:02
 */
public class ProtocolBSVTestnetConfig  extends ProtocolConfigBase implements ProtocolConfig {

    private static String id = "BSV [Test Net]";
    private static long magicPackage = 0x0b110907;
    private static int services;
    private static int port;
    private static int protocolVersion;
    private static String[] userAgentBlacklist;
    private static String[] userAgentWhitelist;
    private static String[] dns;


    public ProtocolBSVTestnetConfig() {
        super(id, magicPackage, services, port, protocolVersion, userAgentBlacklist, userAgentWhitelist, dns, (Integer)null, (Integer)null, (new ProtocolBasicConfig()).toBuilder().id(id).magicPackage(magicPackage).servicesSupported(services).port(port).protocolVersion(protocolVersion).build(), (NetworkConfig)null, (MessageHandlerConfig)null, (new HandshakeHandlerConfig()).toBuilder().userAgentBlacklistPatterns(userAgentBlacklist).userAgentWhitelistPatterns(userAgentWhitelist).build(), (PingPongHandlerConfig)null, (new DiscoveryHandlerConfig()).toBuilder().dnsSeeds(dns).build(), (BlacklistHandlerConfig)null, (BlockDownloaderHandlerConfig)null);
    }

    public String getId() {
        return id;
    }

    static {
        services = ProtocolServices.NODE_BLOOM.getProtocolServices();
        port = 18333;
        protocolVersion = ProtocolVersion.CURRENT.getBitcoinProtocolVersion();
        userAgentBlacklist = new String[]{"Bitcoin ABC:", "BUCash:"};
        userAgentWhitelist = new String[]{"Bitcoin SV:", "/bitcoinj-sv:0.0.7/"};
        dns = new String[] {
                "testnet-seed.bitcoin.jonasschnelli.ch", // Jonas Schnelli
                "seed.tbtc.petertodd.org",               // Peter Todd
                "seed.testnet.bitcoin.sprovoost.nl",     // Sjors Provoost
                "testnet-seed.bluematt.me",              // Matt Corallo
                "bitcoin-testnet.bloqseeds.net",         // Bloq
        };
    }
}
