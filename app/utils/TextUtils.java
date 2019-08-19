package utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.oracle.javafx.jmx.json.JSONException;

public class TextUtils {
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isJsonText(String str) {
        boolean result = false;
        try {
//            Object obj = GsonUtils.fromJson(str, Object.class);
            JsonObject obj = GsonUtils.fromJson(str);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

}
