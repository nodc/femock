package com.bpc.femock.netty.pipeline;

import com.bpc.femock.ConnectorConfiguration;
import com.bpc.femock.ConnectorConfigurer;
import com.bpc.femock.netty.codec.Iso8583Decoder;
import com.bpc.femock.netty.codec.Iso8583Encoder;
import com.solab.iso8583.MessageFactory;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.IdleStateHandler;

public class Iso8583ChannelInitializer<
        T extends Channel,
        B extends AbstractBootstrap,
        C extends ConnectorConfiguration> extends ChannelInitializer<T> {

    private final C configuration;
    private final ConnectorConfigurer<C, B> configurer;
    private final EventLoopGroup workerGroup;
    private final MessageFactory isoMessageFactory;
    private final ChannelHandler[] customChannelHandlers;
    private final Iso8583Encoder isoMessageEncoder;
    private final ChannelHandler loggingHandler;
    private final ChannelHandler parseExceptionHandler;

    public Iso8583ChannelInitializer(
            C configuration,
            ConnectorConfigurer<C, B> configurer,
            EventLoopGroup workerGroup,
            MessageFactory isoMessageFactory,
            ChannelHandler... customChannelHandlers) {
        this.configuration = configuration;
        this.configurer = configurer;
        this.workerGroup = workerGroup;
        this.isoMessageFactory = isoMessageFactory;
        this.customChannelHandlers = customChannelHandlers;

        this.isoMessageEncoder = createIso8583Encoder(configuration);
        this.loggingHandler = createLoggingHandler(configuration);
        this.parseExceptionHandler = createParseExceptionHandler();
    }

    @Override
    public void initChannel(T ch) {
        final ChannelPipeline pipeline = ch.pipeline();

        final int lengthFieldLength = configuration.getFrameLengthFieldLength();
        pipeline.addLast("lengthFieldFameDecoder", new LengthFieldBasedFrameDecoder(
                configuration.getMaxFrameLength(), 0, lengthFieldLength, 0, lengthFieldLength));
        pipeline.addLast("iso8583Decoder", createIso8583Decoder(isoMessageFactory));

        pipeline.addLast("iso8583Encoder", isoMessageEncoder);

        if (configuration.addLoggingHandler()) {
            pipeline.addLast(workerGroup, "logging", loggingHandler);
        }

        if (configuration.replyOnError()) {
            pipeline.addLast(workerGroup, "replyOnError", parseExceptionHandler);
        }

        pipeline.addLast("idleState", new IdleStateHandler(0, 0, configuration.getIdleTimeout()));
        pipeline.addLast("idleEventHandler", new IdleEventHandler(isoMessageFactory));
        if (customChannelHandlers != null) {
            pipeline.addLast(workerGroup, customChannelHandlers);
        }

        if (configurer != null) {
            configurer.configurePipeline(pipeline, configuration);
        }
    }

    protected MessageFactory getIsoMessageFactory() {
        return isoMessageFactory;
    }

    protected ChannelHandler createParseExceptionHandler() {
        return new ParseExceptionHandler(isoMessageFactory, true);
    }

    protected Iso8583Encoder createIso8583Encoder(C configuration) {
        return new Iso8583Encoder(configuration.getFrameLengthFieldLength());
    }

    protected Iso8583Decoder createIso8583Decoder(final MessageFactory messageFactory) {
        return new Iso8583Decoder(messageFactory);
    }

    protected ChannelHandler createLoggingHandler(C configuration) {
        return new IsoMessageLoggingHandler(LogLevel.INFO,
                configuration.logSensitiveData(),
                configuration.logFieldDescription(),
                configuration.getSensitiveDataFields());
    }


}
