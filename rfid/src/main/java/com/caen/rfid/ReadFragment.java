package com.caen.rfid;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caen.RFIDLibrary.CAENRFIDLogicalSource;
import com.caen.RFIDLibrary.CAENRFIDReader;
import com.caen.RFIDLibrary.CAENRFIDTag;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadFragment extends Fragment {

    private int WORD = 2; //2 bytes
    private CAENRFIDReader mReader;
    private CAENRFIDLogicalSource mSource;
    private CAENRFIDTag mTag;
    private String mTagHex;
    private TextView mSelectedTag;
    private TextView temperatures;


    private ProgressDialog mLoadingDialog;


    public ReadFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_read_and_write, container, false);
        mReader = mainActivity.Readers.get(mainActivity.Selected_Reader).getReader();

      //  Bundle b = this.getIntent().getExtras();
    //    mTagHex = b.getString("TAG_HEX");
        mSelectedTag = (TextView)v.findViewById(R.id.read_and_write_tag_selected);



        temperatures =(TextView)v.findViewById(R.id.temperatures_array);



        mSelectedTag.setText(mTagHex);

        return v;
    }
}