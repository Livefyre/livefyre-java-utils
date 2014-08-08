package com.livefyre.api.client;

import com.livefyre.core.LfCore;
import com.livefyre.core.Network;
import com.livefyre.core.Site;

public class Domain {
    public static String quill(LfCore core) {
        Network network = getNetworkFromCore(core);
        return network.isSsl() ? String.format("https://%s.quill.fyre.co", network.getNetworkName()) : String.format("http://quill.%s", network.getName());
    }
    
    public static String bootstrap(LfCore core) {
        Network network = getNetworkFromCore(core);
        return network.isSsl() ? "https://bootstrap.livefyre.com" : String.format("http://bootstrap.%s", network.getName());
    }

    private static Network getNetworkFromCore(LfCore core) {
        if (core.getClass().equals(Network.class)) {
            return (Network) core;
        } else {
            return ((Site) core).getNetwork();
        }
    }
}
