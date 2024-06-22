package src.tcp;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.io.TextFileIO;

public class TCPServer extends Application implements Runnable {
	
	TextArea tashow = new TextArea();//显示信息文本框
	TextArea tawrit = new TextArea();//输入信息文本框
	
	Thread thread = null;
    ServerSocket serverSocket;
    Socket connectToClient;
    DataInputStream inFromClient;
    DataOutputStream outToClient;
	    
	public static void main(String[] args)
	 {
	        Application.launch(args);
	 }
	    
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane rootpane = new BorderPane();//主面

		//发送信息文本框
				tawrit.setPadding(new Insets(10, 10, 10, 10));
				VBox vbwrit = new VBox();
				vbwrit.getChildren().add(tawrit);
				vbwrit.setPadding(new Insets(10, 10, 0, 10));
				
				//发送按钮和文件按钮
				Button btsend = new Button("发送");
				btsend.setPrefSize(80, 50);
				Button btfile = new Button("文件");
				btfile.setPrefSize(40, 40);
				
				HBox hbsendright = new HBox();
				hbsendright.getChildren().add(btsend);
				hbsendright.setAlignment(Pos.BOTTOM_RIGHT);//设置发送面板在最右侧
				HBox.setMargin(btsend, new Insets(0, 10, 0, 0));
				
				HBox hbsendleft = new HBox();
				hbsendleft.getChildren().add(btfile);
				HBox.setMargin(btfile, new Insets(0, 0, 10, 10));
				
				VBox gp = new VBox();
				gp.getChildren().addAll(hbsendleft, hbsendright);
				
				VBox vbsend = new VBox();
			    vbsend.getChildren().addAll(vbwrit, gp);
				
				rootpane.setBottom(vbsend);
				
				//显示信息文本框

				tashow.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);//设置对话框的最大变化范围，这里统一设置为最大double，即0x3f3f3f3f，这样当我们将窗口拖长时，对话框也可以自适应的变大或变小
				VBox.setVgrow(tashow, Priority.ALWAYS);//设置显示信息区域的文本区域可以自动纵向扩充范围，这样的好处时让按钮可以一直在整个窗口的右下角，十分美观
				
			    VBox vbshow = new VBox();
			    
			    vbshow.getChildren().addAll(new Label("信息显示区域："), tashow);
				vbshow.setPadding(new Insets(10, 10, 0, 10));
				tashow.setEditable(false);//设置文本框只读
				
				tashow.setPrefColumnCount(50);//设置文本区显示高度
				tashow.setPrefRowCount(50);//设置文本区显示宽度
				
				HBox.setHgrow(tashow, Priority.ALWAYS);
				HBox.setHgrow(vbshow, Priority.NEVER);
			   
			    rootpane.setCenter(vbshow);
				
				//主面板
				Scene scene = new Scene(rootpane, 800, 800);
				primaryStage.setScene(scene);
				primaryStage.setTitle("Soul");
				primaryStage.show();
				
				 
				
				
				
				
							    
			    //文件按钮
			    btfile.setOnAction(event -> {
			      
			        TextFileIO textFileIO = new TextFileIO();
			        String msg = textFileIO.load();

			        if(msg != null){
			            tashow.clear();
			            tashow.setText(msg);
			        }
			      });
			    
			   
			    //网络通信顶部的布局
		        VBox top = new VBox();
		        Button cb = new Button("搜索：");
		        
		        cb.setAlignment(Pos.TOP_LEFT);
		        
		        top.setSpacing(10);
		        Label lab = new Label("聊天界面");
		        HBox hb = new HBox();
		        hb.getChildren().add(lab);
		        hb.setAlignment(Pos.CENTER);

		        top.getChildren().addAll(cb, hb);
		        

		        top.setPadding(new Insets(20,20,10,20));//设置顶部hbox的留白区域
		        
		        rootpane.setTop(top);
		        
		       
		        
		        


		        //搜索按钮事件
		        cb.setOnAction(e ->
		        {  
		        	CheckBox cbx = new CheckBox();
		        	VBox vb = new VBox();
		        	
		        	
		        	Button bt = new Button("发给");
		        	VBox vb2 = new VBox();
		        	vb2.getChildren().add(bt);
		        	bt.setPrefSize(50, 0);

		        	vb2.setAlignment(Pos.BASELINE_RIGHT);
		    		VBox.setMargin(bt, new Insets(0, 5, 5, 0));

		        	
		        	cbx = new CheckBox("发送者");
		        	
		        	       	
		        	vb.getChildren().add(cbx);
		        	
		        	
		        	vb.getChildren().add(vb2);
		        	Stage primarystage = new Stage();
		    		Scene scene2 = new Scene(vb, 100, 300);
		        	primarystage.setScene(scene2);
		        	primarystage.show();
		        	
		        	  	
		        });
		        
		      //发送按钮动作：将发送框的信息加到显示框中
			    btsend.setOnAction(e -> { 
			    	
			    	String s = tawrit.getText();
			        if (s.length() > 0) {
			            try {
			                outToClient.writeUTF(s);
			                outToClient.flush();
			                tashow.appendText("我（服务端）说：" + tawrit.getText() + "\n");
			                tawrit.setText(null);
			            } catch (IOException el) {
			                tashow.appendText("你的消息：“" + tawrit.getText() + "”未能发出去!\n");
			           }
			        }	
			    
			    			    			    	                    				   		    	
			    });	
		        
			  //实现按回车发送,这里我们就要用到键盘的监听器KeyPressed
			    tawrit.setOnKeyPressed(e ->{
			    	if(e.getCode() == KeyCode.ENTER)
			    	{
			    					    		
			    		String s = tawrit.getText();
				        if (s.length() > 0) {
				            try {
				                outToClient.writeUTF(s);
				                outToClient.flush();
				                tashow.appendText("我（服务端）说：" + tawrit.getText() + "\n");
				                tawrit.setText(null);
				            } catch (IOException el) {
				                tashow.appendText("你的消息：“" + tawrit.getText() + "”未能发出去!\n");
				            }
				        }
				        
			    					    		
			    	}		
			    		
			    });
			    
			    			    
			    
			    new Thread(() -> {
			        try {
			            serverSocket = new ServerSocket(8007);
			            tashow.appendText("正在等待对话请求...\n");
			            connectToClient = serverSocket.accept();
			            inFromClient = new DataInputStream(connectToClient.getInputStream());
			            outToClient = new DataOutputStream(connectToClient.getOutputStream());
			            
			          //创建线程在后台处理对方的消息
    	                thread=new Thread(this);
    	                thread.setPriority(Thread.MIN_PRIORITY);
    	                thread.start();
			            
			        } catch (IOException e) {
			            Platform.runLater(() -> tashow.appendText("对不起，不能创建服务器\n"));
			        }
			        
			        finally {
			        	try {
							serverSocket.close();
						} catch (IOException e1) {
							// TODO 自动生成的 catch 块
							e1.printStackTrace();
						}
			        }
			        
			    }).start();		
	}

	@Override
    //本线程负责将客户机传来的信息显示在对话区域
    public void run() {
		 try{
	            while (true){
	                tashow.appendText("客户端说："+inFromClient.readUTF()+"\n");
	                Thread.sleep(1000);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
    }
  
}
