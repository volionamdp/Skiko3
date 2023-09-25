package com.createchance.imageeditor;

import android.content.Context;
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.createchance.imageeditor.gles.EglCore;
import com.createchance.imageeditor.gles.WindowSurface;

public class IESurfaceView extends SurfaceView implements IRenderTarget {
    private final int[] mOffScreenFrameBuffer = new int[1];
    private static final int[] mOffScreenTextureIds = new int[3];
    private int mInputTextureIndex = 0, mOutputTextureIndex = 1;
    private boolean isInitSuccess = false;

    private WindowSurface mWindowSurface;

    public IESurfaceView(Context context) {
        super(context);
    }

    public IESurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IESurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        IEManager.Companion.getInstance().runRenderThread(() -> {
            EglUtil.updateTextures(mOffScreenTextureIds, getWidth(), getHeight());
            IEManager.Companion.getInstance().renderCurrentFrame();
        });
    }

    @Override
    public void makeCurrent() {
        if (mWindowSurface != null) {
            mWindowSurface.makeCurrent();
        }
    }

    @Override
    public void swapBuffers() {
        if (mWindowSurface != null) {
            mWindowSurface.swapBuffers();
        }
    }

    @Override
    public void init(EglCore eglCore) {
        mWindowSurface = new WindowSurface(eglCore, getHolder().getSurface());
        mWindowSurface.makeCurrent();
        createOffScreenFrameBuffer();
        EglUtil.updateTextures(mOffScreenTextureIds, getWidth(), getHeight());
        isInitSuccess = true;
//        createOffScreenTextures();
    }

    @Override
    public int getSurfaceWidth() {
        return getWidth();
    }

    @Override
    public int getSurfaceHeight() {
        return getHeight();
    }

    @Override
    public int getInputTextureId() {
        return mOffScreenTextureIds[mInputTextureIndex];
    }

    @Override
    public int getOutputTextureId() {
        return mOffScreenTextureIds[mOutputTextureIndex];
    }

    @Override
    public int getTempleTextureId() {
        return mOffScreenTextureIds[2];
    }

    @Override
    public void swapTexture() {
        int tmp = mInputTextureIndex;
        mInputTextureIndex = mOutputTextureIndex;
        mOutputTextureIndex = tmp;
    }

    @Override
    public void release() {
        deleteOffScreenFrameBuffer();
        if (mWindowSurface != null) {
            mWindowSurface.release();
        }
        isInitSuccess = false;
    }

    @Override
    public boolean isPreview() {
        return true;
    }

    @Override
    public boolean isInitSuccess() {
        return isInitSuccess;
    }

    @Override
    public void bindOffScreenFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mOffScreenFrameBuffer[0]);
    }

    @Override
    public void attachOffScreenTexture(int textureId) {
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);
    }

    @Override
    public void bindDefaultFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void createOffScreenFrameBuffer() {
        GLES20.glGenFramebuffers(mOffScreenFrameBuffer.length, mOffScreenFrameBuffer, 0);
    }

    private void deleteOffScreenFrameBuffer() {
        GLES20.glDeleteFramebuffers(mOffScreenFrameBuffer.length, mOffScreenFrameBuffer, 0);
    }
}
