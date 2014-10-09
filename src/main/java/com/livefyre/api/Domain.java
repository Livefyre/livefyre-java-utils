package com.livefyre.api;

import com.livefyre.core.LfCore;
import com.livefyre.core.Network;
import com.livefyre.utils.LivefyreUtil;

public class Domain {
    public static String quill(LfCore core) {
        Network network = LivefyreUtil.getNetworkFromCore(core);
        return network.isSsl() ? String.format("https://%s.quill.fyre.co",
                network.getNetworkName()) : String.format("http://quill.%s.fyre.co", network.getNetworkName());
    }
    
    public static String bootstrap(LfCore core) {
        Network network = LivefyreUtil.getNetworkFromCore(core);
        return network.isSsl() ? String.format("https://%s.bootstrap.fyre.co",
                network.getNetworkName()) : String.format("http://bootstrap.%s.fyre.co", network.getNetworkName());
    }
}
