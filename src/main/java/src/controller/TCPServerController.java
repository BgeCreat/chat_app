package src.controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerController {
    private ServerSocket serverSocket;
    private Socket connectToClient;
    private DataInputStream inFromClient;
    private DataOutputStream outToClient;

    public void startServer(int port) throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            throw new IOException("Server is already running.");
        }
        serverSocket = new ServerSocket(port);
        connectToClient = serverSocket.accept();
        inFromClient = new DataInputStream(connectToClient.getInputStream());
        outToClient = new DataOutputStream(connectToClient.getOutputStream());
    }

    public void sendMessage(String message) throws IOException {
        outToClient.writeUTF(message);
        outToClient.flush();
    }

    public String receiveMessage() throws IOException {
        return inFromClient.readUTF();
    }

    public void closeServer() throws IOException {
        if (connectToClient != null) {
            connectToClient.close();
        }
        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}
