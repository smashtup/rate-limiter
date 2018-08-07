# RateLimiter (Java)

A simple and rudimentary Java library to help provide an interface for rate limiting to your java application. Interface can be extended with
own rate limiting strategies as well as storage for rate limiting state. Using the In Memory storage limits this library to a single application server.
Please look into implementing a store based on a central storage such as Redis for clustered support.

Supported Rate Limit Strategies:

- Fixed Window

Supported Rate Limit Data Storage:

- In Memory Concurrent Map

## Getting Started

Source for this project can be cloned from: https://github.com/smashtup/rate-limiter.git

Alternatively you can just download the latest jar file from: https://github.com/smashtup/rate-limiter/releases 
copy this to a folder in the classpath of your project.

### Prerequisites

Library is built and tested using latest version of gradle on Java 8. 

* Internet access to https://search.maven.org/

* [Install Gradle](https://gradle.org/install/)

* [Install Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

* Familiarity with [Project Lombok](https://projectlombok.org/). This library is used to help make writing java code more productive and readable with the use of java annotations. 

### Building

Assuming you have successfully installed gradle and Java SDK, that is both tools are available in your global path. 

Using your favourite command line tool, change directory to where you cloned the rate-limiter project.

Simply build the project by: 

```
gradle build
```

This will build the project and create a jar file in the following subdirectory:
 
```
./build/libs/rate-limiter-1.X-SNAPSHOT.jar
``` 

## Running the tests

Following on from Building, Tests can be re-executed exclusively using:

```
gradle clean test
```

## Using the library

### Quick Start

Use the RateLimitHandler preferrably at the first point a new request hits your application server. Each request received is can be allocated a RateLimit object by registering the request. Use the RateLimit object to test whether the request should be limited or not.

An example of a request id could be the IP Address the request is comming from or alternatively a security token id, username etc. 
```java
RateLimitHandler rateLimitHandler = RateLimitHandler.getInstance();
RateLimit rateLimit = rateLimitHandler.registerRequest("SOME UNIQUE REQUESTER_IDENTIFIER");
``` 

Verify if the current request is limited

```java
rateLimit.isRateLimited()
```

### Rate Limit Configuration

The RateLimitHandler default configuration consists:

```$xslt
request limit = 100
duration = 1 Hour
rate limit strategy = Fixed Window
rate limit data store = In Memory
trim interval = 10 Minutes
trim age = 3 Hours
```

To override the configuration create an instance of RateLimitConfig and the update the Handler with the new config. ie. to override the duration to 60 seconds for all new RateLimit objects:

```java
RateLimitConfig rateLimitConfig = RateLimitConfig.builder()
                .durationMs(60000)
                .build();
try {
    RateLimitHandler rateLimitHandler = RateLimitHandler.getInstance().updateConfig(rateLimitConfig);
}catch (InvalidRateLimitConfigException e) {
    // Handle invalid config
}
```

All time parameters are interpreted as Milliseconds. 

### Example

The following is simple single threaded example of how to use the RateLimiter. Otherwise please refer to the tests for more inspiration.

```java
import au.com.unsol.rateLimiter.InvalidRateLimitConfigException;
import au.com.unsol.rateLimiter.RateLimit;
import au.com.unsol.rateLimiter.RateLimitConfig;
import au.com.unsol.rateLimiter.RateLimitHandler;

import static au.com.unsol.rateLimiter.RateLimitConfig.MILLISECONDS_IN_SECOND;
import static java.lang.Thread.sleep;

public class RateLimitSample {

    public static void main(String[] args) throws Exception {
        RateLimitConfig rateLimitConfig = RateLimitConfig.builder()
                .requestLimit(100)
                .durationMs(10 * MILLISECONDS_IN_SECOND)
                .build();
        try {
            RateLimitHandler rateLimitHandler = RateLimitHandler.getInstance().updateConfig(rateLimitConfig);
            while(true) {
                RateLimit rateLimit = rateLimitHandler.registerRequest("SOME UNIQUE IDENTIFIER");
                if (rateLimit.isRateLimited()) {
                    System.out.println("Oh no we're rate limited.");
                } else {
                    System.out.println("We're not rate limited just yet. Keep going.");
                }
                sleep(10);
            }

        }catch (InvalidRateLimitConfigException e) {
            System.out.println("Invalid Config Used for RateLimitHandler.");
            e.printStackTrace();
        }
    }
}
```

## Authors

* **Michael Coleman**

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

Inspiration derived from:

* https://stripe.com/blog/rate-limiters

* https://konghq.com/blog/how-to-design-a-scalable-rate-limiting-algorithm/
