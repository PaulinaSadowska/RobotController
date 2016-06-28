package com.paulina.sadowska.robotwirelesscontroller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by palka on 17.12.15.
 */
public class ReceivedDataFragment extends Fragment {


    private TextView mCurrent1;
    private TextView mCurrent2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.received_data_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrent1 = (TextView) view.findViewById(R.id.current_1_text);
        mCurrent2 = (TextView) view.findViewById(R.id.current_2_text);

        if(getActivity().getLocalClassName().equals("Wifi.WifiActivity"))
        {
            mCurrent1.setTextColor(getActivity().getResources().getColor(R.color.black));
            mCurrent2.setTextColor(getActivity().getResources().getColor(R.color.black));
        }
        
    }


    public void bindData()
    {
        mCurrent1.setText(Html.fromHtml("I<sub><small>L</sub></small> = " + (int)Utilities.getCurrentmV(2) + " mA"));
        mCurrent2.setText(Html.fromHtml("I<sub><small>R</sub></small> = " + (int)Utilities.getCurrentmV(1) + " mA"));
    }

}



