package com.createchance.imageeditor.drawers;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.createchance.imageeditor.shaders.ModelViewVertexShader;
import com.createchance.imageeditor.shaders.PagOverlayFragmentShader;

import java.nio.FloatBuffer;

/**
 * ${DESC}
 *
 * @author createchance
 * @date 2018/10/29
 */
public class PagOverlayDrawer extends AbstractDrawer {

    private static final String TAG = "BaseImageDrawer";

    private FloatBuffer mVertexPositionBuffer;

    private FloatBuffer mTextureCoordinateBuffer;
    private FloatBuffer mTextureCoordinateBufferFlipped;

    private ModelViewVertexShader mVertexShader;
    private PagOverlayFragmentShader mFragmentShader;

    private float[] mModelMatrix, mViewMatrix, mProjectionMatrix;

    public PagOverlayDrawer() {
        mVertexShader = new ModelViewVertexShader();
        mFragmentShader = new PagOverlayFragmentShader();
        loadProgram(mVertexShader.getShaderId(), mFragmentShader.getShaderId());
        mVertexShader.initLocation(mProgramId);
        mFragmentShader.initLocation(mProgramId);

        mVertexPositionBuffer = createFloatBuffer(
                new float[]{
                        -1.0f, 1.0f,
                        -1.0f, -1.0f,
                        1.0f, 1.0f,
                        1.0f, -1.0f,
                }
        );
        mTextureCoordinateBufferFlipped = createFloatBuffer(
                new float[]{
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        1.0f, 1.0f,
                }
        );
        mTextureCoordinateBuffer = createFloatBuffer(
                new float[]{
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                }
        );

        mModelMatrix = new float[16];
        mViewMatrix = new float[16];
        mProjectionMatrix = new float[16];
    }

    public void draw(int pagTextureId,
                     int fromTextureId,
                     int toTextureId,
                     int posX,
                     int posY,
                     int width,
                     int height,
                     boolean flipY,
                     float scaleX,
                     float scaleY,
                     float translateX,
                     float translateY) {
        GLES20.glUseProgram(mProgramId);

        GLES20.glViewport(posX, posY, width, height);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, scaleX, scaleY, 1.0f);
        Matrix.translateM(mViewMatrix, 0, translateX, translateY, 0);
        mVertexShader.setUModelMatrix(mModelMatrix);
        mVertexShader.setUViewMatrix(mViewMatrix);
        mVertexShader.setUProjectionMatrix(mProjectionMatrix);
        mVertexShader.setAPosition(mVertexPositionBuffer);
        if (flipY) {
            mVertexShader.setATextureCoordinates(mTextureCoordinateBufferFlipped);
        } else {
            mVertexShader.setATextureCoordinates(mTextureCoordinateBuffer);
        }
        mFragmentShader.setUInputTexture(GLES20.GL_TEXTURE0, pagTextureId, fromTextureId, toTextureId);

        // draw it.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        mVertexShader.unsetAPosition();
        mVertexShader.unsetATextureCoordinates();
    }
}
