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
import src.controller.TCPClientController;
import src.io.TextFileIO;

import java.io.IOException;

public class TCPClientView extends Application implements Runnable {
    private TCPClientController controller;
    private TextArea tashow = new TextArea();
    private TextArea tawrit = new TextArea();
    private TextField ipField = new TextField("localhost");
    private TextField portField = new TextField("8989");
    private Thread thread;

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = new TCPClientController();
        BorderPane rootpane = new BorderPane();

        setupBottomPane(rootpane);
        setupCenterPane(rootpane);
        setupTopPane(rootpane);

        Scene scene = new Scene(rootpane, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TCP Client");
        primaryStage.setOnCloseRequest(event -> closeApplication());
        primaryStage.show();
    }

    private void setupBottomPane(BorderPane rootpane) {
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
    }

    private void setupCenterPane(BorderPane rootpane) {
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
    }

    private void setupTopPane(BorderPane rootpane) {
        VBox top = new VBox();
        HBox ipPortBox = new HBox(10);
        ipPortBox.setAlignment(Pos.CENTER);
        ipPortBox.getChildren().addAll(new Label("IP:"), ipField, new Label("Port:"), portField);
        top.getChildren().add(ipPortBox);
        Button connectButton = new Button("连接");
        top.getChildren().add(connectButton);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20, 20, 10, 20));
        rootpane.setTop(top);

        connectButton.setOnAction(e -> {
            try {
                controller.connectToServer(ipField.getText(), Integer.parseInt(portField.getText()));
                tashow.appendText("正在连接到服务器...连接成功后连接按钮将被禁用\n");
                connectButton.setDisable(true); // 连接成功后禁用连接按钮

                thread = new Thread(this);
                thread.setDaemon(true); // 设置为守护线程，随主线程关闭而关闭
                thread.start();
            } catch (NumberFormatException ex) {
                tashow.appendText("端口号必须是数字\n");
            } catch (IOException ex) {
                tashow.appendText("无法连接到服务器：" + ex.getMessage() + "\n");
                ex.printStackTrace(); // 添加异常信息输出，查看连接失败的具体原因
            }
        });
    }

    private void sendMessage() {
        String s = tawrit.getText();
        if (s.length() > 0) {
            try {
                controller.sendMessage(s);
                tashow.appendText("我（客户端）说：" + tawrit.getText() + "\n");
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
                Platform.runLater(() -> {
                    tashow.appendText("服务器说：" + message + "\n");
                });
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeApplication() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt(); // 中断消息接收线程
        }
        controller.disconnectFromServer(); // 实现此方法来关闭与服务器的连接
    }

}
