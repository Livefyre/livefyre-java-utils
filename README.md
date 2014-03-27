# Livefyre Java Utility Classes
[![GitHub version](https://badge.fury.io/gh/livefyre%2Flivefyre-java-utils.png)](http://badge.fury.io/gh/livefyre%2Flivefyre-java-utils)

Livefyre's official library for common server-side tasks necessary for getting Livefyre apps (comments, reviews, etc.) working on your website.

Works with Java 1.5 and later.

## Installation

Add this dependency to your project's POM:

    <dependency>
      <groupId>com.livefyre</groupId>
      <artifactId>utils</artifactId>
      <version>1.0.3</version>
    </dependency>

## Usage

Creating tokens:

**Livefyre token:**

```Java
Network network = Livefyre.getNetwork("networkName", "networkKey").buildLfToken()
```

**User auth token:**

```Java
Livefyre.getNetwork("networkName", "networkKey").buildUserAuthToken("userId", "displayName", double timeTillExpire);
```

**Collection meta token:**

```Java
Network network = Livefyre.getNetwork("networkName", "networkKey");
network.getSite("siteId", "siteKey").buildCollectionMetaToken("title", "articleId", "url", "tags");
```

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

## Testing

You must have Maven installed. To run the tests, simply run `mvn test`. You can run particular tests by passing `-D test=Class#method` -- for example, `-D test=NetworkTest#testNetworkCreation`.

## Documentation

Located [here](http://answers.livefyre.com/libraries).

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

Note: any feature update on any of Livefyre's libraries will need to be reflected on all libraries. We will try and accommodate when we find a request useful, but please be aware of the time it may take.

## License

MIT