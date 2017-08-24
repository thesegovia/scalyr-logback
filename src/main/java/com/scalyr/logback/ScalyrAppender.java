package com.scalyr.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.scalyr.api.logs.EventAttributes;
import com.scalyr.api.logs.Events;
import com.scalyr.util.Util;

/**
 * Created by steve on 4/8/14.
 */
public class ScalyrAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private String apiKey = "";
    private String serverHost = "";
    private String env = "";
    private Integer maxBufferRam;
    private String logfile = "logback";
    private String parser = "logback";
    private String extraAttributes = "";
    private Layout<ILoggingEvent> layout;

    @Override protected void append(ILoggingEvent event) {
        int level = event.getLevel().toInt();
        String message = layout.doLayout(event);

        if (level >= Level.ERROR_INT) {
            Events.error(new EventAttributes("message", message));
        } else if (level >= Level.WARN_INT) {
            Events.warning(new EventAttributes("message", message));
        } else if (level >= Level.INFO_INT) {
            Events.info(new EventAttributes("message", message));
        } else if (level >= Level.DEBUG_INT) {
            Events.fine(new EventAttributes("message", message));
        } else if (level >= Level.TRACE_INT) {
            Events.finer(new EventAttributes("message", message));
        } else {
            Events.finest(new EventAttributes("message", message));
        }
    }

    public String getApiKey() { return this.apiKey; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getServerHost() { return this.serverHost == null ? "" : this.serverHost.trim(); }

    public void setServerHost(String serverHost) { this.serverHost = serverHost; }

    public Integer getMaxBufferRam() { return maxBufferRam; }

    public void setMaxBufferRam(String maxBufferRam) { this.maxBufferRam = Util.stringToIntMemory(maxBufferRam); }

    public void setLogfile(String logfile) { this.logfile = logfile; }

    public String getLogfile() { return logfile; }

    public String getParser() { return parser; }

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

    public void setParser(String parser) { this.parser = parser; }

    public Layout<ILoggingEvent> getLayout() { return layout; }

    /**
     * Sets the Logback layout to use for this appender.  The default layout
     * consists of the first character of the level name (E, W, I, D, T for error,
     * warning, info, debug, and trace, respectively) followed by the message.
     *
     * @param layout the Layout to use
     */
    public void setLayout(Layout<ILoggingEvent> layout) { this.layout = layout; }

    @Override
    public void start() {
        if (layout == null) {
            //default layout
            layout = new PatternLayout();
            ((PatternLayout) layout).setPattern("%.-1level %msg");
        }

        final EventAttributes serverAttributes = new EventAttributes();
        if (getServerHost().length() > 0)
            serverAttributes.put("serverHost", getServerHost());
        serverAttributes.put("logfile", getLogfile());
        serverAttributes.put("parser", getParser());
        serverAttributes.put("env", getEnv());
        serverAttributes.addAll(Util.makeEventAttributesFromString(getExtraAttributes()));

        if(this.apiKey != null && !"".equals(this.apiKey.trim())) {
            // default to 4MB if not set.
            int maxBufferRam = (this.maxBufferRam != null) ? this.maxBufferRam : 4194304;
            Events.init(this.apiKey.trim(), maxBufferRam, null, serverAttributes);
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
