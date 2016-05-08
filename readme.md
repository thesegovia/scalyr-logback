Scalyr Logback/Log4J Appender
---

This library provides a simple Appender implementation to send log messages to
the [Scalyr](https://www.scalyr.com) logging service using [logback](http://logback.qos.ch/) or [log4J](http://logging.apache.org/log4j/1.2/).
With this library, any Java code which uses the log4j or logback APIs can easily integrate with Scalyr.


### Adding to your project

##### With Maven

Add the following dependency to your project's pom.xml (check [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cscalyr%20logback-log4j-appenders) for the latest version):

        <dependency>
            <groupId>com.scalyr</groupId>
            <artifactId>logback-log4j-appenders</artifactId>
            <version>6.0.0</version>
        </dependency>

**NOTE:** You'll also need the logback or log4j dependencies in your project's pom.xml as well (depending on which one you're using).

If you're using logback, you'll need logback-core, logback-classic, and logback-access (your version numbers may vary):

        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.1.7</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.1.7</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-access</artifactId>
            <version>1.1.7</version>
        </dependency>

For log4j, you'll need:

        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>

##### Downloading JARs directly

* Download the Java client library from [Maven Central](https://oss.sonatype.org/content/groups/public/com/scalyr/scalyr-client/6.0.0/scalyr-client-6.0.0.jar) and add it to your project.
* Download the Appender library from [Maven Central](https://oss.sonatype.org/content/groups/public/com/scalyr/logback-log4j-appenders/6.0.0/logback-log4j-appenders-6.0.0.jar) and add it to your project.
* Make sure you also have either logback or log4j jars in your project, as described in the section above.

### Configuration

##### Logback

In your logback configuration file, add a com.scalyr.logback.ScalyrAppender. 
See sample [logback.groovy](https://github.com/scalyr/scalyr-logback/blob/master/samples/logback.groovy)

##### Log4J

In your log4J configuration file, add a com.scalyr.log4j.ScalyrAppender.
See sample [log4j.properties](https://github.com/scalyr/scalyr-logback/blob/master/samples/log4j.properties)

### Parsing Rules

Once you have log messages flowing into Scalyr, you can set up parsing rules. The easiest way to do that
is to go to your [parsers page](https://www.scalyr.com/parsers), find the **logback** parser in the list, and click either the **Ask us to create for you** or **Ask us to edit for you** button. This will
send a sample of your log data to the Scalyr staff, and we'll respond the same day with a custom-built parser.

### Examples

See [src/test/java/com/scalyr/logback/test/Test.java](https://github.com/scalyr/scalyr-logback/blob/master/src/test/java/com/scalyr/logback/test/Test.java) for usage examples.
