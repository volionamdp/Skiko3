package com.createchance.imageeditor.shaders;

import android.opengl.GLES20;

/**
 * ${DESC}
 *
 * @author createchance
 * @date 2018/11/10
 */
public class MixFragmentShader extends AbstractShader {
    private final String FRAGMENT_SHADER = "MixFragmentShader.glsl";

    private final String U_INPUT_TEXTURE = "u_InputTexture";
    private final String S_INPUT_TEXTURE = "s_InputTexture";

    private int mUInputTexture;
    private int mSInputTexture;

    public MixFragmentShader() {
        initShader(FRAGMENT_SHADER, GLES20.GL_FRAGMENT_SHADER);
    }

    @Override
    public void initLocation(int programId) {
        mUInputTexture = GLES20.glGetUniformLocation(programId, U_INPUT_TEXTURE);
        mSInputTexture = GLES20.glGetUniformLocation(programId, S_INPUT_TEXTURE);
    }

    public void setUInputTexture(int textureTarget,int textureBaseId,int textureTopId) {
        // bind texture
        GLES20.glActiveTexture(textureTarget);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBaseId);
        GLES20.glUniform1i(mSInputTexture, textureTarget - GLES20.GL_TEXTURE0);

        GLES20.glActiveTexture(textureTarget+1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureTopId);
        GLES20.glUniform1i(mUInputTexture, textureTarget - GLES20.GL_TEXTURE0+1);
    }
}
