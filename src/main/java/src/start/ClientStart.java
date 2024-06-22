package src.start;

import javafx.application.Application;
import src.view.TCPClientView;

public class ClientStart {
    public static void main(String[] args) {
        Application.launch(TCPClientView.class, args);  // 或者 TCPServerView.class, args
    }
}
