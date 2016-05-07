Scalyr Logback Appender
---

This library provides a simple Appender implementation to send log messages to
the [Scalyr](https://www.scalyr.com) logging service using [logback](http://logback.qos.ch/) or [log4J](http://logging.apache.org/log4j/1.2/).
With this library, any Java code which uses the log4j or logback APIs can easily integrate with Scalyr.


### Using With Maven

Add the following dependency to your project's pom.xml (check [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cscalyr%20logback-log4j-appenders) for the latest version):

        <dependency>
            <groupId>com.scalyr</groupId>
            <artifactId>logback-log4j-appenders</artifactId>
            <version>6.0.0</version>
        </dependency>

**NOTE:** You must have either the logback or log4j dependencies in your project's pom.xml as well.


### Downloading JARs directly

1) Download the Java client library from [Maven Central](https://oss.sonatype.org/content/groups/public/com/scalyr/scalyr-client/6.0.0/scalyr-client-6.0.0.jar) and add it to your project.

2) Download the Appender library from [Maven Central](https://oss.sonatype.org/content/groups/public/com/scalyr/logback-log4j-appenders/6.0.0/logback-log4j-appenders-6.0.0.jar) and add it to your project.

3) 
  a) LogBack: In your logback configuration file, add a com.scalyr.logback.ScalyrAppender.
    See sample [logback.groovy](https://github.com/scalyr/scalyr-logback/samples/logback.groovy)
  b) Log4J: In your log4J configuration file, add a com.scalyr.log4j.ScalyrAppender.
    See sample [log4j.properties](https://github.com/scalyr/scalyr-logback/samples/log4j.properties)

4) Once you have log messages flowing into Scalyr, you can set up parsing rules. The easiest way to do that
is to go to https://www.scalyr.com/parsers?parser=logback and click the "Leave It to Us" button. This will
send a sample of your log data to the Scalyr staff, and we'll respond the same day with a custom-built parser.

See [src/test/java/com/scalyr/logback/test/Test.java](https://github.com/scalyr/scalyr-logback/blob/master/src/test/java/com/scalyr/logback/test/Test.java) for usage examples.
