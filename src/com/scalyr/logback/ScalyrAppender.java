package com.scalyr.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.scalyr.api.logs.EventAttributes;
import com.scalyr.api.logs.Events;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Logback appender that sends log messages to the Scalyr API.
 */
public class ScalyrAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    /**
     * Scalyr's log message length limit
     */
    private static final int MAX_LENGTH = 3500;

    private String continuationPrefix = "<CONT> ";
    private String apiKey = "";
    private String serverHost = "";
    private Integer maxBufferRam;
    private Layout<ILoggingEvent> layout;

    /**
     * Splits a log message up into chunks, attempting to split on line boundaries if possible.
     * Chunks after the first one are prefixed with the continuation prefix ("<CONT> " by default) to
     * make it easy to reconstruct the original message.
     */
    private List<String> toChunks(String message) {
        String remainder;

        if (message.startsWith(continuationPrefix) || message.startsWith("\\")) {
            // Disambiguate continuation lines
            remainder = "\\" + message;
        } else {
            remainder = message;
        }

        List<String> chunks = new LinkedList<>();

        while (remainder.length() > MAX_LENGTH) {
            int splitPos = remainder.lastIndexOf('\n', MAX_LENGTH);
            if (splitPos >= 0) {
                chunks.add(remainder.substring(0, splitPos));
                remainder = continuationPrefix + remainder.substring(splitPos + 1);
            } else {
                chunks.add(remainder.substring(0, MAX_LENGTH));
                remainder = continuationPrefix + remainder.substring(MAX_LENGTH);
            }
        }

        chunks.add(remainder);
        return chunks;
    }

    @Override
    protected void append(ILoggingEvent event) {
        int level = event.getLevel().toInt();
        Map<String, String> properties = event.getMDCPropertyMap();

        String message = layout.doLayout(event);

        // Scalyr doesn't currently allow the thread name to be queried, so add it as an attribute.
        EventAttributes extraAttributes = new EventAttributes("threadName", event.getThreadName());

        // If there are MDC properties (authentication principal, etc.), add them as attributes too.
        if (properties != null) {
            for (Map.Entry<String, String> propertyEntry : properties.entrySet()) {
                extraAttributes.put(propertyEntry.getKey(), propertyEntry.getValue());
            }
        }

        for (String chunk : toChunks(message)) {
            EventAttributes eventAttributes = new EventAttributes("message", chunk);
            eventAttributes.addAll(extraAttributes);

            if (level >= Level.ERROR_INT) {
                Events.error(eventAttributes);
            } else if (level >= Level.WARN_INT) {
                Events.warning(eventAttributes);
            } else if (level >= Level.INFO_INT) {
                Events.info(eventAttributes);
            } else if (level >= Level.DEBUG_INT) {
                Events.fine(eventAttributes);
            } else if (level >= Level.TRACE_INT) {
                Events.finer(eventAttributes);
            } else {
                Events.finest(eventAttributes);
            }
        }
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getServerHost() {
        return this.serverHost == null ? "" : this.serverHost.trim();
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getMaxBufferRam() {
        return maxBufferRam;
    }

    public void setMaxBufferRam(String maxBufferRam) {
        if(maxBufferRam != null && !"".equals(maxBufferRam)) {
            maxBufferRam = maxBufferRam.toLowerCase().trim();
            if(maxBufferRam.contains("m")) {
                this.maxBufferRam = Integer.valueOf(maxBufferRam.substring(0, maxBufferRam.indexOf("m"))) * 1048576;
            } else if (maxBufferRam.contains("k")) {
                this.maxBufferRam = Integer.valueOf(maxBufferRam.substring(0, maxBufferRam.indexOf("k"))) * 1024;
            } else {
                this.maxBufferRam = Integer.valueOf(maxBufferRam);
            }
        }
    }

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    /**
     * Sets the Logback layout to use for this appender. The default is a simple layout consisting of
     * the first character of the level name (E, W, I, D, T for error, warning, info, debug, and
     * trace, respectively) followed by the message.
     */
    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }

    public String getContinuationPrefix() {
        return continuationPrefix;
    }

    /**
     * Sets the prefix to be placed at the front of any log message that is a continuation of the
     * previous ones. This happens when a log message is split into multiple pieces to fit within
     * Scalyr's log message length limit. The default is "<CONT> ".
     */
    public void setContinuationPrefix(String continuationPrefix) {
        this.continuationPrefix = continuationPrefix;
    }

    @Override
    public void start() {
        if (layout == null) {
            // Use a simple default layout.
            layout = new PatternLayout();
            ((PatternLayout) layout).setPattern("%.-1level %msg");
        }

        final EventAttributes serverAttributes = new EventAttributes();
        if (getServerHost().length() > 0)
            serverAttributes.put("serverHost", getServerHost());
        serverAttributes.put("logfile", "logback");
        serverAttributes.put("parser", "logback");

        if(this.apiKey != null && !"".equals(this.apiKey.trim())) {
            // default to 4MB if not set.
            int maxBufferRam = (this.maxBufferRam != null) ? this.maxBufferRam : 4194304;
            Events.init(this.apiKey.trim(), maxBufferRam, null,
              serverAttributes);
            super.start();
        } else {
            addError("Cannot initialize logging.  No Scalyr API Key has been set.");
        }
    }

    @Override public void stop() {
        Events.flush();
        super.stop();
    }
}
