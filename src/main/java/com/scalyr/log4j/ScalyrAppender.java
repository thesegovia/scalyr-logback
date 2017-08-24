package com.scalyr.log4j;

import com.scalyr.api.logs.EventAttributes;
import com.scalyr.api.logs.Events;
import com.scalyr.util.Util;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Map;

public class ScalyrAppender extends AppenderSkeleton {
    private String apiKey;
    private String serverHost = "";
    private String env = "";
    private String logfile = "log4j";
    private String parser = "log4j";
    private String extraAttributes = "";
    private Integer maxBufferRam;

    public String getServerHost() { return this.serverHost == null ? "" : this.serverHost.trim(); }

    public void setServerHost(String serverHost) { this.serverHost = serverHost; }

    public String getLogfile() { return logfile; }

    public void setLogfile(String logfile) { this.logfile = logfile; }

    public String getParser() { return parser; }

    public void setParser(String parser) { this.parser = parser; }

    public void setEnv(String env) { this.env = env; }

    public String getEnv() { return env; }

    /**
     * Use this to describe any additional server attributes. Takes a string that is kv pairs separated by a comma e.g.:
     * <pre>
     *   appName=appofdoom,zodiac=rooster
     * </pre>
     * An example of what to put in your logback.xml:
     <pre><code>
     &lt;configuration&gt;
       &lt;appender name="scalyr" class="com.scalyr.logback.ScalyrAppender"&gt;
         &lt;apiKey&gt;YOUR KEY&lt;/apiKey&gt;
         &lt;extraAttributes&gt;appName=appofdoom,zodiac=rooster&lt;/extraAttributes&gt;
         ...
       &lt;/appender&gt;
     &lt;/configuration&gt;
     </code></pre>
     * <p>
     * The extraAttributes will be appended to the serverAttributes list and will be searchable in scalyr by a query like:
     * <pre>
     *   $zodiac == "rooster" "hello world"
     * </pre>
     *
     * @param extraAttributes String of key-value pairs
     */
    public void setExtraAttributes(String extraAttributes) { this.extraAttributes = extraAttributes; }

    public String getExtraAttributes() { return extraAttributes; }

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
        if (this.apiKey != null && !"".equals(this.apiKey.trim())) {
            final EventAttributes serverAttributes = new EventAttributes();
            if (getServerHost().length() > 0)
                serverAttributes.put("serverHost", getServerHost());
            serverAttributes.put("logfile", getLogfile());
            serverAttributes.put("parser", getParser());
            serverAttributes.put("env", getEnv());
            serverAttributes.addAll(Util.makeEventAttributesFromString(getExtraAttributes()));

            // default to 4MB if not set.
            int maxBufferRam = (this.maxBufferRam != null) ? this.maxBufferRam : 4194304;
            Events.init(this.apiKey.trim(), maxBufferRam, null, serverAttributes);
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
      this.maxBufferRam = Util.stringToIntMemory(maxBufferRam);
    }

    @Override
    public void close() {
        if (this.closed) {
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
