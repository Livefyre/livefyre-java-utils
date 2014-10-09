package com.livefyre.api.filter;

import com.livefyre.core.LfCore;
import com.livefyre.utils.LivefyreUtil;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

public class LftokenAuthFilter extends ClientFilter {
    private final LfCore core;
    private final String userToken;
    
    public LftokenAuthFilter(LfCore core, String userToken) {
        this.core = core;
        this.userToken = userToken;
    }
    
    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        cr.getHeaders().add("Authorization", "lftoken " + (userToken == null ? LivefyreUtil.getNetworkFromCore(core).buildLivefyreToken() : userToken));
        
        return getNext().handle(cr);
    }
}
