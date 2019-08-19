package core;

import utils.LogUtils;
import utils.ThreadPool;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;


public class Server {

    private int PORT = 8839;

    private AtomicInteger count = new AtomicInteger();
    private ServerSocket ss;

    private Server(int port) {
        PORT = port;
    }

    public static Server newInstance(int port) {
        return new Server(port);
    }

    public void start(String clientClazz) {
        if (ss == null) {
            try {
                ss = new ServerSocket(PORT);
                System.out.println("ServerSocket 创建成功！PORT：" + PORT);
            } catch (IOException e) {
                LogUtils.e("ServerSocket 创建 Server 过程中遇到错误！");
                e.printStackTrace();
                return;
            }
        }
        ThreadPool.run(() -> {
            try {
                while (true) {
                    Socket s;
                    synchronized (ss) {
                        s = ss.accept();
                    }
                    try {
                        Client client = onClientConnection(s, clientClazz);
                        System.out.println(String.format("客户端接入！ip：%s，类型：%s", client.getIp(), client.getClass().getSimpleName()));
                        System.out.println("总计客户端接入数量：" + count.addAndGet(1));
                    } catch (Exception e) {
                        LogUtils.e("客户端创建过程中遇到错误！");
                        e.printStackTrace();
                        if (!s.isClosed()) {
                            s.close();
                        }
                    }
                }
            } catch (Exception e) {
                LogUtils.e("遇到未知错误！PORT：" + PORT + "，Thread.id：" + Thread.currentThread().getId());
                e.printStackTrace();
            }
        });
    }

    private Client onClientConnection(Socket s, String clientClazzName) throws Exception {
        String ip = s.getInetAddress().getHostAddress();
        System.out.println("接入 ip：" + ip);

        Class clientClazz = Class.forName(clientClazzName);
        Constructor constructor = clientClazz.getConstructor(Socket.class);
        constructor.setAccessible(true);
        Object o = constructor.newInstance(s);
        Method startMethod = clientClazz.getMethod("start");
        startMethod.setAccessible(true);
        startMethod.invoke(o);
        return (Client) o;
    }
}