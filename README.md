# Livefyre Java Utility Classes

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
      <version>1.0.0</version>
    </dependency>

Usage
=====

Creating tokens:

**User auth token:**
```java
public String example() {
	return Livefyre.getNetwork("networkName", "networkKey").getUserAuthToken("userId", "displayName", "timeTillExpire");
}
```

**Collection meta token:**
```java
public String example() {
	Network network = Livefyre.getNetwork("networkName", "networkKey");
	return network.getSite("siteId", "siteKey").getCollectionMetaToken("title", "articleId", "url", "tags");
}
```

**You can also use the LivefyreJwtUtil class to create and decode tokens.*

To validate a Livefyre token:
```java
public boolean example() {
	return Livefyre.getNetwork("networkName", "networkKey").validateLivefyreToken("lfToken");
}
```


To send Livefyre a user sync url and then have Livefyre pull user data from that url:

```java
public void example() {
	Network network = Livefyre.getNetwork("networkName", "networkKey");
    network.setUserSyncUrl("http://thisisa.test.url/{id}/");
    network.syncUser("system");
}
```
        
To retrieve content collection data as a string and json object from Livefyre (note that both are in JSON, but the latter is encapsulated in a JsonObject):

```java
public void example() {
    Site site = Livefyre.getNetwork("networkName", "networkKey").getSite("siteId", "siteSecret");
    String content = site.getCollectionContent("articleId");

    JsonObject jsonObject = site.getCollectionContentJson("articleId");
}
```

Testing
=======

You must have Maven installed. To run the tests, simply run `mvn test`. You can run particular tests by passing `-D test=Class#method` -- for example, `-D test=NetworkTest#testNetworkCreation`.