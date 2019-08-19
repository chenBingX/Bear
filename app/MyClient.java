import core.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;



class MyClient {
    private Socket s;
    private String ip;
    private String userName = null;
    private OutputStream  outputStream;
    private InputStream inputStream;
    private ClientHandler handler;
    private Server server;
    public MyClient(Socket s, String ip){
        this.s = s;
        this.ip = ip;
        try {
            outputStream = s.getOutputStream();
            inputStream = s.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public Socket getSocket() {
        return s;
    }

    public String getIp() {
        return ip;
    }

    protected void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserName(){
        return this.userName;
    }
    public OutputStream getOutputStream() {
        return outputStream;
    }
    public InputStream getInputStream() {
        return inputStream;
    }
    public ClientHandler getHandler() {
        return handler;
    }
    public void setHandler(ClientHandler handler) {
        this.handler = handler;
    }
    public Server getServer() {
        return server;
    }
    public void setServer(Server server) {
        this.server = server;
    }

}