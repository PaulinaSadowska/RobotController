package com.paulina.sadowska.robotwirelesscontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by palka on 25.11.15.
 */
public class ControllerFragment extends Fragment implements View.OnTouchListener {

    //used to determine inner circle dimensions
    private int INNER_CIRCLE_DIAMETER = 180;
    private TextView connectionState;

    private View mInnerCircleView;
    private View mOuterCircleMarginView;

    private ViewGroup mRootLayout;
    private int _xDelta;
    private int _yDelta;



    private final Handler mHandler = new Handler();

    private int _xCenter;
    private int _yCenter;
    private boolean centered = false;
    private double _Radius;

    private final Runnable centerInnerCircle = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            RelativeLayout.LayoutParams layParams = (RelativeLayout.LayoutParams) mInnerCircleView.getLayoutParams();
            layParams = centerInnerCircle(layParams);
            mInnerCircleView.setLayoutParams(layParams);
        }
    };

    public void setConnectionState(boolean isConnected)
    {
        if(isConnected)
            connectionState.setVisibility(View.GONE);
        else
            connectionState.setVisibility(View.VISIBLE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.controller_fragment, container, false);

        mRootLayout = (ViewGroup) rootView.findViewById(R.id.root_relative_layout);
        mInnerCircleView = mRootLayout.findViewById(R.id.inner_circle_view);
        mOuterCircleMarginView = mRootLayout.findViewById(R.id.outer_circle_view_margin);
        connectionState = (TextView) mRootLayout.findViewById(R.id.connection_state_text_view);

        ViewTreeObserver vto = mOuterCircleMarginView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                INNER_CIRCLE_DIAMETER = mInnerCircleView.getWidth();
                _xCenter = mOuterCircleMarginView.getLeft() + mOuterCircleMarginView.getWidth() / 2 - INNER_CIRCLE_DIAMETER / 2;
                _yCenter = mOuterCircleMarginView.getTop() + mOuterCircleMarginView.getHeight() / 2 - INNER_CIRCLE_DIAMETER / 2;
                _Radius = mOuterCircleMarginView.getWidth() / 2;

                if (!centered) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(INNER_CIRCLE_DIAMETER, INNER_CIRCLE_DIAMETER);
                    layoutParams = centerInnerCircle(layoutParams);
                    mInnerCircleView.setLayoutParams(layoutParams);
                    centered = true;
                }
            }
        });

        mInnerCircleView.setOnTouchListener(this);
        return rootView;
    }


    public void fragmentDimensionsChanged() {
        //center after 10ms
       mHandler.postDelayed(centerInnerCircle, 20);
    }

    private RelativeLayout.LayoutParams centerInnerCircle(RelativeLayout.LayoutParams layoutParams)
    {
        layoutParams.leftMargin = _xCenter;
        layoutParams.topMargin = _yCenter;
        layoutParams.rightMargin = -250;
        layoutParams.bottomMargin = -250;
        return layoutParams;
    }

    private int[] getMargins(int diffX, int diffY)
    {

        double x, y, r, alpha, rx, ry;
        int marginX, marginY;
        x = _xCenter - diffX;
        y = _yCenter - diffY;

        alpha = Math.atan(x/y);

        if(y<0)
            alpha += 3.14; //2 i 3 cwiartka
        if(y>0 && x<0)
            alpha +=3.14 * 2; // 4 cwiartka

        //value used outside to control robot
        double alphaClockwise =  3.14 * 2 -  alpha;
        Utilities.setAlpha((int)(alphaClockwise * 180/3.14));


        rx = _Radius * Math.sin(alpha);
        ry = _Radius * Math.cos(alpha);

        marginX = diffX;
        marginY = diffY;

        if(rx < (x) && x>0 || rx > (x) && x<0 )
            marginX = _xCenter - (int)rx;

        if(ry < (y) && y>0 || ry > (y) && y<0)
            marginY = _yCenter - (int)ry;

        //calculate velocity multiplier
        r = Math.pow(x, 2) + Math.pow(y, 2);
        r = Math.sqrt(r);
        if(r>_Radius)
            r = _Radius;
        Utilities.setVelocityMultiplier(r / _Radius);


        int[] margin = {marginX, marginY};
        return margin;
    }

    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                Utilities.setRobotIsMovingFlag(); //start moving
                break;
            case MotionEvent.ACTION_UP:
                RelativeLayout.LayoutParams layParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layParams = centerInnerCircle(layParams);
                Utilities.resetRobotIsMovingFlag(); //stop moving
                Utilities.setVelocityMultiplier(0.0);
                view.setLayoutParams(layParams);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                        .getLayoutParams();
                int margins[] = getMargins(X - _xDelta, Y - _yDelta);
                layoutParams.leftMargin = margins[0];
                layoutParams.topMargin = margins[1];
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                view.setLayoutParams(layoutParams);
                break;
        }
        mRootLayout.invalidate();
        return true;
    }
}
