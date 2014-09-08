package com.burfdevelopment.android3d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class SurfaceView extends GLSurfaceView {

    private final com.burfdevelopment.android3d.Renderer mRenderer;
    private final float SCREEN_DENSITY;
    private boolean mFOVIPDenabled = false;

    public SurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        mRenderer = new com.burfdevelopment.android3d.Renderer(getContext());
        setRenderer(mRenderer);

        this.setKeepScreenOn(true);

        SCREEN_DENSITY = context.getResources().getDisplayMetrics().density;

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public com.burfdevelopment.android3d.Renderer getRenderer() {
        return mRenderer;
    }

    public boolean isFOVIPDenabled() {
        return mFOVIPDenabled;
    }

    public void setFOVIPDenabled(boolean enabled) {
        mFOVIPDenabled = enabled;
    }

    private float mPreviousX1;
    private float mPreviousY1;
    private float mPreviousX2;
    private float mPreviousY2;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        int pointerId;

        float x1 = -1;
        float y1 = -1;
        float x2 = -1;
        float y2 = -1;

        for (int i = 0; i < ev.getPointerCount(); i++) {
            pointerId = ev.getPointerId(i);
            if (pointerId == 0) {
                x1 = ev.getX(i);
                y1 = ev.getY(i);
            }
            if (pointerId == 1) {
                x2 = ev.getX(i);
                y2 = ev.getY(i);
            }
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE:

                float dx = 0, dy = 0, x = 0, y = 0;
                boolean processTouch = false;

                for (int i = 0; i < ev.getPointerCount(); i++) {
                    pointerId = ev.getPointerId(i);
                    if (pointerId == 0) {
                        dx = x1 - mPreviousX1;
                        dy = y1 - mPreviousY1;
                        x = x1;
                        y = y1;
                        processTouch = true;
                    }
                    if (pointerId == 1) {
                        dx = x2 - mPreviousX2;
                        dy = y2 - mPreviousY2;
                        x = x2;
                        y = y2;
                        processTouch = true;
                    }
                    if (processTouch) {
                        if (mFOVIPDenabled) {
                            if (x < getWidth() / 2) {
                                mRenderer.mdFOV += (dx / 10f / SCREEN_DENSITY);
                            } else {
                                mRenderer.mIPD += (dx / 1000f * SCREEN_DENSITY);
                            }
                        } else {
                            if (x < getWidth() / 2) {
                                // pointer in left half
                                mRenderer.mdPosY += (dx / 100f * SCREEN_DENSITY);
                                mRenderer.mdPosX += (dy / 100f * SCREEN_DENSITY);
                            } else {
                                // pointer in right half
                                mRenderer.mdAngleX += (dx / 10f * SCREEN_DENSITY);
                                mRenderer.mdAngleY += (dy / 10f * SCREEN_DENSITY);
                            }
                        }
                    }
                }
        }

        mPreviousX1 = x1;
        mPreviousY1 = y1;
        mPreviousX2 = x2;
        mPreviousY2 = y2;
        return true;
    }
}
