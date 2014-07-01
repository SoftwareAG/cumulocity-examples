package com.cumulocity.tixi.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.Service;

public class Server {

    private final int port;

    EventLoopGroup group = new NioEventLoopGroup();

    private final Service service = new AbstractExecutionThreadService() {

        @Override
        protected void run() throws Exception {
            ServerBootstrap bootstrap = new ServerBootstrap();
            try {
                bootstrap.group(group).channel(NioServerSocketChannel.class);

                bootstrap.bind(port)
                .sync();
            } catch (InterruptedException e) {
                Throwables.propagate(e);
            }
        }
    };

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        service.startAsync();
        service.awaitRunning();
    }

    public void stop() {
        service.startAsync();
        service.awaitTerminated();
    }

}
