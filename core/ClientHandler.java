import core.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientHandler implements Runnable{
    private MyClient myClient;
    private Server server;
    private Socket s;
    private boolean isRun;
    private InputStream inputStream;
    private OutputStream outputStream;

    private Logger log = Logger.getLogger("Server_ClientThread");
    /*
       */


    //myClient.setThread(new ClientThread(myClient));   //给客户端创建一条专属线程
    //new Thread(myClient.getThread()).start();


    /**
     * 构造函数
     */
    public ClientHandler(MyClient myClient){
        this.myClient = myClient;
        this.server = myClient.getServer();
        this.s = myClient.getSocket();
        this.isRun = true;
        this.inputStream = myClient.getInputStream();
        this.outputStream = myClient.getOutputStream();
    }

   @Override
   public void run() {
       initConn(); //初始化连接，验证登陆信息等
       byte[] b;
       String str;
       String userName = myClient.getUserName();
       List<MyClient> myClients = null;
       MyClient myClientTemp;
       while(isRun){
           //循环监听客户端发来的消息
           b = reciveData(inputStream);
           if(b!=null){ //如果接收到
               try {
                   str ="                              "+userName + " : "+  new String(b,"UTF-8");
                   b = str.getBytes("UTF-8");
                   //向所有其它客户端发送消息
                   for(int i = 0; i< myClients.size(); i++){
                       myClientTemp = myClients.get(i);
                       if(!(myClientTemp.getUserName().equals(userName))){
                           sendData(myClientTemp.getOutputStream(), b);
                       }
                   }
               } catch (UnsupportedEncodingException e) {
                   e.printStackTrace();
               }

               }            
           }

       }

   private void initConn(){
               //1：c-s ：获取用户名
               byte[] b = reciveData(inputStream);  //读取一次数据
               try {
                   String str = new String(b,"UTF-8");
                   //检测是否已存在相同用户
                   //2:s-c:反馈登陆信息
                   MyClient myClientTemp;
                   boolean isExist = false;
                   List<MyClient> myClients = null;  //获得服务器的客户端池
                   for(int i = 0; i< myClients.size(); i++){
                       myClientTemp = myClients.get(i);
                       if(myClientTemp.getUserName().equals(str)){
                           isExist = true;
                           break;
                       }
                   }
                   if(isExist){
                       //存在相同用户
                       b = "EXIST".getBytes();
                       sendData(outputStream,b); //发送验证不通过代码
                       if(s!=null){
                           try {
                               outputStream.close();
                               inputStream.close();
                               s.close();
                           } catch (IOException e) {
                               // TODO Auto-generated catch block
                               e.printStackTrace();
                           }
                       }

                   }else{
                       //不存在相同用户
//                       infoArea.appendText(" 客户端认证成功！\n   IP："+myClient.getIp() + "\n   用户名为 ：" + str + "\n---------------------------------------------------------------------------------------------\n");
                       System.out.println(" 客户端认证成功！\n   IP："+ myClient.getIp() + "\n   用户名为 ：" + str + "\n---------------------------------------------------------------------------------------------\n");
                       b = "INEXISTANCE".getBytes("UTF-8");
                       sendData(outputStream,b); //发送验证通过代码
                       //将客户端收入线程池
                       myClients.add(myClient);
                       myClient.setUserName(str);  //设置用户名
                   }
               } catch (UnsupportedEncodingException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
//                   infoArea.appendText("转换用户名时出错，客户端ip为："+myClient.getIp());
                   System.out.println("转换用户名时出错，客户端ip为："+ myClient.getIp());
               }
   }

    /**
     * 接收数据
     * @param input 输入流
     * @return 接收到的数据，未接收成功返回null
     */
    public byte[] reciveData(InputStream input){

           try {
               int length;
               while((length = input.available()) == 0){ //直到有数据跳出循环，读取数据
                   //阻塞中
                   try {
                       Thread.sleep(300);
                   } catch (InterruptedException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                       log.log(Level.INFO, "接收数据时，遇到了休眠错误。");
                   }

               }
               byte[] b = new byte[length];
               input.read(b);
               //log.log(Level.INFO, "读取一次数据完成！b=" + b.length + "内容：" + new String(b,"UTF-8"));
               return b;
           } catch (IOException e) {
               e.printStackTrace();
           } finally {

           }
           return null;
    }
    /**
     * 发送数据
     * @param output  输出流
     * @param b 需要发送的数据
     */
    public void sendData(OutputStream output,byte[] b){
        try {
           // log.log(Level.INFO, "显示即将发送的数据！b=" + b.length + "内容：" + new String(b,"UTF-8"));
           // log.log(Level.INFO,"检测output是否为空 output = " + output);
           output.write(b);
           output.flush();
       } catch (IOException e) {
           e.printStackTrace();
       }

    }

   /**
    * @return 返回线程中的Client
    */
   public MyClient getMyClient(){
       return myClient;
   }
   /**
    * 设置Runnable是否继续运行
    * @param isRun 是否继续运行。
    */
   public void setIsRun(boolean isRun){
       this.isRun = isRun;
   }

}