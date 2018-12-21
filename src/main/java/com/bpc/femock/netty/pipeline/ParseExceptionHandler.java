package com.bpc.femock.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;

import java.text.ParseException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ParseExceptionHandler extends ChannelInboundHandlerAdapter {

    private final MessageFactory isoMessageFactory;

    private final boolean includeErrorDetails;

    public ParseExceptionHandler(MessageFactory isoMessageFactory, boolean includeErrorDetails) {
        this.isoMessageFactory = isoMessageFactory;
        this.includeErrorDetails = includeErrorDetails;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ParseException) {
            final IsoMessage message = createErrorResponseMessage((ParseException)cause);
            ctx.writeAndFlush(message);
        }
        super.exceptionCaught(ctx, cause);
    }

    protected IsoMessage createErrorResponseMessage(ParseException cause) {
        final IsoMessage message = isoMessageFactory.newMessage(0x1644);
        message.setValue(24, 650, IsoType.NUMERIC, 3); //650 (Unable to parse message)
        if (includeErrorDetails) {
            String details = cause.getMessage();
            if (details.length() > 25) {
                details = details.substring(0, 22) + "...";
            }
            message.setValue(44, details, IsoType.LLVAR, 25);
        }
        return message;
    }
}
