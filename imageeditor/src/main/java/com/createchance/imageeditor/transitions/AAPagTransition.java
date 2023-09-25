package com.createchance.imageeditor.transitions;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.opengl.GLES20;
import android.util.Log;

import com.createchance.imageeditor.IEManager;
import com.createchance.imageeditor.IRenderTarget;
import com.createchance.imageeditor.drawers.AbstractTransDrawer;
import com.createchance.imageeditor.drawers.BaseImageDrawer;

import org.libpag.PAGFile;
import org.libpag.PAGImage;
import org.libpag.PAGPlayer;
import org.libpag.PAGScaleMode;
import org.libpag.PAGSurface;
import org.libpag.PAGTimeStretchMode;

import javax.microedition.khronos.opengles.GL10;

public class AAPagTransition extends PagTransition{
    public AAPagTransition() {
        super(AAPagTransition.class.getSimpleName(), TRANS_ANGULAR);
    }

    @Override
    protected void getDrawer() {

    }

    private static final String TAG = "AAPagTransition";


    private int mBaseTextureId = -1;
    private BaseImageDrawer mDrawer;
    private PAGPlayer pagPlayer;
    private int lastWidth = 1, lastHeight = 1;

    private float mScaleX = 1f, mScaleY = 1f;
    private float mTranslateX = 0f, mTranslateY = 0f;
    private boolean isLoadPag = false;
    PAGFile pagFile;
    public void loadPag() {
        if (!isLoadPag && pagPlayer == null) {
            long time = System.currentTimeMillis();
            isLoadPag = true;
            pagFile = PAGFile.Load(IEManager.Companion.getInstance().getContext().getAssets(), "Template.pag");
            pagPlayer = new PAGPlayer();
            pagPlayer.setComposition(pagFile);
            pagPlayer.setScaleMode(PAGScaleMode.Stretch);
            pagPlayer.setProgress(0);
            pagPlayer.flush();
            isLoadPag = false;
        }

    }
    public void loadTexture(){
        if (mContext.getSurfaceWidth() != lastWidth || mContext.getSurfaceHeight() != lastHeight) {
            releaseTexture();
        }
        if (pagPlayer != null && mBaseTextureId == -1) {
            mBaseTextureId = initRenderTarget();
            PAGSurface pagSurface = PAGSurface.FromTexture(mBaseTextureId, mContext.getSurfaceWidth(), mContext.getSurfaceHeight());
            Log.d(TAG, "loadTexture: "+(pagSurface == null)+"  "+(pagPlayer == null)+ " "+mBaseTextureId);
            pagSurface.updateSize();
            pagPlayer.setSurface(pagSurface);
            lastWidth = mContext.getSurfaceWidth();
            lastHeight = mContext.getSurfaceHeight();
        }

    }

    @Override
    public void exec() {
        render();
        Log.d(TAG, "exec: ");
    }

    @Override
    public boolean isReady() {
        return pagPlayer != null;
    }

    @Override
    public void setProgress(float progress) {
        Log.d(TAG, "setProgress: "+progress);
        loadTexture();
        if (mDrawer == null) {
            mDrawer = new BaseImageDrawer();
        }
        int texture2 = mContext.getToTextureId();
        int texture = mContext.getFromTextureId();
        long time = System.currentTimeMillis();

        if (texture2 >= 0 && pagImage == null && pagFile != null){
            pagImage = PAGImage.FromTexture(texture2,GLES20.GL_TEXTURE_2D,mContext.getSurfaceWidth(),mContext.getSurfaceHeight(),false);
            pagImage.setScaleMode(PAGScaleMode.Stretch);
            pagFile.replaceImage(0,pagImage);
            Log.d(TAG, "setProgressTime: "+(System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }

//        if (texture >= 0 && pagImage2 == null && pagFile != null){
//            pagImage2 = PAGImage.FromTexture(texture,GLES20.GL_TEXTURE_2D,mContext.getSurfaceWidth(),mContext.getSurfaceHeight(),false);
//            pagImage2.setScaleMode(PAGScaleMode.Stretch);
//            pagFile.replaceImage(1,pagImage2);
//            Log.d(TAG, "setProgressTime2: "+(System.currentTimeMillis() - time));
//            time = System.currentTimeMillis();
//        }

        if (pagPlayer != null){
            Log.d(TAG, "setProgress: "+mBaseTextureId);
            pagPlayer.setProgress(progress);
            Log.d(TAG, "setProgressTime4: "+(System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
            pagPlayer.flush();
            Log.d(TAG, "setProgressTime5: "+(System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }
        Log.d(TAG, "setProgressTime6: "+(System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
    }
    PAGImage pagImage;
    PAGImage pagImage2;

    void render() {

        mContext.attachOffScreenTexture(mContext.getOutputTextureId());
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//        GLES20.glEnable(GLES20.GL_BLEND);
        mDrawer.draw(mBaseTextureId,
                0,
                0,
                mContext.getSurfaceWidth(),
                mContext.getSurfaceHeight(),
                true,
                mScaleX,
                mScaleY,
                mTranslateX,
                mTranslateY);
        mContext.swapTexture();


    }

    private int initRenderTarget() {
        int id[] = {0};
        GLES20.glGenTextures(1, id, 0);
        if (id[0] == 0) {
            return 0;
        }
        int textureId = id[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mContext.getSurfaceWidth(),
                mContext.getSurfaceHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureId;
    }

    @Override
    public void release() {
        releaseData();
        releaseTexture();
    }

    void releaseData() {
        if (pagPlayer != null) {
            pagPlayer.release();
            pagPlayer = null;
        }
    }

    void releaseTexture() {
        if (mBaseTextureId != -1) {
            GLES20.glDeleteTextures(1, new int[]{mBaseTextureId}, 0);
            mBaseTextureId = -1;
        }
    }

}
