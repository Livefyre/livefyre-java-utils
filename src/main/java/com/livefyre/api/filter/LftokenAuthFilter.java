package com.livefyre.api.filter;

import com.livefyre.core.LfCore;
import com.livefyre.utils.LivefyreUtil;
import java.io.IOException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class LftokenAuthFilter implements ClientRequestFilter {
    private final LfCore core;
    private final String userToken;
    
    public LftokenAuthFilter(LfCore core, String userToken) {
        this.core = core;
        this.userToken = userToken;
    }
    
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        final String lftoken = "lftoken " + (userToken == null ? LivefyreUtil.getNetworkFromCore(core).buildLivefyreToken() : userToken);
        requestContext.getHeaders().add("Authorization", lftoken);
    }
}
