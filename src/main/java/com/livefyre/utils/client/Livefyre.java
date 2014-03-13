package com.livefyre.utils.client;


public class Livefyre {

    /* Private construction to prevent instantiation. */
    private Livefyre() {
    }

    /**
     * Returns an instance of a Livefyre network object.
     * 
     * @param networkName Livefyre-provided Network name, e.g., "labs.fyre.co".
     * @param networkKey The Livefyre-provided key/id for the website or application the collection belongs
     *            to, e.g., "303617"
     * @return a Network object
     */
    public static Network getNetwork(String networkName, String networkKey) {
        return new Network(networkName, networkKey);
    }
}
