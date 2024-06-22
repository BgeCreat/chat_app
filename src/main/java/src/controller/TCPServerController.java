package src.controller;

import src.model.TCPServerModel;

import java.io.IOException;

public class TCPServerController {
    private TCPServerModel serverModel;

    public TCPServerController() {
        this.serverModel = new TCPServerModel();
    }

    public void startServer(int port) throws IOException {
        serverModel.start(port);
    }

    public void sendMessage(String message) throws IOException {
        serverModel.sendMessage(message);
    }

    public String receiveMessage() throws IOException {
        return serverModel.receiveMessage();
    }

    public void closeServer() throws IOException {
        serverModel.close();
    }
}
