package com.paulina.sadowska.robotwirelesscontroller;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by palka on 17.12.15.
 */
public class VelocityFragment extends Fragment {

    private SeekBar mVelocityBar;
    private TextView mCurrent1;
    private TextView mCurrent2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.velocity_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVelocityBar = (SeekBar) view.findViewById(R.id.velocity_seek_bar);
        mCurrent1 = (TextView) view.findViewById(R.id.current_1_text);
        mCurrent2 = (TextView) view.findViewById(R.id.current_2_text);

        mVelocityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            /**
             * gets velocity seekBar progress and transform it to String in 3-digit format (e.g. 021)
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int velocity = i; //i <- progress
                Utilities.setVelocity(velocity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)  { }
        });
    }
}
