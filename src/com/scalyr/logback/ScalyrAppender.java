package com.scalyr.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.scalyr.api.logs.EventAttributes;
import com.scalyr.api.logs.Events;

/**
 * Created by steve on 4/8/14.
 */
public class ScalyrAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  @Override protected void append(ILoggingEvent event) {
    int level = event.getLevel().toInt();
    String message = event.getFormattedMessage();

    if (level >= Level.ERROR_INT) {
      Events.error(new EventAttributes("message", "E " + message));
    } else if (level >= Level.WARN_INT) {
      Events.warning(new EventAttributes("message", "W " + message));
    } else if (level >= Level.INFO_INT) {
      Events.info(new EventAttributes("message", "I " + message));
    } else if (level >= Level.DEBUG_INT) {
      Events.fine(new EventAttributes("message", "J " + message));
    } else if (level >= Level.TRACE_INT) {
      Events.finer(new EventAttributes("message", "K " + message));
    } else {
      Events.finest(new EventAttributes("message", "L " + message));
    }
  }

  @Override public void stop() {
    Events.flush();

    super.stop();
  }
}
