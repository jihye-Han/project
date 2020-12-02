package com.example.mobile;

public class SocketEvent {

    /* singleton */
    private static final ConnectEvent connectEvent = new ConnectEvent();
    private static final RefreshEvent refreshEvent = new RefreshEvent();
    private static final BarCodeEvent barcodeEvent = new BarCodeEvent();

    /* event List */
    public static class ConnectEvent {
        public boolean isConnected;
    }

    public static class RefreshEvent {
        public boolean isUpdated;
    }

    public static class BarCodeEvent {
        public String scans;
    }


    /* static factory methods */
    public static SocketEvent.ConnectEvent newSocketConnectEvent(boolean isConnected) {
        connectEvent.isConnected = isConnected;
        return connectEvent;
    }

    public static SocketEvent.RefreshEvent newSocketRefreshEvent(boolean isUpdated) {
        refreshEvent.isUpdated = isUpdated;
        return refreshEvent;
    }

    public static SocketEvent.BarCodeEvent newSocketBarCodeEvent(String scans) {
        barcodeEvent.scans = scans;
        return barcodeEvent;
    }

    /* Constructor */
    private SocketEvent() {
    }
}
