package com.createchance.imageeditor.shaders;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * Transition main vertex shader.
 *
 * @author createchance
 * @date 2018/12/23
 */
public class TransitionMainVertexShader extends AbstractShader {

    private static final String TAG = "TransitionMainVertexSha";

    private final String VERTEX_SHADE = "TransitionMainVertexShader.glsl";

    // attrs and uniforms
    private final String A_POSITION = "a_Position";
    private final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    private int mAPosition, mATextureCoordinates;

    public TransitionMainVertexShader() {
        initShader(new String[]{TRANSITION_FOLDER + VERTEX_SHADE}, GLES20.GL_VERTEX_SHADER);
    }

    @Override
    public void initLocation(int programId) {
        mAPosition = GLES20.glGetAttribLocation(programId, A_POSITION);
        mATextureCoordinates = GLES20.glGetAttribLocation(programId, A_TEXTURE_COORDINATES);
    }

    public void setAPosition(FloatBuffer buffer) {
        GLES20.glEnableVertexAttribArray(mAPosition);
        buffer.position(0);
        GLES20.glVertexAttribPointer(
                mAPosition,
                2,
                GLES20.GL_FLOAT,
                false,
                0,
                buffer);
    }

    public void setATextureCoordinates(FloatBuffer buffer) {
        GLES20.glEnableVertexAttribArray(mATextureCoordinates);
        buffer.position(0);
        GLES20.glVertexAttribPointer(
                mATextureCoordinates,
                2,
                GLES20.GL_FLOAT,
                false,
                0,
                buffer);
    }

    public void unsetAPosition() {
        GLES20.glDisableVertexAttribArray(mAPosition);
    }

    public void unsetATextureCoordinates() {
        GLES20.glDisableVertexAttribArray(mATextureCoordinates);
    }
}
