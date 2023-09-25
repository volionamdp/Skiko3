//package com.createchance.imageeditor;
//
//import android.content.Context;
//import android.graphics.SurfaceTexture;
//import android.view.SurfaceHolder;
//import android.view.TextureView;
//
//import com.createchance.imageeditor.freetype.FreeType;
//import com.createchance.imageeditor.framerender.IEClip;
//import com.createchance.imageeditor.framerender.IERender;
//import com.createchance.imageeditor.ops.AbstractOperator;
//import com.createchance.imageeditor.transitions.AbstractTransition;
//import com.createchance.imageeditor.utils.Logger;
//import com.volio.vn.models.ProjectModel;
//
//import java.io.File;
//import java.util.List;
//
//
///**
// * IE manager, api class.
// *
// * @author createchance
// * @date 2018/10/28
// */
//public class IEManager {
//
//    private static final String TAG = "IEManager";
//
//    private Context mAppContext;
//    private IRenderTarget mPreviewTarget;
//    private IRenderTarget mSaveTarget;
//
//    private GLRenderThread mRenderThread;
//
//    private IEManager() {
//        // init freetype
//    }
//
//    public static IEManager getInstance() {
//        return Holder.sInstance;
//    }
//
//    public void init(Context context) {
//        mAppContext = context.getApplicationContext();
//
//    }
//
//    public void setData(ProjectModel projectModel){
//        ieRender.setData(projectModel);
//    }
//    public void startEngine() {
//        mRenderThread = new GLRenderThread();
//        mRenderThread.start();
//    }
//
//    public void stopEngine() {
//        if (mRenderThread == null) {
//            Logger.e(TAG, "Call startEngine first!!!");
//            return;
//        }
//        // release resources.
//        mRenderThread.post(new Runnable() {
//            @Override
//            public void run() {
//                ieRender.release();
//            }
//        });
//        ieRender.clearData();
//        mRenderThread.quitSafely();
//    }
//
//    private IERender ieRender = new IERender();
//
//    public void attachPreview(IEPreviewView previewView) {
//        mPreviewTarget = previewView;
//        previewView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
//            @Override
//            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//                Logger.d(TAG, "onSurfaceTextureAvailable, width: " + width + ", height: " + height);
//                mRenderThread.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mPreviewTarget.init(mRenderThread.getEglCore());
//                        mPreviewTarget.makeCurrent();
//                        ieRender.setTarget(mPreviewTarget);
//                    }
//                });
//            }
//
//            @Override
//            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//                Logger.d(TAG, "onSurfaceTextureSizeChanged, width: " + width + ", height: " + height);
//                // todo: reset env when surface texture size changed.
//            }
//
//            @Override
//            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//                Logger.d(TAG, "onSurfaceTextureDestroyed: ");
//                return true;
//            }
//
//            @Override
//            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//                Logger.d(TAG, "onSurfaceTextureUpdated: ");
//            }
//        });
//    }
//
//    public void detachPreview(IEPreviewView previewView) {
//        mRenderThread.post(new Runnable() {
//            @Override
//            public void run() {
//                mPreviewTarget.release();
//                mPreviewTarget = null;
//            }
//        });
//    }
//
//    public void attachPreview(IESurfaceView previewView) {
//        mPreviewTarget = previewView;
//        previewView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                mRenderThread.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mPreviewTarget.init(mRenderThread.getEglCore());
//                        mPreviewTarget.makeCurrent();
//                        ieRender.setTarget(mPreviewTarget);
//                    }
//                });
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });
//    }
//
//    public void detachPreview(IESurfaceView previewView) {
//        mRenderThread.post(new Runnable() {
//            @Override
//            public void run() {
//                mPreviewTarget.release();
//                mPreviewTarget = null;
//            }
//        });
//    }
//
//    public long getTotalDuration() {
//        return ieRender.getTotalDuration();
//
//    }
//    public List<IEClip> getClipList() {
//        return ieRender.getClipList();
//    }
//
//    public IEClip getClip(int clipIndex) {
//        return ieRender.getClip(clipIndex);
//    }
//
//    public boolean saveAsVideo(int width,
//                               int height,
//                               int orientation,
//                               File target,
//                               File bgmFile,
//                               long bgmStartTimeMs,
//                               SaveListener saveListener) {
//        if (width <= 0 || height <= 0) {
//            Logger.e(TAG, "Output size invalid, width: " + width + ", height: " + height);
//        }
//        if (target == null) {
//            Logger.e(TAG, "Target file can not be null!");
//            return false;
//        }
//
//        long totalDuration = ieRender.getTotalDuration();
//        mSaveTarget = new VideoSaver(width, height, orientation, totalDuration, target, bgmFile, bgmStartTimeMs, saveListener);
//
//        ieRender.setTarget(mSaveTarget);
//        mRenderThread.post(new Runnable() {
//            @Override
//            public void run() {
//                ieRender.release();
//                mSaveTarget.init(mRenderThread.getEglCore());
//                mSaveTarget.makeCurrent();
//                long curTime = 0;
//                while (curTime < getTotalDuration()){
//                    ieRender.renderAtPosition(curTime,curTime+20,true);
//                    curTime += 20;
//                }
//                mSaveTarget.release();
//                mSaveTarget = null;
//                ieRender.release();
//                mPreviewTarget.makeCurrent();
//                ieRender.setTarget(mPreviewTarget);
//            }
//        });
//
//        return true;
//    }
//
//    public Context getContext() {
//        return mAppContext;
//    }
//
//    public void runRenderThread(Runnable runnable) {
//        mRenderThread.post(runnable);
//    }
//
//    void renderAtPosition(final long position,final long positionNextFrame) {
//        ieRender.renderAtPosition(position,positionNextFrame,false);
//    }
//
//
//    private static class Holder {
//        private static final IEManager sInstance = new IEManager();
//    }
//}
