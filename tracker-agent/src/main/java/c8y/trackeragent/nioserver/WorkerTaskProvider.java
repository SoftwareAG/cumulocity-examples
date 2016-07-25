package c8y.trackeragent.nioserver;

public interface WorkerTaskProvider {

    SocketChannelState next();
}
