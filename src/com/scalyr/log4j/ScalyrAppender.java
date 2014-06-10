package com.autotec.thirdparty;

import ch.qos.logback.classic.Level;
import com.scalyr.api.logs.EventAttributes;
import com.scalyr.api.logs.Events;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class ScalyrAppenderLog4J extends AppenderSkeleton {
    private String apiKey;
    private Integer maxBufferRam;

    @Override
    protected void append(LoggingEvent event) {
        int level = event.getLevel().toInt();
        String message = event.getRenderedMessage();

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
    public void activateOptions() {
        if(this.apiKey != null && !"".equals(this.apiKey.trim())) {
            // default to 4MB if not set.
            int maxBufferRam = (this.maxBufferRam != null) ? this.maxBufferRam : 4194304;
            Events.init(this.apiKey.trim(), maxBufferRam);
        } else {
            errorHandler.error("Cannot initialize logging.  No Scalyr API Key has been set.");
        }
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getMaxBufferRam() {
        return maxBufferRam;
    }

    public void setMaxBufferRam(String maxBufferRam) {
        if(maxBufferRam != null && !"".equals(maxBufferRam)) {
            maxBufferRam = maxBufferRam.toLowerCase().trim();
            if(maxBufferRam.contains("m")) {
                this.maxBufferRam = Integer.valueOf(maxBufferRam.substring(0, maxBufferRam.indexOf("m"))) * 4194304;
            } else if (maxBufferRam.contains("k")) {
                this.maxBufferRam = Integer.valueOf(maxBufferRam.substring(0, maxBufferRam.indexOf("k"))) * 1024;
            } else {
                this.maxBufferRam = Integer.valueOf(maxBufferRam);
            }
        }
    }

    @Override
    public void close() {
        if(this.closed) {
            return;
        }
        Events.flush();
        this.closed = true;
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
