package com.livefyre.api.client.filter;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import com.livefyre.core.Network;

public class LftokenAuthFilter implements ClientRequestFilter {
    private final Network network;
    
    public LftokenAuthFilter(Network network) {
        this.network = network;
    }
    
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add("Authorization", "lftoken " + network.buildLivefyreToken());
    }
}
