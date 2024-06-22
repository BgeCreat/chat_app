package src.io;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.Scanner;

public class TextFileIO {

  private Scanner sc;//扫描
  

  //从文件中加载内容
  public String load() {
	  
    FileChooser fileChooser = new FileChooser();//打开文件系统窗口
    File file = fileChooser.showOpenDialog(null);
    if(file == null) //用户放弃操作则返回
      return null;
    StringBuilder sb = new StringBuilder(); //字符缓冲
    try {
      //读和写的编码要注意保持一致
      sc = new Scanner(file,"utf-8"); //将文件的内容先扫描一遍
      while (sc.hasNext()) {  //判断是否存在下一行，若存在就添加到字节缓存区
        sb.append(sc.nextLine() + "\n"); //补上行读取的行末尾回车
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      sc.close();
    }
    return sb.toString();   //将字符缓冲区变成string类型
  }


}
