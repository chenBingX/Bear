package bear;


import artist.*;
import com.google.gson.JsonObject;
import core.Code;
import core.UserClient;
import utils.GsonUtils;
import utils.LogUtils;
import utils.SocketUtils;
import utils.TextUtils;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class BearClient extends UserClient {

    private PhoneLayout phoneLayout;
    private Graphics2D surface;
    private BufferedImage image;
    private int count;
    private boolean work;
    private Random random = new Random();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private Context context;

    public BearClient(Socket socket) {
        super(socket);
    }

    @Override
    public void doWork() throws Exception {
        work = true;
        while (work) {
            byte[] b = SocketUtils.receiveData(inputStream);
            String data = new String(b, "UTF-8");
            LogUtils.e(String.format("time:%s, userName：%s，data：%s", sdf.format(new Date()), key, data));
            boolean needFlush = false;
            if (data.contains("|")) {
                String[] split = data.split("\\|");
                for (String aSplit : split) {
                    needFlush = handleOneData(aSplit);
                }
            } else {
                needFlush = handleOneData(data);
            }
            if (needFlush) {
                flush();
            }
        }
    }

    private boolean handleOneData(String data) {
        boolean needFlush = false;
        if (TextUtils.isJsonText(data)) {
            JsonObject clientData = GsonUtils.fromJson(data);
            if (!clientData.has("type")) {
                LogUtils.e("错误的信息！userName：" + key + "，ip：" + getIp());
                LogUtils.e("必须包含 \"type\"!");
                return false;
            }
            String type = GsonUtils.getString(clientData, "type");
            if (TextUtils.equals(type, "exit")) {
                work = false;
                return false;
            } else if (TextUtils.equals(type, "init")) {
                /* 获取 设备信息 -> 初始化 Surface */
                handleInit(clientData);
                /**
                 *
                 */
                needFlush = false;
            } else if (TextUtils.equals(type, "touch")) {
                // 处理触摸事件
                handleTouch(clientData);
                needFlush = false;
            }
        } else {
            LogUtils.e("信息格式错误！userName：" + key + "，ip：" + getIp());
            LogUtils.e("data：" + data);
        }
        return needFlush;

    }

    int touchCount = 0;

    private void handleTouch(JsonObject data) {
        if (data.has("action") && data.has("x") && data.has("y")) {
            int action = GsonUtils.getInt(data, "action");
            touchCount++;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    count = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    System.out.println(String.format("本次事件流共接收到 %d 个点", touchCount));
                    touchCount = 0;
                    break;
            }
            float x = GsonUtils.getFloat(data, "x");
            float y = GsonUtils.getFloat(data, "y");
//            drawPoint(x, y);
            phoneLayout.dispatchTouchEvent(MotionEvent.build(action,x,y));
        } else {
            LogUtils.e("信息格式错误！\n" + data.toString());
        }
    }

    private void handleInit(JsonObject data) {
        if (data.has("w") && data.has("h")) {
            initSurface(GsonUtils.getInt(data, "w")
                    , GsonUtils.getInt(data, "h")
                    , GsonUtils.getFloat(data, "density")
                    , GsonUtils.getFloat(data, "scaledDensity"));
            buildPage();
        } else {
            LogUtils.e("信息格式错误！\n" + data.toString());
            SocketUtils.sendData(outputStream, String.format("{errCode:%d}", Code.INIT_FAILURE).getBytes());
        }
    }

    private void initSurface(int w, int h, float density, float scaledDensity) {
        if (!(w > 0 && h > 0)) return;
        context = new Context(() -> flush());
        phoneLayout = new PhoneLayout(context);
        phoneLayout.setBackground(Color.BLACK);
        phoneLayout.setBounds(0, 0, w, h);
        context.setPhoneLayout(phoneLayout);
        context.setDensity(density);
        context.setScaledDensity(scaledDensity);

        image = new BufferedImage(phoneLayout.getWidth(), phoneLayout.getHeight(), BufferedImage.TYPE_INT_RGB);
        surface = image.createGraphics();
    }

    private void buildPage() {
        ArtistLayout artistLayout = new ArtistLayout(phoneLayout.getContext());
        phoneLayout.add(artistLayout);
        artistLayout.setRoot(phoneLayout);
        artistLayout.setBackground(Color.decode("#03DAC6"));

        ArtText artText = new ArtText(artistLayout.getContext());
        artText.setWidth(500);
        artText.setHeight(200);
        artText.setPadding((int) context.dp(10));
        artText.setText("Hello World！");
        artText.setGravity(Gravity.CENTER);
        artText.setLayoutGravity(Gravity.CENTER);
        artText.setTextColor(Color.BLACK);
        artText.setTextSize(context.dp(22));
        artText.setBackgroundColor(Color.WHITE);
        artistLayout.addView(artText);

        artText.setOnClickListener(artView -> {
            LogUtils.e("文字颜色变更");
            artText.setTextColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        });


    }

    private void flush() {
        try {
            if (image != null) {
                phoneLayout.paintComponent(surface);
                ImageIO.write(image, "png", outputStream);
                File file = new File("/Users/coorchice/VSProjects/Bear/img");
                if (!file.exists()) file.mkdir();
                File output = new File("/Users/coorchice/VSProjects/Bear/img/_" + (count++) + ".png");
                ImageIO.write(image, "png", output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
