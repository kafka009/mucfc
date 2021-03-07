package com.mucfc.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.OutputStreamAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.CompositeFilter;
import org.apache.logging.log4j.core.filter.DenyAllFilter;
import org.apache.logging.log4j.core.filter.DynamicThresholdFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 *
 */
public class JobLogFactory {
    private JobLogFactory() {
    }

    public static void start(String pkg, OutputStream outputStream) {
        // 有mdc，无mdc需要都允许
        Filter filter1 = DynamicThresholdFilter.createFilter("name", new KeyValuePair[]{new KeyValuePair("kafka", "INFO")}, Level.OFF, Filter.Result.ACCEPT, Filter.Result.DENY);
        Filter filter2 = DenyAllFilter.newBuilder().build();
        Filter filter = CompositeFilter.createFilters(new Filter[]{filter1, filter2});

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss:SSS}] [%p] - %l - %m%n").build();
        Appender appender = OutputStreamAppender.createAppender(layout, filter, outputStream, pkg, true, true);
        appender.start();
        config.addAppender(appender);
        System.out.println(config.getAppenders());

        LoggerConfig loggerConfig = config.getLoggerConfig(pkg);
        loggerConfig.addAppender(appender, null, filter);
        System.out.println(loggerConfig.getAppenders());
    }

    public static void stop(String pkg) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        config.getAppender(pkg).stop();
        config.getLoggerConfig(pkg).removeAppender(pkg);
    }

    // 必须代理一个outputStream，异常时候干掉它
    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger("com");
        OutputStream outputStream = new FileOutputStream("D:\\sdf.txt");
        start("com.mucfc", outputStream);
        logger.info("不会打印的");
        MDC.put("name", "kafka");
        logger.info("这里会打印的");
        MDC.put("name", "cha");
        logger.info("不会打印的");
        outputStream.flush();
        outputStream.close();

        logger.info("不会打印的");
        MDC.put("name", "kafka");
        logger.info("这里会打印的");
        MDC.put("name", "cha");
        logger.info("不会打印的");

        stop("com.mucfc");
    }
}