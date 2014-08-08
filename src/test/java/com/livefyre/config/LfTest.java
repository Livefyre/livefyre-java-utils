package com.livefyre.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        setPropValues(LfEnvironments.PROD);
    }
    
    public void setPropValues(LfEnvironments env) {
        Properties prop = new Properties();
        String prefix = env.toString();
 
        try {
            InputStream inputStream = new FileInputStream("test.properties");
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
