package com.pasc.lib.hybrid.presenter;

import android.content.Context;

import com.pasc.lib.hybrid.listener.KeyboardListener;

import java.util.Collection;
import java.util.HashMap;

public class KeyBoardPresenter {
    public KeyboardListener keyboardListener; // 键盘监听
    public HashMap<String, KeyboardListener.OnKeyboardListener> keyboardListenerMap =
            new HashMap<>();

    public void addKeyboardListener(String key,
                                    KeyboardListener.OnKeyboardListener keyboardListener) {
        keyboardListenerMap.put(key, keyboardListener);
    }

    public KeyBoardPresenter(Context context){
        keyboardListener = new KeyboardListener(context);
        keyboardListener.setOnKeyboardListener(new KeyboardListener.OnKeyboardListener() {
            @Override
            public void onKeyboardOpened(int keyboardHeight) {
                Collection<KeyboardListener.OnKeyboardListener> keyboardListeners =
                        keyboardListenerMap.values();
                for (KeyboardListener.OnKeyboardListener listener : keyboardListeners) {
                    listener.onKeyboardOpened(keyboardHeight);
                }
            }

            @Override
            public void onKeyboardChanged(int keyboardHeight) {
                Collection<KeyboardListener.OnKeyboardListener> keyboardListeners =
                        keyboardListenerMap.values();
                for (KeyboardListener.OnKeyboardListener listener : keyboardListeners) {
                    listener.onKeyboardChanged(keyboardHeight);
                }
            }

            @Override
            public void onKeyboardClose() {
                Collection<KeyboardListener.OnKeyboardListener> keyboardListeners =
                        keyboardListenerMap.values();
                for (KeyboardListener.OnKeyboardListener listener : keyboardListeners) {
                    listener.onKeyboardClose();
                }
            }
        });
    }

    public void onDestory(){
        if (keyboardListener != null) {
            keyboardListener.removeGlobalLayoutListener();
        }
    }
}
