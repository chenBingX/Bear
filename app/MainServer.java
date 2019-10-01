import bear.BearClient;
import core.Server;
import core.ServerChecker;

//public class MainServer extends Application {
//    public static void main(String[] args) {
//
//        Application.launch(args);
//
//    }
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        core.Server.newInstance().start();
//        utils.LogUtils.e("core.Server 启动");
//    }
//}

public class MainServer {
    public static void main(String[] args) {
        Server server = Server.newInstance(8837);
        server.start(BearClient.class.getName());
        System.out.println("core.Server 启动");

        ServerChecker.get().start();
    }
}
