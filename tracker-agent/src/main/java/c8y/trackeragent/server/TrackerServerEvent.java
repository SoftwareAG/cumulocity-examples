package c8y.trackeragent.server;

public class TrackerServerEvent {

    private final ConnectionDetails connectionDetails;

    public TrackerServerEvent(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    public static class ReadDataEvent extends TrackerServerEvent {

        private final byte[] data;
        private final int numRead;

        public ReadDataEvent(ConnectionDetails connectionDetails, byte[] data, int numRead) {
            super(connectionDetails);
            this.data = data;
            this.numRead = numRead;
        }

        public byte[] getData() {
            return data;
        }

        public int getNumRead() {
            return numRead;
        }

    }
    
    public static class CloseConnectionEvent extends TrackerServerEvent {
        
        public CloseConnectionEvent(ConnectionDetails connectionDetails) {
            super(connectionDetails);
        }
    }

}
