package com.caen.rfid;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.caen.RFIDLibrary.CAENRFIDException;
import com.caen.RFIDLibrary.CAENRFIDLogicalSource;
import com.caen.RFIDLibrary.CAENRFIDReader;
import com.caen.RFIDLibrary.CAENRFIDTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.widget.Toolbar;

import static java.lang.String.valueOf;

public class ReadAndWriteActivity extends Activity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView toolbarSubtitle;


    private int WORD = 2; //2 bytes
    private CAENRFIDReader mReader;
    private CAENRFIDLogicalSource mSource;
    private CAENRFIDTag mTag;
    private String mTagHex;
    private TextView mSelectedTag;
    private TextView temperatures;


    private ProgressDialog mLoadingDialog;



    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

/*
        toolbarTitle = this.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Update Temperature");
        toolbarSubtitle = this.findViewById(R.id.toolbar_subtitle);
        toolbarSubtitle.setText("conatainer 1");*/

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.read_and_write_activity);
        mReader = controllerActivity.Readers.get(controllerActivity.Selected_Reader).getReader();
        //Get Tag
        Bundle b = this.getIntent().getExtras();
        mTagHex = b.getString("TAG_HEX");
        mSelectedTag = this.findViewById(R.id.read_and_write_tag_selected);



        temperatures =this.findViewById(R.id.temperatures_array);



        mSelectedTag.setText(mTagHex);




        new AsyncTask<Object, Void, Object>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mLoadingDialog = ProgressDialog.show(ReadAndWriteActivity.this, "", "Loading.Please wait...", true, false);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                byte[] id = (byte[])objects[0];

                ArrayList<Object> values = new ArrayList<>();
                ArrayList<byte[]> tmps = new ArrayList<>();

                try {
                    mSource = mReader.GetSource("Source_0");
                    mTag = new CAENRFIDTag(id, (short) id.length, mSource, "Ant0");

                    short addrStopDateL = Short.parseShort((String)objects[1], 16);//2
                    short addrStopDateH = Short.parseShort((String)objects[2], 16);//
                    short addrSamplesNumber = Short.parseShort((String)objects[3], 16);//2
                    short firstSampleValue = Short.parseShort((String)objects[4], 16);//2
                  
                    boolean data_loss = false;

                        tmps.add( RFIDTag.ReadWithRetry(mTag, (short) 3, (short) ((addrStopDateL * WORD) ), (short) (WORD)));
                        tmps.add( RFIDTag.ReadWithRetry(mTag, (short) 3, (short) ((addrStopDateH * WORD) ), (short) (WORD)));
                        tmps.add( RFIDTag.ReadWithRetry(mTag, (short) 3, (short) ((addrSamplesNumber * WORD) ), (short) (WORD)));

                         int samplesNumberValue =0;
                       if (tmps.get(2) != null) {
                           samplesNumberValue = Short.parseShort(RFIDTag.toHexString(tmps.get(2)), 16);
                       }

                       for(int i =0; i<samplesNumberValue*3;i++){
                            tmps.add( RFIDTag.ReadWithRetry(mTag, (short) 3, (short) (((firstSampleValue + i) * WORD) ), (short) (WORD)));


                        }
                    for (int i = 0; i < tmps.size(); i++) {
                        if (tmps.get(i) == null) {
                            data_loss = true;
                        }
                    }

                    if (data_loss) {
                        values.add(-1);
                        values.add(tmps);
                        return values;
                    }

                } catch (CAENRFIDException e) {
                    values.add(-2);
                    return values;
                }
                values.add(0);
                values.add(tmps);
                return values;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                mLoadingDialog.dismiss();
                Integer result = (Integer) ((ArrayList<Object>) o).get(0);
                switch(result){
                    case -1:
                        Toast.makeText(ReadAndWriteActivity.this, "Cannot read some data,", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ReadAndWriteActivity.this, "Put the reader near the tag", Toast.LENGTH_SHORT).show();
                        break;
                    case -2:
                        Toast.makeText(ReadAndWriteActivity.this, "Some error occurred during read", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                boolean thereAreNulls =false;
                ArrayList<byte[]> tmp = (ArrayList<byte[]>) ((ArrayList<Object>)o).get(1);
                ArrayList<String> registersWeNeed = new ArrayList<>();
                for (int i = 0; i < tmp.size(); i++) {
                    if(tmp.get(i) == null){
                        // if some register data is missing
                        registersWeNeed.add("???");
                        thereAreNulls = true;
                    } else {

                      //  mValue[i].setText(RFIDTag.toHexString(tmp.get(i)));
                        registersWeNeed.add(RFIDTag.toHexString(tmp.get(i)));

                    }
                }
                Map<String, Object> map = new HashMap<String, Object>();
                if(!thereAreNulls){
                    for(int i =3; i<registersWeNeed.size()-2; i+=3){
                        map.put(
                                Conversion.hexToTime(registersWeNeed.get(i+2) + registersWeNeed.get(i+1)),
                                Conversion.ConvertHexToTemperature(registersWeNeed.get(i)));

                    }
                    temperatures.setText(map.toString());




                }

            }
        }.execute(RFIDTag.hexStringToByteArray(mTagHex),"5C","6D","67","8A");

        // register 67 gives the number of samples registered in the tag

        //register 6C and 6D gives the logging stop date when the user stoped the process of taking temperature measurement

        // from 8A you start the first sample value 8B and 8c values gives the date 8D the second sample etc...


    }



}
