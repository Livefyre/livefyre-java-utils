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
    protected String USER_ID = "<USER-ID>";
    protected String ARTICLE_ID = "<ARTICLE-ID>";
    protected final static String TITLE = "JavaTest";
    protected final static String URL = "http://answers.livefyre.com/JAVA";
    protected final static Double DEFAULT_EXPIRES = 86400.00;
    
    public LfTest() {
        setPropValues(LfEnvironments.PROD);
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
            USER_ID = prop.getProperty(prefix+"USER_ID");
            ARTICLE_ID = prop.getProperty(prefix+"ARTICLE_ID");
            return;
        } catch (IOException e) {
            // file missing. continue.
        }
        
     // CIRCLE CI
        try {
            Map<String, String> sys = System.getenv();
            
            NETWORK_NAME = sys.get("NETWORK_NAME");
            NETWORK_KEY = sys.get("NETWORK_KEY");
            SITE_ID = sys.get("SITE_ID");
            SITE_KEY = sys.get("SITE_KEY");
            USER_ID = sys.get("USER_ID");
            ARTICLE_ID = sys.get("ARTICLE_ID");
        } catch (NullPointerException e) {
            System.err.println("Variables haven't been set anywhere!");
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
