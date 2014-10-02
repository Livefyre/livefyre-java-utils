package com.livefyre.api;

import com.livefyre.core.Collection;
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
        return network.isSsl() ? String.format("https://%s.bootstrap.fyre.co", network.getNetworkName()) : String.format("http://bootstrap.%s", network.getName());
    }

    private static Network getNetworkFromCore(LfCore core) {
        if (core.getClass().equals(Network.class)) {
            return (Network) core;
        } else if (core.getClass().equals(Site.class)) {
            return ((Site) core).getNetwork();
        } else {
            return ((Collection) core).getSite().getNetwork();
        }
    }
}