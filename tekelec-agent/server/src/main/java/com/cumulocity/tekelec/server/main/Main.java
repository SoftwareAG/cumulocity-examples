package com.cumulocity.tekelec.server.main;

import static java.nio.file.Files.exists;

import java.nio.file.FileSystems;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Main {

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(10);
        EventLoopGroup workerGroup = new NioEventLoopGroup(10);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(new MessageDecoder(), new MessageHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(8282).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        configureLogger();
        new Main().run();
    }
    
    private static void configureLogger() {
        String logbackConfig = "/etc/tekelec/logback.xml";
        if(!exists(FileSystems.getDefault().getPath("/etc/tekelec", "logback.xml"))) {
            System.err.println("No logback configuration found: " + logbackConfig + ".");
            return;
        }
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(logbackConfig);
        } catch (JoranException je) {
            throw new RuntimeException("Cant configure logger from " + logbackConfig, je);
        }
        System.out.println("Log configured from file: " + logbackConfig + ".");
    }
}
