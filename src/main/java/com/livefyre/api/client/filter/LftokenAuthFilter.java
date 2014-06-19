package com.livefyre.api.client.filter;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import com.livefyre.core.LfCore;
import com.livefyre.core.Network;

public class LftokenAuthFilter implements ClientRequestFilter {
    private final LfCore core;
    private final String user;
    
    public LftokenAuthFilter(LfCore core, String user) {
        this.core = core;
        this.user = user;
    }
    
    public void filter(ClientRequestContext requestContext) throws IOException {
        String token = null;
        
        if (this.user != null) {
            try {
                Network network = (Network) core;
                token = network.buildUserAuthToken(this.user, "", Network.DEFAULT_EXPIRES);
            } catch (ClassCastException ex) {
                // that didn't work. REVERT REVERT!
            }
        }
        
        requestContext.getHeaders().add("Authorization", "lftoken " + (token == null ? core.buildLivefyreToken() : token));
    }
}
