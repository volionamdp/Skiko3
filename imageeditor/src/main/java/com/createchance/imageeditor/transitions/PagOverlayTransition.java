package com.createchance.imageeditor.transitions;

import android.util.Log;

import com.createchance.imageeditor.EglUtil;
import com.createchance.imageeditor.IEManager;
import com.createchance.imageeditor.drawers.BaseImageDrawer;
import com.createchance.imageeditor.drawers.PagOverlayDrawer;
import com.volio.vn.models.transition.TransitionPagOverlayEditModel;

import org.libpag.PAGFile;
import org.libpag.PAGPlayer;
import org.libpag.PAGScaleMode;
import org.libpag.PAGSurface;

public class PagOverlayTransition extends PagTransition {
    private static final String TAG = "PagOverlayTransition";
    private final TransitionPagOverlayEditModel pagOverlay;
    private static PagOverlayDrawer mDrawer;

    public PagOverlayTransition(TransitionPagOverlayEditModel pagOverlayEditModel) {
        super(PagOverlayTransition.class.getSimpleName(), TRANS_ANGULAR);
        pagOverlay = pagOverlayEditModel;
    }

    @Override
    protected void getDrawer() {

    }

    private PAGFile pagFile;
    private boolean isLoadPag = false;
    private int fromTexture;
    private int toTexture;
    private boolean isAllowDraw = true;
    private long lastTimeLoad = 0;

    public void loadPag() {
        if (!isLoadPag && pagFile == null) {
            long time = System.nanoTime();
            isAllowDraw = false;
            isLoadPag = true;
            if (pagOverlay.isAsset()) {
                pagFile = PAGFile.Load(IEManager.Companion.getInstance().getContext().getAssets(), pagOverlay.getLocalPath());
            } else {
                pagFile = PAGFile.Load(pagOverlay.getLocalPath());
            }
            if (pagPlayer == null) {
                pagPlayer = new PAGPlayer();
            }
            isAllowDraw = true;
            isLoadPag = false;
            lastTimeLoad = System.currentTimeMillis();
            Log.d(TAG, "loadPag: " + (System.nanoTime() - time) / 1000);
        }
        if (!pagOverlay.getLocalPath().equals(currentTransitionPath) && pagFile != null) {
            currentTransitionPath = pagOverlay.getLocalPath();
            pagPlayer.setComposition(pagFile);
            pagPlayer.setCacheEnabled(true);
            pagPlayer.setScaleMode(PAGScaleMode.Zoom);
            pagPlayer.prepare();
        }


    }

    public void loadTexture() {
        if (pagPlayer != null && (mContext.getSurfaceWidth() != lastWidthSurface || mContext.getSurfaceHeight() != lastHeightSurface || pagSurface == null)) {
            Log.d(TAG, "loadTexture: ");
            releaseSurface();
            EglUtil.updateTextures(transOverlayId, mContext.getSurfaceWidth(), mContext.getSurfaceHeight());
            pagSurface = PAGSurface.FromTexture(transOverlayId[0], mContext.getSurfaceWidth(), mContext.getSurfaceHeight());
            pagSurface.updateSize();
            pagPlayer.setSurface(pagSurface);
            lastWidthSurface = mContext.getSurfaceWidth();
            lastHeightSurface = mContext.getSurfaceHeight();
        }

    }

    @Override
    public void exec() {
        render();
    }

    @Override
    public boolean isReady() {
        return System.currentTimeMillis() - lastTimeLoad > 50;
    }

    @Override
    public void setProgress(float progress) {
        Log.d(TAG, "setProgress: " + progress);
        loadTexture();
        if (pagPlayer != null && isAllowDraw) {
            pagPlayer.setProgress(progress);
            pagPlayer.flush();
        }
        fromTexture = mContext.getFromTextureId();
        toTexture = mContext.getToTextureId();

    }

    void render() {
        if (mDrawer == null) {
            mDrawer = new PagOverlayDrawer();
        }
        if (mDrawDefault == null) {
            mDrawDefault = new BaseImageDrawer();
        }
        mContext.attachOffScreenTexture(mContext.getOutputTextureId());
        if (isAllowDraw) {
            mDrawer.draw(transOverlayId[0], fromTexture, toTexture,
                    0,
                    0,
                    mContext.getSurfaceWidth(),
                    mContext.getSurfaceHeight(),
                    true,
                    1f,
                    1f,
                    0f,
                    0f);
        } else {
            mDrawDefault.draw(fromTexture, 0, 0, mContext.getSurfaceWidth(),
                    mContext.getSurfaceHeight(),
                    true,
                    1f,
                    1f,
                    0f,
                    0f);
        }
        mContext.swapTexture();
    }


    @Override
    public void release() {
        releaseSurface();
        pagFile = null;
    }

    void releaseSurface() {
        if (pagSurface != null) {
            pagSurface.clearAll();
            pagSurface.freeCache();
            pagSurface.release();
        }

    }


}
