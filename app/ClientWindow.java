import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;


public class ClientWindow extends Application{
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage arg0) throws Exception {
        // TODO Auto-generated method stub

    }

}
class TestClient {
    private static final int TCP_PORT = 8837;
//    private static final String IP = "182.254.136.123";
    private static final String IP = "30.25.58.66";
    //private static final String IP = "127.0.0.1";

    private static final TestClient client = new TestClient();

    /*    private OutputStream outputStream;
        private InputStream inputStream;*/
    protected boolean isRun;

    private Logger log = Logger.getLogger("TestClient");

    private TestClient(){
        isRun = true;
    }

    //Java程序的执行起始
    public static void main(String[] args){
        //连接服务器
        client.startConn(IP, TCP_PORT);
    }

    //开始连接服务器
    @SuppressWarnings({ "resource", "unused" })
    private void startConn(String ip,int port){
        String str = new Scanner(System.in).nextLine(); //输入用户名
        //开始连接服务器
        try {
            Socket s = new Socket(ip,port); //尝试与服务器建立连接
            //链接成功后开启子线程处理收发事物
            new Thread(new SecondThread(s,str)).start();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

class SecondThread implements Runnable{
    private Logger log = Logger.getLogger("TestClient_SecondThread");

    private Socket s;
    private OutputStream outputStream;
    private InputStream inputStream;

    private String str;  //代表着登陆信息

    private boolean isRun;

    /**
     * 构造器
     * @param s 与服务建立的套接口
     */
    public SecondThread(Socket s,String str){
        this.s = s;
        this.str = str;
        try {
            //获得输入输出流
            this.outputStream = s.getOutputStream();
            this.inputStream = s.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.isRun = true;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        initConn(str);  //初始化连接事物，包括验证/反馈登陆信息等

    }

    private void initConn(String str){
        //1:c-s:向服务发送用户名
        try{
            byte[] b = str.getBytes("UTF-8");
            sendData(b);  //向服务器发送用户名

            //接收服务器的反馈信息
            //2:s-c:接收服务器的登陆反馈信息
            b = reciveData();
            //log.log(Level.INFO,"2:s-c:——b = " + b);
            str = new String(b,"UTF-8");
            //log.log(Level.INFO,"检测是否通过验证：" + str);
            if(str.equals("EXIST")){
                //如果已存在同名用户
                if(s!=null){
                    outputStream.close();
                    inputStream.close();
                    s.close();
                    System.out.println("用户名已存在，请重试！");
                    return;
                }
            }else if(str.equals("INEXISTANCE")){
                System.out.println("登陆验证成功，欢迎！");
                inThread();
                outThread();
            }
        } catch(IOException e){
            e.printStackTrace();
            return;
        }
    }

    /**
     * 该方法开启一条线程来接收消息
     * @param in
     */
    private void inThread() {
        // TODO Auto-generated method stub
        new Thread(){
            public void run(){
                byte[] bytes = null;
                String str = null;
                while(isRun){
                    try {
                        if((bytes = reciveData())!=null){
                            str = new String(bytes,"UTF-8");
                            System.out.println(str);
                        };
                        //Thread.sleep(300);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                        return;
                    }

                }
                return;
            }
        }.start();

    }

    /**
     * 该方法开启一条发送消息的线程
     * @param out
     */
    private void outThread() {
        // TODO Auto-generated method stub
        Scanner input = new Scanner(System.in);
        new Thread(){
            public void run(){
                String str = null;
                while(isRun){
                    str = input.nextLine();  //获得一次输入
                    if(!str.equals("")){  //如果不为空
                        //2：c-s
                        try {
                            sendData(str.getBytes("UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            input.close();
                            e.printStackTrace();
                            log.log(Level.SEVERE,"发送数据过程中遇到了错误，请检查数据发送逻辑和数据的有效性。");
                            return;
                        }
                    } else {
                        System.out.println("输入不能为空！");
                    }
                }
            }
        }.start();
    }


    /**
     * 接收消息
     */
    public byte[] reciveData(){
        byte[] b = null;
        try {
            int length;
            while((length = inputStream.available()) == 0){ //直到有数据跳出循环，然后读取数据
                //阻塞中
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            //log.log(Level.INFO,"检测inputStream的长度  length = " + length);
            b = new byte[length];
            inputStream.read(b);
            return b;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送消息
     */
    public void sendData(byte[] data){
        try {
            outputStream.write(data);  //写出数据
            outputStream.flush();  //刷新
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
