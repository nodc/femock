package com.bpc.femock;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.ChannelPipeline;

public interface ConnectorConfigurer<C extends ConnectorConfiguration, B extends AbstractBootstrap> {

    default void configureBootstrap(B bootstrap, C configuration) {
        // this method was intentionally left blank
    }

    default void configurePipeline(ChannelPipeline pipeline, C configuration) {
        // this method was intentionally left blank
    }
}
