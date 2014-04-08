package com.scalyr.logback.test;

import ch.qos.logback.classic.LoggerContext;
import com.scalyr.api.logs.Events;
import com.scalyr.logback.ScalyrAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple test of Scalyr / logback integration.
 */
public class Test {
  private static Logger logger = LoggerFactory.getLogger("com.scalyr.logback.test.Test");

  /**
   * Insert a Scalyr "Write Logs" key here (see https://www.scalyr.com/keys).
   */
  private static final String API_KEY = "...";

  public static void main(String[] args) throws InterruptedException {
    // Create a ScalyrAppender, and attach it to our Logger. Note that in a real application, you would
    // generally do this in your logback configuration file, not in Java code.
    ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
    ScalyrAppender scalyrAppender = new ScalyrAppender();
    scalyrAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    scalyrAppender.start();
    logbackLogger.addAppender(scalyrAppender);

    // Initialize the "Events" (Scalyr logging) subsystem. Log a couple of messages, one before and one
    // after. The first message won't appear in Scalyr.
    logger.debug("Hello world (before).");
    int maxBufferRam = 4 * 1024 * 1024;
    Events.init(API_KEY, maxBufferRam);
    logger.debug("Hello world (after).");

    // Write some log messages from three parallel threads, to test threaded logging.
    TestThread[] threads = new TestThread[3];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new TestThread(i + 1);
      threads[i].start();
    }

    for (int i = 0; i < threads.length; i++) {
      threads[i].join();
    }

    Events.flush();
  }

  /**
   * Writes some messages to the log, spaced over 1.5 seconds, and then terminates.
   */
  private static class TestThread extends Thread {
    private final int threadIndex;

    private TestThread(int threadIndex) {
      this.threadIndex = threadIndex;
    }

    @Override public void run() {
      for (int i = 1; i <= 5; i++) {
        logger.warn("Thread " + threadIndex + ", message " + i + " (warn)");
        logger.info("Thread " + threadIndex + ", message " + i + " (info)");
        logger.debug("Thread " + threadIndex + ", message " + i + " (debug)");

        try {
          Thread.sleep(300);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }
    }
  }
}
