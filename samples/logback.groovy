import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import com.scalyr.logback.ScalyrAppender

appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%-4relative [%thread] - %msg%n"
  }
}
appender("SCALYR", ScalyrAppender) {
	apiKey = "YOUR_API_KEY_HERE"
    logfile = "myapp"
    parser = "logback"
}
root(WARN, ["CONSOLE", "SCALYR"])
