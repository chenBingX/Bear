package core;

import utils.LogUtils;
import utils.SocketUtils;
import utils.ThreadPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class Client {

    private Socket s;
    public OutputStream outputStream;
    public InputStream inputStream;

    public Client(Socket socket) {
        this.s = socket;
        try {
            outputStream = s.getOutputStream();
            inputStream = s.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e("创建客户端过程中遇到错误！ip：" + getIp());
            disconnection();
        }
    }

    public final void start() {
        ThreadPool.run(() -> {
            if (innerInitCheck()) {
                try {
                    doWork();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e("客户端运行过程中遇到未知错误！ip：" + getIp());
                }
            } else {
                System.out.println("客户端启动校验失败!强制断开链接..ip：" + getIp());
            }
            disconnection();
        });
    }

    private void disconnection() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (s != null) {
                s.close();
            }
            onDisconnection();
        } catch (IOException e) {
            LogUtils.e("断开链接过程中遇到错误！ip：" + getIp());
        }
    }

    public void onDisconnection() {

    }

    private boolean innerInitCheck() {
        try {
            return initCheck();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("客户端启动校验过程中遇到错误！ip：" + getIp());
        }
        return false;
    }

    /**
     * 进行启动校验。
     *
     * @return 校验成功，将继续执行 {@link Client#doWork()}；校验失败，强制断开链接。
     */
    public boolean initCheck() {
        return false;
    }

    /**
     * 实现处理逻辑
     */
    public abstract void doWork() throws Exception;

    public final Socket getSocket() {
        return s;
    }

    public final String getIp() {
        if (s != null) {
            return s.getInetAddress().getHostAddress();
        } else {
            return "-1.-1.-1.-1";
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * 覆盖此方法，以返回客户端类型。
     * <p>
     * 默认返回 "未定义类型"
     *
     * @return
     */
    public String getType() {
        return "未定义类型";
    }
}
