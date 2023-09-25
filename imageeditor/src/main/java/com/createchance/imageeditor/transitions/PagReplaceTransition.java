package com.createchance.imageeditor.transitions;

import android.opengl.GLES20;

import com.createchance.imageeditor.EglUtil;
import com.createchance.imageeditor.IEManager;
import com.createchance.imageeditor.drawers.BaseImageDrawer;
import com.volio.vn.models.transition.TransitionPagOverlayEditModel;

import org.libpag.PAGFile;
import org.libpag.PAGImage;
import org.libpag.PAGPlayer;
import org.libpag.PAGScaleMode;
import org.libpag.PAGSurface;

public class PagReplaceTransition extends PagTransition {
    private static final String TAG = "PagOverlayTransition";
    private final TransitionPagOverlayEditModel pagOverlay;
    protected static BaseImageDrawer mDrawer;

    public PagReplaceTransition(TransitionPagOverlayEditModel pagOverlayEditModel) {
        super(PagReplaceTransition.class.getSimpleName(), TRANS_ANGULAR);
        pagOverlay = pagOverlayEditModel;
    }

    @Override
    protected void getDrawer() {

    }

    private int baseId = -1;
    private PAGFile pagFile;
    private int lastWidth = 1, lastHeight = 1;
    private boolean isLoadPag = false;
    private boolean isUpdatePag = false;
    private PAGSurface pagSurface;
    private PAGImage firsImage;
    private PAGImage lastImage;

    public void loadPag() {
        if (!isLoadPag && pagFile == null) {
            isUpdatePag = true;
            if (pagOverlay.isAsset()) {
                pagFile = PAGFile.Load(IEManager.Companion.getInstance().getContext().getAssets(), pagOverlay.getLocalPath());
            } else {
                pagFile = PAGFile.Load(pagOverlay.getLocalPath());
            }
            if (pagPlayer == null) {
                pagPlayer = new PAGPlayer();
            }
            pagPlayer.setComposition(pagFile);
            pagPlayer.setScaleMode(PAGScaleMode.LetterBox);
            pagPlayer.setProgress(0);
            pagPlayer.flush();
            isUpdatePag = true;
            isLoadPag = false;
        }

    }

    public void loadTexture() {
        if (pagPlayer != null && (mContext.getSurfaceWidth() != lastWidth || mContext.getSurfaceHeight() != lastHeight || isUpdatePag)) {
            EglUtil.updateTextures(transOverlayId, mContext.getSurfaceWidth(), mContext.getSurfaceHeight());
            pagSurface = PAGSurface.FromTexture(transOverlayId[0], mContext.getSurfaceWidth(), mContext.getSurfaceHeight());
            pagSurface.updateSize();
            pagPlayer.setSurface(pagSurface);
            lastWidth = mContext.getSurfaceWidth();
            lastHeight = mContext.getSurfaceHeight();
            isUpdatePag = false;
        }

    }

    @Override
    public void exec() {
        render();
    }

    @Override
    public boolean isReady() {
        return pagPlayer != null;
    }

    @Override
    public void setProgress(float progress) {
        loadTexture();
        long time = System.currentTimeMillis();

        mContext.getToTextureId();
        if (lastImage == null && pagFile != null) {
            lastImage = PAGImage.FromTexture(mContext.getToTextureId(), GLES20.GL_TEXTURE_2D, mContext.getSurfaceWidth(), mContext.getSurfaceHeight());
            lastImage.setScaleMode(PAGScaleMode.Stretch);
            pagFile.replaceImage(0, lastImage);
        }

        if (firsImage == null && pagFile != null) {
            firsImage = PAGImage.FromTexture(mContext.getFromTextureId(), GLES20.GL_TEXTURE_2D, mContext.getSurfaceWidth(), mContext.getSurfaceHeight());
            firsImage.setScaleMode(PAGScaleMode.Stretch);
            pagFile.replaceImage(1, firsImage);
        }

        if (pagPlayer != null) {
            pagPlayer.setProgress(progress);
            time = System.currentTimeMillis();
            pagPlayer.flush();
//            Log.d(TAG, "setProgressTime5: " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }
//        Log.d(TAG, "setProgressTime6: " + (transOverlayId[0]));
        time = System.currentTimeMillis();
    }

    void render() {
        if (mDrawer == null) {
            mDrawer = new BaseImageDrawer();
        }
        mContext.attachOffScreenTexture(mContext.getOutputTextureId());

        mDrawer.draw(transOverlayId[0],
                0,
                0,
                mContext.getSurfaceWidth(),
                mContext.getSurfaceHeight(),
                true,
                1f,
                1f,
                0f,
                0f);
        mContext.swapTexture();
//        mDrawer.draw(baseId,
//                300,
//                300,
//                mContext.getSurfaceWidth(),
//                mContext.getSurfaceHeight(),
//                true,
//                1f,
//                1f,
//                0f,
//                0f);
    }


    @Override
    public void release() {
        releaseData();
    }

    void releaseData() {
        if (pagSurface != null) {
            pagSurface.clearAll();
            pagSurface.freeCache();
            pagSurface.release();
        }
        if (lastImage != null) {
            lastImage.release();
            lastImage = null;
        }
        if (firsImage != null) {
            firsImage.release();
            firsImage = null;
        }
        pagFile = null;

    }


}
