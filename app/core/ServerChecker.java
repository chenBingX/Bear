package core;

import utils.TextUtils;
import utils.ThreadPool;

import java.util.Scanner;

public class ServerChecker {

    private boolean stop = false;

    private ServerChecker() {

    }

    private static final class Holder {
        private static final ServerChecker instance = new ServerChecker();
    }

    public static ServerChecker get() {
        return Holder.instance;
    }

    public void start() {
        ThreadPool.run(() -> {
            Scanner scanner = new Scanner(System.in);
            while (!stop) {
                String command = scanner.nextLine();
                try {
                    handleCommand(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void handleCommand(String command) {
        if (TextUtils.equals(command, "ls")) {
            synchronized (Cache.cacheClientInfo) {
                System.out.println(String.format("当前链接客户端数量：%d\n%s", Cache.cacheClientInfo.size(), Cache.cacheClientInfo.toString()));
            }
        } else if (TextUtils.equals(command, "-c")) {
            synchronized (Cache.cacheClientInfo) {
                Cache.cacheClientInfo.clear();
                System.out.println("已清除所有客户端");
            }
        }
    }

    public void stop() {
        stop = true;
    }
}
