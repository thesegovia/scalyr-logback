Scalyr Logback Appender
---

This library provides a simple Appender implementation to send log messages to
the [Scalyr](https://www.scalyr.com) logging service using [logback](http://logback.qos.ch/).
With this library, any Java code which uses the log4j or logback APIs can easily integrate
with Scalyr.

To use this Appender:

1) Follow the brief "Project Setup" instructions at https://www.scalyr.com/help/java-api#setup to add the Scalyr
API to your project and initialize the API library.

When initializing the Scalyr API library (see previous link), you should add a tag identifying your log, so
that you will later be able to set up parsing rules. The following sample tags your log with parser=logback:

    int maxBufferRam = 4 * 1024 * 1024;
    Events.init("...your API key...", maxBufferRam,
      null, new EventAttributes("parser", "logback")
    );

2) Download [scalyrLogback.jar](https://github.com/scalyr/scalyr-logback/raw/master/ant_dist/scalyrLogback.jar)
and add it to your project.

3) In your loadback configuration file, add a ScalyrAppender.

Note that the Scalyr API library buffers messages for a few seconds before sending them to the Scalyr
server. As a result, the last few seconds may be dropped when your program exits. To avoid this,
insert a call to com.scalyr.api.logs.Events.flush() at program exit.

4) Once you have log messages flowing into Scalyr, you can set up parsing rules. The easiest way to do that
is to go to https://www.scalyr.com/parsers?parser=logback and click the "Leave It to Us" button. This will
send a sample of your log data to the Scalyr staff, and we'll respond the same day with a custom-built parser.

See [src/com/scalyr/logback/test/Test.java](https://github.com/scalyr/scalyr-logback/blob/master/src/com/scalyr/logback/test/Test.java) for usage examples.
