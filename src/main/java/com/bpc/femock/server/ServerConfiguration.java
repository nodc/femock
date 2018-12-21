package com.bpc.femock.server;

import com.bpc.femock.ConnectorConfiguration;

public class ServerConfiguration extends ConnectorConfiguration {

    public ServerConfiguration() {
        this(newBuilder());
    }

    public ServerConfiguration(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static ServerConfiguration getDefault() {
        return newBuilder().build();
    }

    public static class Builder extends ConnectorConfiguration.Builder<Builder> {
        public ServerConfiguration build() {
            return new ServerConfiguration(this);
        }
    }
}
