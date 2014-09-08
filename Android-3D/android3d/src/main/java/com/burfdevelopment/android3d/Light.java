package com.burfdevelopment.android3d;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Light {

    private Context mContext = null;

    private FloatBuffer mVertices;

    private int mProgram,
            maPositionHandle,
            muPMatrixHandle,
            muMMatrixHandle,
            muVMatrixHandle;

    private float[] mMMatrix = new float[16];

    /**
     * Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     * we multiply this by our transformation matrices.
     */
    private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    /**
     * Used to hold the current position of the light in world space (after transformation via model matrix).
     */
    private final float[] mLightPosInWorldSpace = new float[4];

    /**
     * Used to hold the transformed position of the light in eye space (after transformation via modelview matrix)
     */
    private final float[] mLightPosInEyeSpace = new float[4];

    private float[] lightColor;
    private float[] lightAmbient;
    private float[] lightDiffuse;

    public Light(Context context) {
        mContext = context;
        initLight();
    }

    public float[] getLightPos() {
        return mLightPosInEyeSpace;
    }

    private void initLight() {
        float[] pointVerts =
                {
                        0f, 0f, 0f,
                };
        mVertices = ByteBuffer.allocateDirect(1 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(pointVerts).position(0);

        int vertexShader = Shapes.loadShader(mContext, GLES20.GL_VERTEX_SHADER, R.raw.vs_point);
        int fragmentShader = Shapes.loadShader(mContext, GLES20.GL_FRAGMENT_SHADER, R.raw.ps_point);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        //get handle to the vertex shaders vPos member
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        muVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uVMatrix");
        muPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uPMatrix");

        Matrix.setIdentityM(mMMatrix, 0);

    }

    public void draw(float[] mVMatrix, float[] mProjMatrix) {
        //Add program
        GLES20.glUseProgram(mProgram);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mMMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mVMatrix, 0, mLightPosInWorldSpace, 0);

//		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertices);
//		GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttrib3f(maPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);
        GLES20.glDisableVertexAttribArray(maPositionHandle);

        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, mMMatrix, 0);
        GLES20.glUniformMatrix4fv(muVMatrixHandle, 1, false, mVMatrix, 0);
        GLES20.glUniformMatrix4fv(muPMatrixHandle, 1, false, mProjMatrix, 0);

        //Draw the light
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    public Light reset() {
        Matrix.setIdentityM(mMMatrix, 0);
        return this;
    }

    public Light translate(float x, float y, float z) {
        Matrix.translateM(mMMatrix, 0, x, y, z);
        return this;
    }

    public Light rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mMMatrix, 0, angle, x, y, z);
        return this;
    }

    public Light scale(float x, float y, float z) {
        Matrix.scaleM(mMMatrix, 0, x, y, z);
        return this;
    }
}
