package com.zbl.config;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyWebsocketServer {

    @Autowired
    private WebsocketPipeline websocketPipeline;
    @Autowired
    private NettyProperties nettyProperties;

    @Bean
    public ServerBootstrap start(){

        // 启动服务器
        Thread thread =  new Thread(() -> {
            NioEventLoopGroup bossGroup = new NioEventLoopGroup(nettyProperties.getBossThreads());
            NioEventLoopGroup workerGroup = new NioEventLoopGroup(nettyProperties.getWorkThreads());
            try {
                log.info("start netty [WebSocket] server ,port: " + nettyProperties.getPort());
                ServerBootstrap boot = new ServerBootstrap();
                options(boot).group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(wsPipeline);
                Channel ch = null;
                //是否绑定IP
                if(StringUtils.isNotEmpty(nettyWsProperties.getBindIp())){
                    ch = boot.bind(nettyWsProperties.getBindIp(),nettyWsProperties.getPort()).sync().channel();
                }else{
                    ch = boot.bind(nettyWsProperties.getPort()).sync().channel();
                }
                ch.closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("启动NettyServer错误", e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
        thread.setName("Ws_Server");
        thread.start();
        return boot;
    }

}
