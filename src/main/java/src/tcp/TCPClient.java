package src.tcp;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.io.TextFileIO;


public class TCPClient extends Application implements Runnable{
	
	 	
		Thread thread=null;
		Socket connectToServer;
		DataInputStream inFromServer;
		DataOutputStream outToServer;
		
	    TextArea tashow = new TextArea();//显示信息文本框
		TextArea tawrit = new TextArea();//输入信息文本框

	    public static void main(String[] args)
	    {
	    	Application.launch(args);
	    }
			
	    @Override
		public void start(Stage primaryStage) throws Exception {
	    
	    	BorderPane rootpane = new BorderPane();//主面板
	    	
	    	
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
	    			
	    			 
	    		
	    			
	    			
	    			//发送按钮动作：将发送框的信息加到显示框中
	    		    btsend.setOnAction(e -> {
	    		        
	    		    	String s=tawrit.getText();
	    		    	if (s.length()>0){
	    		            try{
	    		                outToServer.writeUTF(s);
	    		                outToServer.flush();
	    		                tashow.appendText("我（客户端）说： "+tawrit. getText()+"\n");
	    		            } catch (IOException e1){
	    		                tashow.appendText("你的消息：“"+tawrit.getText()+"”未能发送出去！\n");
	    		            }
	    		    	
	    		    	tawrit.clear();
	    		    	}	    	
	    		        	
	    		    });	
	    		    
	    		    //实现按回车发送,这里我们就要用到键盘的监听器KeyPressed
	    		    tawrit.setOnKeyPressed(e ->{
	    		    	if(e.getCode() == KeyCode.ENTER)
	    		    	{
	    		    		String s=tawrit.getText();
		    		    	if (s.length()>0){
		    		            try{
		    		                outToServer.writeUTF(s);
		    		                outToServer.flush();
		    		                tashow.appendText("我（客户端）说： "+tawrit. getText()+"\n");
		    		            } catch (IOException e1){
		    		                tashow.appendText("你的消息：“"+tawrit.getText()+"”未能发送出去！\n");
		    		            }
		    		    	
		    		    	tawrit.clear();
		    		    	}
	    		    	}
	    		    });
	    		    
	    		    btfile.setOnAction(event -> {
	    		      
	    		        TextFileIO textFileIO = new TextFileIO();
	    		        String msg = textFileIO.load();

	    		        if(msg != null){
	    		            tashow.clear();
	    		            tashow.setText(msg);
	    		        }
	    		      });
	    		   	
	    		    //接收方顶部的布局
	    		    VBox top = new VBox();
	            	
	    		    Label cbx = new Label("bge");
	            	top.getChildren().addAll(cbx);
	    	        
	            	top.setAlignment(Pos.CENTER);
	    	        top.setPadding(new Insets(20,20,10,20));//设置顶部hbox的留白区域        
	    	        rootpane.setTop(top);
	    	        
	    	        
	    	        new Thread(() -> {
	    	        	try{
	    	                connectToServer=new Socket("10.110.14.157",8007);
	    	                inFromServer=new DataInputStream(connectToServer.getInputStream());
	    	                outToServer=new DataOutputStream(connectToServer .getOutputStream());
	    	                tashow.appendText("连接成功，请说话...\n");

	    	                //创建线程在后台处理对方的消息
	    	                thread=new Thread(this);
	    	                thread.setPriority(Thread.MIN_PRIORITY);
	    	                thread.start();
	    	            } catch (UnknownHostException e1){
	    	                e1.printStackTrace();
	    	            } catch (IOException e1){
	    	                tashow.appendText("抱歉，未能连接到服务器！\n");
	    	                tawrit.setEditable(false);
	    	            }
	    	        	
				    }).start();
	    	        
	    	        	
	    	
	    }
	    
	    //本线程负责将服务器传来的消息显示在对话区域
		@Override
		public void run() {
			// TODO 自动生成的方法存根
			try{
	            while (true){
	                tashow.appendText("服务端说："+inFromServer.readUTF()+"\n");
	                Thread.sleep(1000);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			
		}
	    	
}