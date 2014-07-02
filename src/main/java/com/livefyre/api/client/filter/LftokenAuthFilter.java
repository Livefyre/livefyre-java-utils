package com.livefyre.api.client.filter;

import com.livefyre.core.LfCore;
import com.livefyre.core.Network;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

public class LftokenAuthFilter extends ClientFilter {
    private final LfCore core;
    private final String user;
    
    public LftokenAuthFilter(LfCore core, String user) {
        this.core = core;
        this.user = user;
    }
    
    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        String token = null;
        
        if (this.user != null) {
            try {
                Network network = (Network) core;
                token = network.buildUserAuthToken(this.user, "", Network.DEFAULT_EXPIRES);
            } catch (ClassCastException ex) {
                // that didn't work. REVERT REVERT!
            }
        }
        
        cr.getHeaders().add("Authorization", "lftoken " + (token == null ? core.buildLivefyreToken() : token));
        
        return getNext().handle(cr);
    }
}
