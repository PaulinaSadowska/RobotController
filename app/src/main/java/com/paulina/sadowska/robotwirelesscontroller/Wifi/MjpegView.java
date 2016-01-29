package com.paulina.sadowska.robotwirelesscontroller.Wifi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class MjpegView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "MJPEG";

    public final static int POSITION_LOWER_RIGHT = 6;

    SurfaceHolder holder;

    private MjpegViewThread thread;
    private MjpegInputStream mIn = null;
    private boolean mRun = false;
    private boolean surfaceDone = false;

    private int ovlPos;
    private int dispWidth;
    private int dispHeight;

    private boolean suspending = false;

    private Bitmap bmp = null;

    // image size

    public int IMG_WIDTH = 640;
    public int IMG_HEIGHT = 480;

    public class MjpegViewThread extends Thread {
        private SurfaceHolder mSurfaceHolder;


        public MjpegViewThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        private Rect destRect() {
                int translY = 0;
                return new Rect(0, translY, dispWidth, dispHeight-translY);
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
                dispWidth = width;
                dispHeight = height;
            }
        }

        public void run() {
            Paint p = new Paint();

            while (mRun) {

                Rect destRect;
                Canvas c = null;

                if (surfaceDone) {
                    try {
                        if (bmp == null) {
                            bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
                        }

                        int ret = mIn.readMjpegFrame(bmp);

                        if (ret == -1) {
                            //error
                            return;
                        }

                        destRect = destRect();
                        c = mSurfaceHolder.lockCanvas();
                        synchronized (mSurfaceHolder) {

                            if(c!=null)
                                c.drawBitmap(bmp, null, destRect, p);
                        }

                    } catch (IOException e) {

                    } finally {
                        if (c != null) mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

    private void init() {

        //SurfaceHolder holder = getHolder();
        holder = getHolder();
        holder.addCallback(this);
        thread = new MjpegViewThread(holder);
        setFocusable(true);
        ovlPos = MjpegView.POSITION_LOWER_RIGHT;
        dispWidth = getWidth();
        dispHeight = getHeight();
    }

    public void startPlayback() {
        if (mIn != null) {
            mRun = true;
            if (thread == null) {
                thread = new MjpegViewThread(holder);
            }
            thread.start();
        }
    }

    public void resumePlayback() {
        if (suspending) {
            if (mIn != null) {
                mRun = true;
                SurfaceHolder holder = getHolder();
                holder.addCallback(this);
                thread = new MjpegViewThread(holder);
                thread.start();
                suspending = false;
            }
        }
    }

    public void stopPlayback() {
        if (mRun) {
            suspending = true;
        }
        mRun = false;
        if (thread != null) {
            boolean retry = true;
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
            thread = null;
        }
        if (mIn != null) {
            try {
                mIn.close();
            } catch (IOException e) {
            }
            mIn = null;
        }

    }

    public void freeCameraMemory() {
        if (mIn != null) {
            mIn.freeCameraMemory();
        }
    }

    public MjpegView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
        if (thread != null) {
            thread.setSurfaceSize(w, h);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceDone = false;
        stopPlayback();
    }

    public MjpegView(Context context) {
        super(context);
        init();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        surfaceDone = true;
    }

    public void setSource(MjpegInputStream source) {
        mIn = source;
        if (!suspending) {
            startPlayback();
        } else {
            resumePlayback();
        }
    }

    public void setResolution(int w, int h) {
        IMG_WIDTH = w;
        IMG_HEIGHT = h;
    }

    public boolean isStreaming() {
        return mRun;
    }
}
