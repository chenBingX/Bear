package utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Notes:
 */
public class GsonUtils {
    private Gson gson;
    private final JsonParser jsonParser;

    private GsonUtils() {
        gson = new Gson();
        jsonParser = new JsonParser();
    }

    private static final class Holder {
        private static final GsonUtils instance = new GsonUtils();
    }

    public static String toJson(Object o) {
        return getSingleInstance().toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return getSingleInstance().fromJson(json, classOfT);
    }

    public static JsonObject fromJson(String json) {
        return Holder.instance.jsonParser.parse(json).getAsJsonObject();
    }

    public static String getString(JsonObject jsonObject, String key) {
        String r = "";
        if (jsonObject.has(key) && jsonObject.get(key) != null) {
            try {
                r = jsonObject.get(key).getAsString();
            } catch (Exception e) {
                e.printStackTrace();
                r = "";
            }
        }
        return r;
    }

    public static int getInt(JsonObject jsonObject, String key) {
        int r = 0;
        if (jsonObject.has(key) && jsonObject.get(key) != null) {
            try {
                r = jsonObject.get(key).getAsInt();
            } catch (Exception e) {
                e.printStackTrace();
                r = 0;
            }
        }
        return r;
    }


    public static double getDouble(JsonObject jsonObject, String key) {
        double r = 0;
        if (jsonObject.has(key) && jsonObject.get(key) != null) {
            try {
                r = jsonObject.get(key).getAsDouble();
            } catch (Exception e) {
                e.printStackTrace();
                r = 0;
            }
        }
        return r;
    }

    public static float getFloat(JsonObject jsonObject, String key) {
        float r = 0;
        if (jsonObject.has(key) && jsonObject.get(key) != null) {
            try {
                r = jsonObject.get(key).getAsFloat();
            } catch (Exception e) {
                e.printStackTrace();
                r = 0;
            }
        }
        return r;
    }

    public static Gson getSingleInstance() {
        return Holder.instance.gson;
    }

    public static Gson newInstance() {
        return new Gson();
    }
}
