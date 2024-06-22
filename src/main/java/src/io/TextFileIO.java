package src.io;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.Scanner;

public class TextFileIO {

  private Scanner sc;//ɨ��
  

  //���ļ��м�������
  public String load() {
	  
    FileChooser fileChooser = new FileChooser();//���ļ�ϵͳ����
    File file = fileChooser.showOpenDialog(null);
    if(file == null) //�û����������򷵻�
      return null;
    StringBuilder sb = new StringBuilder(); //�ַ�����
    try {
      //����д�ı���Ҫע�Ᵽ��һ��
      sc = new Scanner(file,"utf-8"); //���ļ���������ɨ��һ��
      while (sc.hasNext()) {  //�ж��Ƿ������һ�У������ھ���ӵ��ֽڻ�����
        sb.append(sc.nextLine() + "\n"); //�����ж�ȡ����ĩβ�س�
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      sc.close();
    }
    return sb.toString();   //���ַ����������string����
  }


}
