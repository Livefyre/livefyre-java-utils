# Livefyre Java Utility Classes

Livefyre's official library for common server-side tasks necessary for getting Livefyre apps (comments, reviews, etc.) working on your website.

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
```Java
Livefyre.getNetwork("networkName", "networkKey").buildUserAuthToken("userId", "displayName", double timeTillExpire);
```

**Collection meta token:**
```Java
Network network = Livefyre.getNetwork("networkName", "networkKey");
network.getSite("siteId", "siteKey").buildCollectionMetaToken("title", "articleId", "url", "tags");
```

**You can also use the LivefyreJwtUtil class to create and decode tokens.*

To validate a Livefyre token:
```Java
Livefyre.getNetwork("networkName", "networkKey").validateLivefyreToken("lfToken");
```

To send Livefyre a user sync url and then have Livefyre pull user data from that url:

```Java
Network network = Livefyre.getNetwork("networkName", "networkKey");
network.setUserSyncUrl("url");
network.syncUser("userId");
```
        
To retrieve content collection data as a and json object from Livefyre (note that both are in JSON, but the latter is encapsulated in a JsonObject):

```Java
Site site = Livefyre.getNetwork("networkName", "networkKey").getSite("siteId", "siteSecret");
content = site.getCollectionContent("articleId");

JsonObject jsonObject = site.getCollectionContentJson("articleId");
```

Testing
=======

You must have Maven installed. To run the tests, simply run `mvn test`. You can run particular tests by passing `-D test=Class#method` -- for example, `-D test=NetworkTest#testNetworkCreation`.

License
=======

MIT