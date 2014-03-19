# Livefyre Java Utility Classes

This is Livefyre's official utility for Java users.  These classes provide a simple way to create the tokens necessary for interacting with the Livefyre API, and they also provide some methods to assist with accessing some common endpoints.

Requirements
============

Works with Java 1.5 and later.

Installation
============

### Maven users

Add this dependency to your project's POM:

    <dependency>
      <groupId>com.livefyre</groupId>
      <artifactId>utils</artifactId>
      <version>1.0.3</version>
    </dependency>

Usage
=====

Creating tokens:

**User auth token:**
```java
Livefyre.getNetwork(String networkName, String networkKey).buildUserAuthToken(String userId, String displayName, double timeTillExpire);
```

**Collection meta token:**
```java
Network network = Livefyre.getNetwork(String networkName, String networkKey);
network.getSite(String siteId, String siteKey).buildCollectionMetaToken(String title, String articleId, String url, String tags);
```

**You can also use the LivefyreJwtUtil class to create and decode tokens.*

To validate a Livefyre token:
```java
Livefyre.getNetwork(String networkName, String networkKey).validateLivefyreToken(String lfToken);
```

To send Livefyre a user sync url and then have Livefyre pull user data from that url:

```java
Network network = Livefyre.getNetwork(String networkName, String networkKey);
network.setUserSyncUrl(String url);
network.syncUser(String userId);
```
        
To retrieve content collection data as a string and json object from Livefyre (note that both are in JSON, but the latter is encapsulated in a JsonObject):

```java
Site site = Livefyre.getNetwork(String networkName, String networkKey).getSite(String siteId, String siteSecret);
String content = site.getCollectionContent(String articleId);

JsonObject jsonObject = site.getCollectionContentJson(String articleId);
```

Testing
=======

You must have Maven installed. To run the tests, simply run `mvn test`. You can run particular tests by passing `-D test=Class#method` -- for example, `-D test=NetworkTest#testNetworkCreation`.