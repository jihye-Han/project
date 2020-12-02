package com.example.mobile;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketService {

    private static SocketService socketService = null;
    private static Socket socket = null;

    public static SocketService getInstance() {
        if (socketService == null) {
            socketService = new SocketService();
        }

        return socketService;
    }

    public boolean onGetSocketStatus() {
        if (socket == null) {
            return false;
        }

        return socket.connected();
    }

    public boolean onAttachSocket() {
        try {

            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.query = "client_type=mobile";
            socket = IO.socket(Global.socketUrl, opts);

            socket.connect();
            socket.on(Socket.EVENT_CONNECT, onConnectSocket);
            socket.on(Socket.EVENT_DISCONNECT, onDisConnectSocket);
            socket.on("UpdateCheerDataEvent", onUpdateCheerDataEvent);

            return true;
        } catch (URISyntaxException e) {

            return false;
        }
    }

    public void onDetachSocket() {

        socket.disconnect();

        socket.off(Socket.EVENT_CONNECT, onConnectSocket);
        socket.off(Socket.EVENT_DISCONNECT, onDisConnectSocket);
        socket.off("UpdateCheerDataEvent", onUpdateCheerDataEvent);

        socket = null;
    }

    public void onReconnectSocket() {
        onDetachSocket();
        onAttachSocket();
    }

    private Emitter.Listener onConnectSocket = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            EventBusService.getInstance().onPublish(SocketEvent.newSocketConnectEvent(true));
        }
    };
    private Emitter.Listener onDisConnectSocket = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            EventBusService.getInstance().onPublish(SocketEvent.newSocketConnectEvent(false));
        }
    };
    private Emitter.Listener onUpdateCheerDataEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            EventBusService.getInstance().onPublish(SocketEvent.newSocketRefreshEvent(true));
        }
    };

    private SocketService() {
    }
}
