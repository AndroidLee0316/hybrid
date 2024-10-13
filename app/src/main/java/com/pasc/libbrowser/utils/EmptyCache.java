package com.pasc.libbrowser.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by huanglihou519 on 2018/6/18.
 */

public class EmptyCache extends ACache {
    private static final EmptyCache emptyCache = new EmptyCache(null, 0,0);

    public static EmptyCache getInstance(){
        return emptyCache;
    }

    public EmptyCache(File cacheDir, long max_size, int max_count) {
        super(cacheDir, max_size, max_count);
    }

    @Override public void put(String key, String value) {

    }

    @Override public void put(String key, String value, int saveTime) {

    }

    @Override public String getAsString(String key) {
        return null;
    }

    @Override public void put(String key, JSONObject value) {

    }

    @Override public void put(String key, JSONObject value, int saveTime) {

    }

    @Override public JSONObject getAsJSONObject(String key) {
        return null;
    }

    @Override public void put(String key, JSONArray value) {

    }

    @Override public void put(String key, JSONArray value, int saveTime) {

    }

    @Override public JSONArray getAsJSONArray(String key) {
        return null;
    }

    @Override public void put(String key, byte[] value) {

    }

    @Override public OutputStream put(String key) throws FileNotFoundException {
        return null;
    }

    @Override public InputStream get(String key) throws FileNotFoundException {
        return null;
    }

    @Override public void put(String key, byte[] value, int saveTime) {

    }

    @Override public byte[] getAsBinary(String key) {
        return null;
    }

    @Override public void put(String key, Serializable value) {

    }

    @Override public void put(String key, Serializable value, int saveTime) {

    }

    @Override public Object getAsObject(String key) {
        return null;
    }

    @Override public void put(String key, Bitmap value) {

    }

    @Override public void put(String key, Bitmap value, int saveTime) {

    }

    @Override public Bitmap getAsBitmap(String key) {
        return null;
    }

    @Override public void put(String key, Drawable value) {

    }

    @Override public void put(String key, Drawable value, int saveTime) {

    }

    @Override public Drawable getAsDrawable(String key) {
        return null;
    }

    @Override public File file(String key) {
        return null;
    }

    @Override public boolean remove(String key) {
        return true;
    }

    @Override public void clear() {

    }
}
