package utils;


import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class Utils {


    public static void saveExceptionMessage(Exception e, String name) {
        String desktopPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath();
        File exceptionFile = new File(desktopPath + "/Desktop/ArtistLog/" + name + "/" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) + ".txt");
        if (!exceptionFile.getParentFile().exists()) {
            exceptionFile.getParentFile().mkdirs();
        }
        try {
            exceptionFile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (exceptionFile.exists()) {
            BufferedWriter bw = null;
            PrintWriter pw = null;
            try {
                FileWriter fw = new FileWriter(exceptionFile);
                bw = new BufferedWriter(fw);
                pw = new PrintWriter(bw);
                e.printStackTrace(pw);
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (pw != null) {
                        pw.close();
                    }
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static Image scaleImage(Image src, int w, int h) {
        BufferedImage bi = new BufferedImage(w, h, TYPE_INT_ARGB);
        Graphics t = bi.getGraphics();
        t.drawImage(src, 0, 0, w, h, null);
        t.dispose();
        return bi;
    }
}
