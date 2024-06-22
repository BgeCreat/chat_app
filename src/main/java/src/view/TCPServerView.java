package src.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.controller.TCPServerController;
import src.io.TextFileIO;

import java.io.IOException;

public class TCPServerView extends Application implements Runnable {
    private TCPServerController controller;
    private TextArea tashow = new TextArea();
    private TextArea tawrit = new TextArea();
    private TextField portField = new TextField("8989");
    private Thread thread;

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = new TCPServerController();
        BorderPane rootpane = new BorderPane();

        tawrit.setPadding(new Insets(10, 10, 10, 10));
        VBox vbwrit = new VBox();
        vbwrit.getChildren().add(tawrit);
        vbwrit.setPadding(new Insets(10, 10, 0, 10));

        Button btsend = new Button("发送");
        btsend.setPrefSize(80, 50);
        Button btfile = new Button("文件");
        btfile.setPrefSize(80, 50);

        HBox hbsendright = new HBox();
        hbsendright.getChildren().add(btsend);
        hbsendright.setAlignment(Pos.BOTTOM_RIGHT);
        HBox.setMargin(btsend, new Insets(0, 10, 10, 0));

        HBox hbsendleft = new HBox();
        hbsendleft.getChildren().add(btfile);
        HBox.setMargin(btfile, new Insets(0, 0, 10, 10));

        HBox hbsend = new HBox();
        hbsend.getChildren().addAll(hbsendleft, hbsendright);

        VBox vbsend = new VBox();
        vbsend.getChildren().addAll(vbwrit, hbsend);

        rootpane.setBottom(vbsend);

        tashow.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(tashow, Priority.ALWAYS);

        VBox vbshow = new VBox();
        vbshow.getChildren().addAll(new Label("信息显示区域："), tashow);
        vbshow.setPadding(new Insets(10, 10, 0, 10));
        tashow.setEditable(false);
        tashow.setPrefColumnCount(50);
        tashow.setPrefRowCount(50);

        HBox.setHgrow(tashow, Priority.ALWAYS);
        HBox.setHgrow(vbshow, Priority.NEVER);

        rootpane.setCenter(vbshow);

        VBox top = new VBox();
        HBox portBox = new HBox(10);
        portBox.setAlignment(Pos.CENTER);
        portBox.getChildren().addAll(new Label("Port:"), portField);
        top.getChildren().add(portBox);
        Button startButton = new Button("启动服务器");
        top.getChildren().add(startButton);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20, 20, 10, 20));
        rootpane.setTop(top);

        startButton.setOnAction(e -> {
            tashow.appendText("正在启动服务器...\n");
            new Thread(() -> {
                try {
                    int port = Integer.parseInt(portField.getText());
                    controller.startServer(port);
                    Platform.runLater(() -> tashow.appendText("服务器已启动，客户端可以连接并发送数据...\n"));

                    thread = new Thread(this);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    thread.start();
                } catch (NumberFormatException ex) {
                    Platform.runLater(() -> tashow.appendText("无效的端口号。\n"));
                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        tashow.appendText("无法启动服务器: " + ex.getMessage() + "\n");
                        ex.printStackTrace(); // 输出异常堆栈信息
                    });
                }
            }).start();
        });



        btsend.setOnAction(e -> sendMessage());
        tawrit.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        btfile.setOnAction(event -> {
            TextFileIO textFileIO = new TextFileIO();
            String msg = textFileIO.load();

            if (msg != null) {
                tashow.clear();
                tashow.setText(msg);
            }
        });

        Scene scene = new Scene(rootpane, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Soul");
        primaryStage.show();
    }

    private void sendMessage() {
        String s = tawrit.getText();
        if (s.length() > 0) {
            try {
                controller.sendMessage(s);
                tashow.appendText("我（服务端）说：" + tawrit.getText() + "\n");
                tawrit.setText(null);
            } catch (IOException e) {
                tashow.appendText("你的消息：“" + tawrit.getText() + "”未能发出去!\n");
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = controller.receiveMessage();
                Platform.runLater(() -> tashow.appendText("客户端说：" + message + "\n"));
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
