package com.hank.corelib.parser;

import java.lang.reflect.Type;

/**
 * with this, we can change json handler easily.
 * alibaba fastjson can not handle private attribute that without getter method.
 * so we choice the google gson.
 *
 * @author Hank
 *         2014-1-14下午11:32:32
 */
public abstract class Json {
    private static Json json;

    /**
     * set new json instance
     *
     * @param json new instance
     * @return new instance
     */
    public static Json set(Json json) {
        Json.json = json;
        return Json.json;
    }

    /**
     * set default json handler: Google Gson
     */
    public static Json setDefault() {
        Json.json = new GsonImpl();
        return Json.json;
    }

    /**
     * get default json handler
     *
     * @return Json
     */
    public static Json get() {
        if (json == null) {
            json = new GsonImpl();
        }
        return json;
    }

    public abstract String toJson(Object src);

    public abstract <T> T toObject(String json, Class<T> claxx);

    public abstract <T> T toObject(String json, Type claxx);

    public abstract <T> T toObject(byte[] bytes, Class<T> claxx);
}
