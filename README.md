# Livefyre Java Utility Classes
[![GitHub version](https://badge.fury.io/gh/livefyre%2Flivefyre-java-utils.png)](http://badge.fury.io/gh/livefyre%2Flivefyre-java-utils)

Livefyre's official library for common server-side tasks necessary for getting Livefyre apps (comments, reviews, etc.) working on your website.

Works with Java 1.5 and later.

## Installation

Add this dependency to your project's POM:

    <dependency>
      <groupId>com.livefyre</groupId>
      <artifactId>utils</artifactId>
      <version>1.1.3</version>
    </dependency>

## Usage

Instantiating a network object:

```Java
Network network = Livefyre.getNetwork("networkName", "networkKey");
```

Building a Livefyre token:

```Java
network.buildLivefyreToken();
```

Building a user auth token:

```Java
network.buildUserAuthToken("userId", "displayName", expires);
```

To validate a Livefyre token:

```Java
network.validateLivefyreToken("lfToken");
```

To send Livefyre a user sync url and then have Livefyre pull user data from that url:

```Java
network.setUserSyncUrl("urlTemplate");
network.syncUser("userId");
```

Instantiating a site object:

```Java
Site site = network.getSite("siteId", "siteKey");
```

Building a collection meta token:
*The "tags" and "stream" arguments are optional.*

```Java
site.buildCollectionMetaToken("title", "articleId", "url", "tags", "stream");
```

Building a checksum:
*The "tags" argument is optional.*

```Java
site.buildChecksum("title", "url", "tags");
```

To retrieve content collection data:

```Java
site.getCollectionContent("articleId");

// The following method does the same as previous, but provides the data as a JsonObject.
JsonObject jsonObject = site.getCollectionContentJson("articleId");
```

To get a content collection's id:

```Java
site.getCollectionId("articleId");
```

## Testing

You must have Maven installed. To run the tests, simply run `mvn test`. You can run particular tests by passing `-D test=Class#method` -- for example, `-D test=NetworkTest#testNetworkCreation`.

## Documentation

Located [here](http://answers.livefyre.com/developers/libraries).

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

Note: any feature update on any of Livefyre's libraries will need to be reflected on all libraries. We will try and accommodate when we find a request useful, but please be aware of the time it may take.

## License

MIT
