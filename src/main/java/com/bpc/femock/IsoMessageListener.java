package com.bpc.femock;

import com.solab.iso8583.IsoMessage;

import io.netty.channel.ChannelHandlerContext;

public interface IsoMessageListener<T extends IsoMessage> {

    boolean applies(T isoMessage);

    boolean onMessage(ChannelHandlerContext ctx, T isoMessage);

}
