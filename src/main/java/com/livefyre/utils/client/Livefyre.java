package com.livefyre.utils.client;


public class Livefyre {

    /* Private construction to prevent instantiation. */
    private Livefyre() {
    }

    public static Network getNetwork(String networkName, String networkKey) {
        return new Network(networkName, networkKey);
    }
}
