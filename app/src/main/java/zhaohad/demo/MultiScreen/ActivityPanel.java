package zhaohad.demo.MultiScreen;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1i;

public class ActivityPanel {
    private Context mContext;

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * ShaderUtils.BYTES_PER_FLOAT;

    private static float ratio = 2.12037f;
    private static final float[] VERTEX_DATA = {
            // Order of coordinates: X, Y, S, T
            // Triangle Fan
            0f,    0f, 0.5f, 0.5f,
            -0.5f, -ratio / 2,   0f, 1f,
            0.5f, -ratio / 2,   1f, 1f,
            0.5f, ratio / 2,   1f, 0f,
            -0.5f,  ratio / 2,   0f, 0f,
            -0.5f, -ratio / 2,   0f, 1f
    };

    private VertexBuffer mVertexBuffer;
    public PanelProgram mTableProgram;

    public ActivityPanel(Context context) {
        mVertexBuffer = new VertexBuffer(VERTEX_DATA);
        mTableProgram = new PanelProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
    }

    private void bindData() {
        mVertexBuffer.setVertexAttrPointer(0, mTableProgram.maPosition, POSITION_COMPONENT_COUNT, STRIDE);
        mVertexBuffer.setVertexAttrPointer(POSITION_COMPONENT_COUNT, mTableProgram.maTextureCoordinates, TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        mTableProgram.useProgram();
        bindData();
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6);
    }


    public class PanelProgram {
        protected int mProgramId;

        private int muMatrix;
        private int maPosition;
        private int maTextureCoordinates;
        private int muTextureUnit;
        private int mTextureId;

        public PanelProgram(Context context, int vertexResId, int fragResId) {
            mProgramId = ShaderUtils.buildProgram(context, vertexResId, fragResId);
            muMatrix = GLES30.glGetUniformLocation(mProgramId, "u_Matrix");
            Log.e("hanwei", "muMatrix = " + muMatrix + " mProgram = " + mProgramId);

            muTextureUnit = GLES30.glGetUniformLocation(mProgramId, "u_TextureUnit");
            maPosition = GLES30.glGetAttribLocation(mProgramId, "a_Position");
            maTextureCoordinates = GLES30.glGetAttribLocation(mProgramId, "a_TextureCoordinates");

            // mTextureId = ShaderUtils.loadTexture(context, R.drawable.air_hockey_surface);
            mTextureId = ShaderUtils.createTexture();
            // 激活文理单元
            /*GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId);
            GLES30.glUniform1i(muTextureUnit, 0);*/

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);
            glUniform1i(muTextureUnit, 0);
        }

        public void useProgram() {
            GLES30.glUseProgram(mProgramId);
        }

        public void setUMatrix(float[] mat) {
            useProgram();
            GLES30.glUniformMatrix4fv(muMatrix, 1, false, mat, 0);
        }

        public int getTextureId() {
            return mTextureId;
        }
    }

    private class VertexBuffer {
        private FloatBuffer mBuf;

        public VertexBuffer(float[] buf) {
            mBuf = ByteBuffer
                    .allocateDirect(buf.length * ShaderUtils.BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(buf);
        }

        public void setVertexAttrPointer(int offset, int location, int componentCount, int stride) {
            mBuf.position(offset);
            GLES30.glVertexAttribPointer(location, componentCount, GLES30.GL_FLOAT, false, stride, mBuf);
            GLES30.glEnableVertexAttribArray(location);
            mBuf.position(0);
        }
    }
}
