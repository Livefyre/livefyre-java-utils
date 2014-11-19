package com.livefyre;

import com.livefyre.core.Network;

public class Livefyre {
    /* Private constructor to prevent instantiation. */
    private Livefyre() { }

    public static Network getNetwork(String networkName, String networkKey) {
        return Network.init(networkName, networkKey);
    }
}
