package io.bootique.logback.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AsyncAppenderBase;
import ch.qos.logback.core.Context;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.bootique.config.PolymorphicConfiguration;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = ConsoleAppenderFactory.class)
public abstract class AppenderFactory implements PolymorphicConfiguration {

    private String logFormat;

    public AppenderFactory() {
        this.logFormat = "%-5p [%d{ISO8601,UTC}] %thread %c{20}: %m%n%rEx";
    }

    public void setLogFormat(String logFormat) {
        this.logFormat = logFormat;
    }

    /**
     * @since 0.12
     * @return configured log format
     */
    public String getLogFormat() {
        return logFormat;
    }

    public abstract Appender<ILoggingEvent> createAppender(LoggerContext context);

    protected PatternLayout createLayout(LoggerContext context) {
        PatternLayout layout = new PatternLayout();
        layout.setPattern(logFormat);
        layout.setContext(context);

        layout.start();
        return layout;
    }

    protected Appender<ILoggingEvent> asAsync(Appender<ILoggingEvent> appender) {
        return asAsync(appender, appender.getContext());
    }

    protected Appender<ILoggingEvent> asAsync(Appender<ILoggingEvent> appender, Context context) {
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setIncludeCallerData(false);
        asyncAppender.setQueueSize(AsyncAppenderBase.DEFAULT_QUEUE_SIZE);
        asyncAppender.setDiscardingThreshold(-1);
        asyncAppender.setContext(context);
        asyncAppender.setName(appender.getName());
        asyncAppender.addAppender(appender);
        asyncAppender.start();
        return asyncAppender;
    }
}
