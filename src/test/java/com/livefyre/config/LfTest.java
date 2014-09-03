package com.livefyre.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class LfTest {
    protected String NETWORK_NAME = "<NETWORK-NAME>";
    protected String NETWORK_KEY = "<NETWORK-KEY>";
    protected String SITE_ID = "<SITE-ID>";
    protected String SITE_KEY = "<SITE-KEY>";
    protected String COLLECTION_ID = "<COLLECTION-ID>";
    protected String USER_ID = "<USER-ID>";
    protected String ARTICLE_ID = "<ARTICLE-ID>";
    
    public LfTest() {
        // For local dev testing
        setPropValues(LfEnvironments.PROD);

        // CIRCLE CI
        try {
            Map<String, String> env = System.getenv();
            
            NETWORK_NAME = env.get("NETWORK_NAME");
            NETWORK_KEY = env.get("NETWORK_KEY");
            SITE_ID = env.get("SITE_ID");
            SITE_KEY = env.get("SITE_KEY");
            COLLECTION_ID = env.get("COLLECTION_ID");
            USER_ID = env.get("USER_ID");
            ARTICLE_ID = env.get("ARTICLE_ID");
        } catch (NullPointerException e) {
            System.err.println("Variables haven't been set anywhere!");
        }
    }
    
    public void setPropValues(LfEnvironments env) {
        try {
            String prefix = env.toString();
            InputStream inputStream = new FileInputStream("test.properties");
            Properties prop = new Properties();

            prop.load(inputStream);
            NETWORK_NAME = prop.getProperty(prefix+"NETWORK_NAME");
            NETWORK_KEY = prop.getProperty(prefix+"NETWORK_KEY");
            SITE_ID = prop.getProperty(prefix+"SITE_ID");
            SITE_KEY = prop.getProperty(prefix+"SITE_KEY");
            COLLECTION_ID = prop.getProperty(prefix+"COLLECTION_ID");
            USER_ID = prop.getProperty(prefix+"USER_ID");
            ARTICLE_ID = prop.getProperty(prefix+"ARTICLE_ID");
        } catch (IOException e) {
            System.err.println("Issue loading file.");
        }
    }
    
    public enum LfEnvironments {
        QA ("qa."),
        UAT ("uat."),
        PROD ("prod.");
        
        String env;
        
        private LfEnvironments(String env) {
            this.env = env;
        }
        
        @Override
        public String toString() {
            return env;
        }
    }
}
