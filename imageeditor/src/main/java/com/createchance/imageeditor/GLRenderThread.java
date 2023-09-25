package com.createchance.imageeditor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.createchance.imageeditor.gles.EglCore;

/**
 * OpenGl render thread.
 *
 * @author createchance
 * @date 2018/12/24
 */
public class GLRenderThread extends HandlerThread {

    private static final String TAG = "GLRenderThread";

    private final int MSG_START = 1;
    private final int MSG_STOP = 2;

    private EglCore mEglCore;

    private Handler mHandler;

    public GLRenderThread() {
        this("GLRenderThread", Thread.NORM_PRIORITY);
    }

    public GLRenderThread(String name, int priority) {
        super(name, priority);
    }

    public void post(Runnable task) {
        if (mHandler == null) start();
        mHandler.post(task);
    }

    @Override
    public synchronized void start() {
        super.start();

        mHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_START:
                        // init egl.
                        mEglCore = new EglCore();
                        Log.d(TAG, "handleMessage: MSG_START");
                        break;
                    case MSG_STOP:
                        mEglCore.release();
                        boolean check = quitSafely();
                        Log.d(TAG, "handleMessage: MSG_STOP "+check);
                        break;
                    default:
                        break;
                }
            }
        };

        mHandler.sendEmptyMessage(MSG_START);
    }

    @Override
    public boolean quit() {
        return true;
    }

    public void stopThread(){
        mHandler.sendEmptyMessage(MSG_STOP);
    }

    public EglCore getEglCore() {
        return mEglCore;
    }
}
