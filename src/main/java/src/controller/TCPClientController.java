package src.controller;

import src.model.TCPClientModel;

import java.io.IOException;

public class TCPClientController {
    private TCPClientModel model;

    public TCPClientController() {
        model = new TCPClientModel();
    }

    public void connectToServer(String serverAddress, int port) throws IOException {
        model.connect(serverAddress, port);
    }

    public void sendMessage(String message) throws IOException {
        model.sendMessage(message);
    }

    public String receiveMessage() throws IOException {
        return model.receiveMessage();
    }

    public void closeConnection() throws IOException {
        model.close();
    }

    public void disconnectFromServer() {
    }
}
