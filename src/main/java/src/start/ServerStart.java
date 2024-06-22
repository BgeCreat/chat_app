package src.start;

import javafx.application.Application;
import src.view.TCPServerView;

public class ServerStart {
    public static void main(String[] args) {
        Application.launch(TCPServerView.class, args);  // 或者 TCPServerView.class, args
    }
}
