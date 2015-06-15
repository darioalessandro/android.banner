package dario.banner.view;

/*
 * Copyright (C) 2015 Dario A Lencina Talarico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.concurrent.TimeUnit;

import dario.banner.R;
import dario.banner.util.App;

public class BannerRenderer extends SurfaceView implements SurfaceHolder.Callback {
    public int frameCounter=0;
    public void updateSimulatedValues(){
        bannerView.setOffset(frameCounter);
    }


    /**
     Constructor
     */

    public BannerRenderer(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        bannerView = (BannerView)inflater.inflate(R.layout.rpm_speed_gear, null);
        bannerView.setWillNotDraw(false);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        doLayout();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        updateRendering();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
        updateRendering();
    }

    public void renderingPaused(SurfaceHolder holder, boolean paused) {
        mPaused = paused;
        updateRendering();
    }

    public synchronized void updateRendering() {
        boolean shouldRender = (mHolder != null) && !mPaused;
        boolean rendering = mRenderThread != null;

        if (shouldRender != rendering) {
            if (shouldRender) {
                mRenderThread = new RenderThread();
                mRenderThread.start();
            } else {
                mRenderThread.quit();
                mRenderThread = null;
            }
        }
    }

    private void doLayout() {
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(mSurfaceWidth,
                View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(mSurfaceHeight,
                View.MeasureSpec.EXACTLY);

        bannerView.measure(measuredWidth, measuredHeight);
        bannerView.layout(0, 0, bannerView.getMeasuredWidth(), bannerView.getMeasuredHeight());
    }

    private synchronized void repaint() {
        Canvas canvas = null;

        try {
            canvas = mHolder.lockCanvas();
        } catch (RuntimeException e) {
            Log.d(TAG, "lockCanvas failed", e);
        }

        if (canvas != null) {
            bannerView.draw(canvas);
            try {
                mHolder.unlockCanvasAndPost(canvas);
            } catch (RuntimeException e) {
                Log.d(TAG, "unlockCanvasAndPost failed", e);
            }
        }
    }

    private class RenderThread extends Thread {
        private boolean mShouldRun;

        public RenderThread() {
            mShouldRun = true;
        }

        private synchronized boolean shouldRun() {
            return mShouldRun;
        }

        public void end(){
            mShouldRun=false;
        }
        public synchronized void quit() {
            mShouldRun = false;
        }

        boolean isAdding = true;

        int max = 120;
        
        Vibrator v = (Vibrator) App.getAppContext().getSystemService(Context.VIBRATOR_SERVICE);


        @Override
        public void run() {
            while (shouldRun()) {
                long frameStart = SystemClock.elapsedRealtime();
                repaint();
                long frameLength = SystemClock.elapsedRealtime() - frameStart;
                if (frameCounter == max && isAdding) {
                    frameCounter--;
                    isAdding = false;
                    v.vibrate(30);
                }else if(frameCounter < max && isAdding)
                    frameCounter++;
                else if(frameCounter < 0 && !isAdding) {
                    frameCounter ++;
                    isAdding = true;
                    v.vibrate(30);
                } else if(frameCounter <max && !isAdding) {
                    frameCounter --;
                }


                updateSimulatedValues();
                long sleepTime = FRAME_TIME_MILLIS - frameLength;
                if (sleepTime > 0) {
                    SystemClock.sleep(sleepTime);
                }
            }
        }
    }

    private static final String TAG = BannerRenderer.class.getSimpleName();
    private static final int REFRESH_RATE_FPS = 60;
    private static final long FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS;
    private SurfaceHolder mHolder;
    private RenderThread mRenderThread;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    public boolean mPaused;
    public BannerView bannerView =null;

}