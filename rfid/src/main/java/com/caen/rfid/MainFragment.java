package com.caen.rfid;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.caen.BLEPort.BLEPortEvent;
import com.caen.RFIDLibrary.CAENRFIDBLEConnectionEventListener;
import com.caen.RFIDLibrary.CAENRFIDException;
import com.caen.RFIDLibrary.CAENRFIDPort;
import com.caen.RFIDLibrary.CAENRFIDReader;
import com.caen.RFIDLibrary.CAENRFIDReaderInfo;
import com.caen.rfid.models.DemoReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

public class MainFragment extends Fragment implements CAENRFIDBLEConnectionEventListener {


    protected static final int DO_INVENTORY = 3;
    protected static final int ADD_READER_BLE = 4;
    protected static boolean DESTROYED = false;
    private final ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
    public static boolean returnFromActivity = false;

    public static Vector<DemoReader> Readers;

    public static int Selected_Reader;

    private TextView toolbarTitle;
    private TextView toolbarSubtitle;

    private ProgressDialog tcpBtWaitProgressDialog;

    @Override
    public void onBLEConnectionEvent(final CAENRFIDReader caenrfidReader, final BLEPortEvent blePortEvent) {
        // when we change something on the ui
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DESTROYED)
                    return;
                if (blePortEvent.getEvent().equals(BLEPortEvent.ConnectionEvent.CONNECTION_LOST) ||
                        blePortEvent.getEvent().equals(BLEPortEvent.ConnectionEvent.CONNECTION_CLOSED)
                ) {
                    int pos = 0;
                    Vector<Integer> toRemove = new Vector<Integer>();
                    for (DemoReader demoReader : Readers) {
                        try {
                            if (
                                    demoReader.getConnectionType().equals(CAENRFIDPort.CAENRFID_BLE)
                                            && demoReader.getReader().equals(caenrfidReader)
                            ) {
                                data.remove(pos);
                                demoReader.getReader().Disconnect();
                                toRemove.add(pos);
                            }
                        } catch (CAENRFIDException e) {
                            e.printStackTrace();
                            toRemove.add(pos);
                        }
                        pos++;
                    }
                    for (int i = 0; i < toRemove.size(); i++) {
                        Readers.remove(toRemove.get(i).intValue());
                    }

                    Toast.makeText(getActivity().getApplicationContext(),
                            "BLE device disconnected!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private class BLEConnector extends Thread{

        BluetoothDevice bleDevice = null;
        boolean error1 = false;
        boolean error2 = false;

        BLEConnector ( BluetoothDevice device) {
            bleDevice = device;
            setName("BLEConnector");
        }

        @Override
        public void run() {
            super.run();
            //connect to the reader...
            CAENRFIDReader r = new CAENRFIDReader();
            CAENRFIDReaderInfo info = null;
            String fwrel = null;
            try {
                r.addCAENRFIDBLEConnectionEventListener(MainFragment.this);
                r.Connect(getActivity().getApplicationContext(),bleDevice);
            } catch (CAENRFIDException e) {
                error1 = true;
            }
            if (!error1) {
                try {
                    error2 = false;
                    info = r.GetReaderInfo();
                    fwrel = r.GetFirmwareRelease();
                } catch (CAENRFIDException e) {
                    error2 = true;
                }
                if (!error2) {
                    DemoReader dr = new DemoReader(r,
                            info.GetModel(), info.GetSerialNumber(), fwrel,
                            CAENRFIDPort.CAENRFID_BLE);
                    Readers.add(dr);
                    Intent do_inventory = new Intent(getActivity().getApplicationContext(),
                            InventoryActivity.class);
                    startActivityForResult(do_inventory, DO_INVENTORY);

                }
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(true);
                }
            });
        }


        protected void onPostExecute(Boolean result) {
            if (error1) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Error during connection...", Toast.LENGTH_SHORT)
                        .show();
            } else if (error2) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Error retrieving info from reader...",
                        Toast.LENGTH_SHORT).show();
            } else {
                //	updateReadersList();
            }
            tcpBtWaitProgressDialog.dismiss();
        }
    }



    public void addNewReaderActivity(View v) {
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Toast.makeText(getActivity().getApplicationContext(), "No Bluetooth adapter found!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent addReader = new Intent(getActivity().getApplicationContext(),
                BLESelection.class);
        getActivity().startActivityForResult(addReader,
                ADD_READER_BLE);
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }






    public MainFragment() {
        // Required empty public constructor
    }

/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        toolbarTitle =(TextView) v.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Choose readers");
        toolbarSubtitle = (TextView) v.findViewById(R.id.toolbar_subtitle);
        toolbarSubtitle.setText("Available readers");
        if (!mainActivity.returnFromActivity) {
            Readers = new Vector<>();
        } else
            mainActivity.returnFromActivity = false;



        return v;
    }*/

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            int selected_port = data.getIntExtra("BLE_SELECTION_RESULT", BLESelection.BLE_CANCELED);
            if(selected_port == BLESelection.BLE_SELECTED){
                final BluetoothDevice device = (BluetoothDevice) Objects.requireNonNull(data.getExtras()).get("BLE_DEVICE");
                assert device != null;
                tcpBtWaitProgressDialog = ProgressDialog.show(this,
                        "Connection ","Connecting to "+ device.getName());
                new mainActivity.BLEConnector(device).start();
            }
        }

    }*/
}