package io.bitcoinsv.headerSV.config;

import io.bitcoinsv.jcl.net.network.PeerAddress;
import io.bitcoinsv.jcl.net.network.config.NetworkConfig;
import io.bitcoinsv.jcl.net.network.config.provided.NetworkDefaultConfig;
import io.bitcoinsv.jcl.net.protocol.config.ProtocolConfig;
import io.bitcoinsv.jcl.net.protocol.config.ProtocolConfigBuilder;
import io.bitcoinsv.bitcoinjsv.bitcoin.api.base.HeaderReadOnly;
import io.bitcoinsv.bitcoinjsv.params.*;
import io.bitcoinsv.headerSV.tools.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.naming.ConfigurationException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Distributed under the Open BSV software license, see the accompanying file LICENSE
 * Copyright (c) 2021 Bitcoin Association
 *
 * @author m.fletcher@nchain.com
 */
@Configuration
public class NetworkConfiguration {

    /* The JCL attempts to connect to the peers in batches. If 1/10 peers is a good peer, then it will take a long time to connect to 30
       peers if this number is low. */
    private final int NUMBER_OF_PEERS_TO_CONNECT_TO_EACH_BATCH = 300;
    private final String[] dns;
    private final List<PeerAddress> initialConnections = new ArrayList<>();

    private final ProtocolConfig protocolConfig;
    private final HeaderReadOnly genesisBlock;
    private final AbstractBitcoinNetParams networkParams;
    private final NetworkConfig jclNetworkConfig;


    public NetworkConfiguration(@Value("${headersv.network.networkId:}") String networkId,
                                @Value("${headersv.network.minPeers:5}") int minPeers,
                                @Value("${headersv.network.maxPeers:15}") int maxPeers,
                                @Value("${headersv.network.port:-1}") int port,
                                @Value("${headersv.network.dns:[]}") String[] dns,
                                @Value("${headersv.network.initialConnections}") String[] initialConnections) throws ConfigurationException {

        try {

        switch (networkId) {
            case "mainnet":
                genesisBlock = Util.GENESIS_BLOCK_HEADER_MAINNET;
                networkParams = new MainNetParams(Net.MAINNET);
                break;

            case "testnet":
                genesisBlock = Util.GENESIS_BLOCK_HEADER_TESTNET;
                networkParams = new TestNet3Params(Net.TESTNET3);
                break;

            case "stnnet":
                genesisBlock = Util.GENESIS_BLOCK_HEADER_STNNET;
                networkParams = new STNParams(Net.STN);
                break;

            case "regtest":
                genesisBlock = Util.GENESIS_BLOCK_HEADER_REGTEST;
                networkParams = new RegTestParams(Net.REGTEST);
                break;

            default:
                throw new ConfigurationException("Invalid configuration 'networkId'. Either 'mainnet', 'stnnet', 'regtest' or 'testnet'");

        }

            List<String> dnsList = new ArrayList<>(Arrays.asList(dns));

            //if there's any default dns, add them
            if(networkParams.getDnsSeeds() != null) {
                dnsList.addAll(Arrays.asList(networkParams.getDnsSeeds()));
            }

        //We might want to override the port if connecting to a custom network
        ProtocolConfig defaultConfig = ProtocolConfigBuilder.get(networkParams);
        int requiredPort = port == -1 ? defaultConfig.getBasicConfig().getPort() : port;

        protocolConfig = defaultConfig.toBuilder()
                .minPeers(minPeers)
                .maxPeers(maxPeers)
                .port(requiredPort)
                .discoveryConfig(defaultConfig.getDiscoveryConfig().toBuilder()
                        .recoveryHandshakeFrequency(Optional.of(Duration.ofSeconds(30)))
                        .recoveryHandshakeThreshold(Optional.of(Duration.ofSeconds(30)))
                        .dns(dnsList.toArray(new String[dnsList.size()]))
                        .build())
                .build();

        jclNetworkConfig = new NetworkDefaultConfig()
                .toBuilder()
                .maxSocketConnectionsOpeningAtSameTime(NUMBER_OF_PEERS_TO_CONNECT_TO_EACH_BATCH)
                .build();

            this.dns = dns;

            // We configure the list of initial Connections:
            if (initialConnections != null) {
                for(String peerAddress : initialConnections) {
                    this.initialConnections.add(PeerAddress.fromIp(peerAddress.trim()));
                }
            }

        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage());
        }

    }


    public ProtocolConfig getProtocolConfig(){
        return protocolConfig;
    }

    public NetworkConfig getJCLNetworkConfig(){
        return jclNetworkConfig;
    }

    public HeaderReadOnly getGenesisBlock(){
        return genesisBlock;
    }

    public AbstractBitcoinNetParams getNetworkParams() { return networkParams; }

    public String[] getDns() {
        return dns;
    }

    public List<PeerAddress> getInitialConnections() { return this.initialConnections;}
}
