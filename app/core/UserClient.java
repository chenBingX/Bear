package core;

import com.google.gson.JsonObject;
import utils.SocketUtils;
import utils.TextUtils;

import java.net.Socket;

public abstract class UserClient extends Client {

    public String key;

    public UserClient(Socket socket) {
        super(socket);
    }

    @Override
    public boolean initCheck() {
        //1：c-s ：获取用户名
        byte[] b = SocketUtils.receiveData(inputStream);
        try {
            key = new String(b, "UTF-8");
            //2:s-c:反馈登陆信息 - 检测是否已存在相同用户
            if (TextUtils.isEmpty(key)) {
                return false;
            }
            boolean r = false;
            synchronized (Cache.cacheClientInfo) {
                if (Cache.cacheClientInfo.containsKey(key)) {
                    //存在相同用户
                    b = "EXIST".getBytes();
                    SocketUtils.sendData(outputStream, b); //发送验证不通过代码
                    r = false;
                } else {
                    b = "INEXISTANCE".getBytes("UTF-8");
                    SocketUtils.sendData(outputStream, b);

                    JsonObject user = new JsonObject();
                    user.addProperty("userName", key);
                    user.addProperty("password", "");
                    user.addProperty("startTime", System.currentTimeMillis());
                    Cache.cacheClientInfo.put(key, user);

                    b = SocketUtils.receiveData(inputStream);
                    String clientResponse_1 = new String(b, "UTF-8");
                    if (TextUtils.equals(clientResponse_1, "OK")) {
                        r = true;
                    }
                    SocketUtils.sendData(outputStream, "ACCEPT".getBytes());
                }
            }
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onDisconnection() {
        if (!TextUtils.isEmpty(key)) {
            synchronized (Cache.cacheClientInfo) {
                Cache.cacheClientInfo.remove(key);
            }
        }
    }


}
