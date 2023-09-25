package com.createchance.imageeditor.shaders;

import android.opengl.GLES20;

/**
 * ${DESC}
 *
 * @author createchance
 * @date 2018/11/10
 */
public class PagOverlayFragmentShader extends AbstractShader {
    private final String FRAGMENT_SHADER = "PagOverlayFragmentShader.glsl";

    private final String U_PAG_TEXTURE = "u_PagTexture";
    private final String U_FROM_TEXTURE = "u_FromTexture";
    private final String U_TO_TEXTURE = "u_ToTexture";

    private int mUPagTexture;
    private int mUFromTexture;
    private int mUToTexture;

    public PagOverlayFragmentShader() {
        initShader(FRAGMENT_SHADER, GLES20.GL_FRAGMENT_SHADER);
    }

    @Override
    public void initLocation(int programId) {
        mUPagTexture = GLES20.glGetUniformLocation(programId, U_PAG_TEXTURE);
        mUFromTexture = GLES20.glGetUniformLocation(programId, U_FROM_TEXTURE);
        mUToTexture = GLES20.glGetUniformLocation(programId, U_TO_TEXTURE);
    }

    public void setUInputTexture(int textureTarget, int pagTextureId, int fromTextureId, int toTextureId) {
        // bind pagTextureId
        GLES20.glActiveTexture(textureTarget);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, pagTextureId);
        GLES20.glUniform1i(mUPagTexture, textureTarget - GLES20.GL_TEXTURE0);
        // bind fromTextureId
        GLES20.glActiveTexture(textureTarget + 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fromTextureId);
        GLES20.glUniform1i(mUFromTexture, textureTarget - GLES20.GL_TEXTURE0 + 1);
        // bind toTextureId
        GLES20.glActiveTexture(textureTarget + 2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, toTextureId);
        GLES20.glUniform1i(mUToTexture, textureTarget - GLES20.GL_TEXTURE0 + 2);
    }
}
