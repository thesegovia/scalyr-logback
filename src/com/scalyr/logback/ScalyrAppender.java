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
    private String apiKey = "";
    private String serverHost = "";
    private Integer maxBufferRam;

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

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getServerHost() {
        return this.serverHost;
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

    @Override
    public void start() {
        if(this.apiKey != null && !"".equals(this.apiKey.trim())) {
            // default to 4MB if not set.
            int maxBufferRam = (this.maxBufferRam != null) ? this.maxBufferRam : 4194304;
            Events.init(this.apiKey.trim(), maxBufferRam, null,
            	new EventAttributes("serverHost", this.serverHost.trim()));
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
