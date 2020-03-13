package com.zbl.config;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WebsocketPipeline extends ChannelInitializer<SocketChannel> {
    @Autowired
    private WebsocketServerHandler websocketServerHandler;
    private static final int READ_IDEL_TIME_OUT = 3; // 读超时
    private static final int WRITE_IDEL_TIME_OUT = 4;// 写超时
    private static final int ALL_IDEL_TIME_OUT = 5; // 所有超时
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline p = ch.pipeline();
        p.addLast(new IdleStateHandler(READ_IDEL_TIME_OUT,WRITE_IDEL_TIME_OUT, ALL_IDEL_TIME_OUT, TimeUnit.MINUTES));
        p.addLast("http-codec", new HttpServerCodec());
        p.addLast("aggregator", new HttpObjectAggregator(65536));
        p.addLast("http-chunked", new ChunkedWriteHandler());
        p.addLast("handler",websocketServerHandler);
    }

}
