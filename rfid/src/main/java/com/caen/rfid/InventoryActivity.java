package com.caen.rfid;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.caen.BLEPort.BLEPortEvent;
import com.caen.RFIDLibrary.CAENRFIDBLEConnectionEventListener;
import com.caen.RFIDLibrary.CAENRFIDEvent;
import com.caen.RFIDLibrary.CAENRFIDEventListener;
import com.caen.RFIDLibrary.CAENRFIDException;
import com.caen.RFIDLibrary.CAENRFIDLogicalSource;
import com.caen.RFIDLibrary.CAENRFIDLogicalSourceConstants;
import com.caen.RFIDLibrary.CAENRFIDNotify;
import com.caen.RFIDLibrary.CAENRFIDReader;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryActivity extends AppCompatActivity {

/*    public static final short MAX_TAGS_PER_LIST = 700;
    public static Hashtable<String, Integer> mTagListPosition;
    public static int mSelectedTag;
    protected DemoReader mReader;
    protected RFIDTagAdapter mRFIDTagAdapter;
    protected Button mButtonInventory;
    protected ListView mInventoryList;
    protected TextView mTotalFound;
    protected int reader_position = 0;

    protected static AtomicInteger mCurrentFoundNum;
    protected static long mCurrentFoundTime;
    InventoryTask _inventory_task = null;*/

/*
    @Override
    public void onBLEConnectionEvent(CAENRFIDReader caenrfidReader, BLEPortEvent blePortEvent) {
        if (blePortEvent.getEvent().equals(BLEPortEvent.ConnectionEvent.CONNECTION_LOST))
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
    }


    class InventoryTask extends AsyncTask<Object, Object, Void>
            implements CAENRFIDEventListener {

        private boolean _running = false;

        boolean running() {
            return _running;
        }

        void setRunning(boolean is_running) {
            _running = is_running;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... args) {
            CAENRFIDReader reader = ((DemoReader) args[0]).getReader();
            CAENRFIDLogicalSource TheSource = null;
            try {
                TheSource = reader.GetSource("Source_0");
            } catch (CAENRFIDException e1) {
                e1.printStackTrace();
            }
            try {
                assert TheSource != null;
                TheSource.SetReadCycle(0);
                reader.addCAENRFIDEventListener(this);
                    TheSource
                            .SetSelected_EPC_C1G2(CAENRFIDLogicalSourceConstants.EPC_C1G2_All_SELECTED);
                    TheSource
                            .EventInventoryTag(
                                    new byte[0],
                                    (short) 0x0,
                                    (short) 0x0,
                                    (short) 0x06
                                   );


            } catch (CAENRFIDException e) {
                e.printStackTrace();
            }
            //update main layout, inventory has been started...
            this.publishProgress(new Object[]{null});

            try {
                int[] tmp_i = new int[]{0, 0, 0, 0, 0};
                int tmp_cur = 0;
                mCurrentFoundTime = System.currentTimeMillis();
                _running = true;
                while (true) {
                    Thread.sleep(10);
                    if ((System.currentTimeMillis() - mCurrentFoundTime) > 1000) {
                        tmp_cur = (tmp_cur - tmp_i[4]);
                        for (int i = 1; i < 5; i++) {
                            tmp_i[5 - i] = tmp_i[5 - i - 1];
                        }
                        tmp_i[0] = mCurrentFoundNum.getAndSet(0);
                        tmp_cur += tmp_i[0];
                        mCurrentFoundTime = System.currentTimeMillis();
                    }
                    if (!running()) {
                        try {
                            reader.InventoryAbort();
                        } catch (CAENRFIDException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reader.removeCAENRFIDEventListener(this);
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... elements) {
            if (elements[0] == null) {
                //Inventory has been started;
                mButtonInventory.setText(R.string.inventory_button_stop);
                mButtonInventory.setEnabled(true);
                return;
            }
            CAENRFIDNotify tag = (CAENRFIDNotify) elements[0];


            mRFIDTagAdapter.addTag(
                    new RFIDTag(tag, RFIDTag
                            .toHexString(tag.getTagID()), tag.getRSSI()));

            mTotalFound.setText(Integer.toString(mRFIDTagAdapter.getCount()));
        }

        @Override
        protected void onPostExecute(Void unused) {
            Toast.makeText(InventoryActivity.this, "Done!", Toast.LENGTH_SHORT)
                    .show();
            mButtonInventory.setText(R.string.inventory_button_start);
            mButtonInventory.setEnabled(true);
        }

        @Override
        public void CAENRFIDTagNotify(CAENRFIDEvent evt) {
            mCurrentFoundNum.incrementAndGet();
            this.publishProgress((Object[]) evt.getData().toArray(new CAENRFIDNotify[0]));

        }
    }
*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_selection);


/*
        reader_position = controllerActivity.Selected_Reader;
        mReader = controllerActivity.Readers.get(controllerActivity.Selected_Reader);
        mRFIDTagAdapter = new RFIDTagAdapter(getApplicationContext(), R.id.inventory_list);
        mButtonInventory = this.findViewById(R.id.inventory_button);
        mInventoryList = this.findViewById(R.id.inventory_list);
        mInventoryList.setAdapter(mRFIDTagAdapter);


        mReader.getReader().addCAENRFIDBLEConnectionEventListener(this);
        startInventory();

        OnItemClickListener listListener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (inventoryRunning()) {
                    Toast.makeText(getApplicationContext(),
                            "Must stop inventory", Toast.LENGTH_SHORT).show();
                    return;
                }
                mSelectedTag = position;

                                Bundle b = new Bundle();
                                String s1 = mRFIDTagAdapter.getItem(
                                        mSelectedTag).getId();
                                b.putString("TAG_HEX", s1);
                                Intent randw = new Intent(
                                        getApplicationContext(),
                                        ReadAndWriteActivity.class);
                                randw.putExtras(b);
                                startActivityForResult(randw, 0);

            }

        };
        mInventoryList.setOnItemClickListener(listListener);
        mTotalFound = this.findViewById(R.id.total_found_num);


        mTagListPosition = new Hashtable<>(MAX_TAGS_PER_LIST);
        mCurrentFoundNum = new AtomicInteger(0);
        mCurrentFoundTime = 0;*/

    }

/*    @Override
    protected void onDestroy() {
        mReader.getReader().removeCAENRFIDBLEConnectionEventListener(this);
        super.onDestroy();
        if (inventoryRunning()) {
            mButtonInventory.setText(R.string.inventory_button_start);
            mButtonInventory.setEnabled(false);
            stopInventory();
        }
        controllerActivity.returnFromActivity = true;
    }



    public void doInventoryAction(View v) {
        mButtonInventory.setEnabled(false);
        if (inventoryRunning()) {
            stopInventory();
        } else {
            _inventory_task = null;
            startInventory();
        }
    }



    public void startInventory() {
        if (_inventory_task == null) {
            _inventory_task = new InventoryTask();
            _inventory_task.execute(this.mReader);
        }
    }

    public void stopInventory() {
        try {
            _inventory_task.setRunning(false);
        } catch (Exception exc) {
            exc.printStackTrace(); // [cg]NAJ> not a good solution, these exceptions shoul propagate or register a log entry
        }
    }



    private boolean inventoryRunning() {
        return _inventory_task != null && _inventory_task.running();
    }


    @Override
    public void onBLEConnectionEvent(CAENRFIDReader caenrfidReader, BLEPortEvent blePortEvent) {

    }*/
}