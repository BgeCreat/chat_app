package src.model;

import java.io.*;
import java.net.Socket;

public class TCPClientModel {
    private Socket connectToServer;
    private DataInputStream inFromServer;
    private DataOutputStream outToServer;

    public void connect(String serverAddress, int port) throws IOException {
        connectToServer = new Socket(serverAddress, port);
        inFromServer = new DataInputStream(connectToServer.getInputStream());
        outToServer = new DataOutputStream(connectToServer.getOutputStream());
    }

    public void sendMessage(String message) throws IOException {
        outToServer.writeUTF(message);
        outToServer.flush();
    }

    public String receiveMessage() throws IOException {
        return inFromServer.readUTF();
    }

    public void close() throws IOException {
        if (connectToServer != null) {
            connectToServer.close();
        }
    }
}
