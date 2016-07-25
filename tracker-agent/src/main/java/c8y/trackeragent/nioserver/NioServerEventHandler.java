package c8y.trackeragent.nioserver;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import c8y.trackeragent.nioserver.NioServerEvent.ReadDataEvent;

public class NioServerEventHandler implements WorkerTaskProvider {

    private static final int NUMBER_OF_WORKERS = 10;

    private final Map<SocketChannel, SocketChannelState> channelStatesIndex = new HashMap<SocketChannel, SocketChannelState>();
    private final List<SocketChannelState> channelStates = new ArrayList<SocketChannelState>();
    private int channelStatesPos = 0;
    private final Object channelStatesMonitor = new Object();

    private final ExecutorService workers = newFixedThreadPool(NUMBER_OF_WORKERS);

    private final ReaderWorkerExecutorFactory executorFactory;

    public NioServerEventHandler(ReaderWorkerExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
    }

    public void init() {
        for (int i = 0; i < NUMBER_OF_WORKERS; i++) {
            ReaderWorker worker = new ReaderWorker((WorkerTaskProvider) this);
            workers.execute(worker);
        }
    }

    public void handle(NioServerEvent.ReadDataEvent readData) {
        synchronized (channelStatesMonitor) {
            SocketChannelState state = getChannelState(readData);
            state.getDataBuffer().append(readData.getData(), readData.getNumRead());
        }
    }

    private SocketChannelState getChannelState(ReadDataEvent readData) {
        SocketChannelState channelState = channelStatesIndex.get(readData.getChannel());
        if (channelState == null) {
            ReaderWorkerExecutor executor = executorFactory.create(readData.getData());
            channelState = new SocketChannelState(executor, new DataBuffer(executor.getReportSeparator()));
            channelStatesIndex.put(readData.getChannel(), channelState);
            channelStates.add(channelState);
        }
        return channelState;
    }

    @Override
    public SocketChannelState next() {
        synchronized (channelStatesMonitor) {
            if (channelStates.size() == 0) {
                return null;
            }
            channelStatesPos = (channelStatesPos + 1) % channelStates.size();
            SocketChannelState result = channelStates.get(channelStatesPos);
            if (result.isProcessing()) {
                return null;
            }
            result.setProcessing(true);
            return result;
        }
    }
}
