package com.mikroe.hexiwear_android;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.yodiwo.androidnode.NodeService;
import com.yodiwo.androidnode.ThingManager;

import java.util.ArrayList;

public class AccelActivity extends Activity {

    private CustomProgress_Vertical progressBarX;
    private CustomProgress_Vertical progressBarY;
    private CustomProgress_Vertical progressBarZ;

    private HexiwearService hexiwearService;

    private final ArrayList<String> uuidArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accel_screen);

        uuidArray.add(HexiwearService.UUID_CHAR_ACCEL);

        progressBarX = (CustomProgress_Vertical) findViewById(R.id.accelProgressX);
        progressBarY = (CustomProgress_Vertical) findViewById(R.id.accelProgressY);
        progressBarZ = (CustomProgress_Vertical) findViewById(R.id.accelProgressZ);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override

    protected void onResume() {
        super.onResume();

        BluetoothManager btm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if( btm.getAdapter().isEnabled()) {
            hexiwearService = new HexiwearService(uuidArray);
            hexiwearService.readCharStart(10);
        }
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override

    protected void onPause() {
        super.onPause();
        if (hexiwearService != null) {
            hexiwearService.readCharStop();
        }
        unregisterReceiver(mGattUpdateReceiver);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////1//////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override

    protected void onDestroy() {

        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void displayCharData(String uuid, byte[] data) {
        int tmpLong;
        float tmpFloat;
        String[] portStates = new String[3];

        if (uuid.equals(HexiwearService.UUID_CHAR_ACCEL)) {
            tmpLong = (((int)data[1]) << 8) | (data[0] & 0xff);
            tmpFloat = (float)tmpLong / 100;
            portStates[0] = String.valueOf(tmpFloat);
            progressBarX.setProgressTitle(portStates[0] + "g");
            tmpLong += (progressBarX.getProgressMax() >> 1);
            if(tmpLong > progressBarX.getProgressMax()) {
                tmpLong = progressBarX.getProgressMax();
            }
            progressBarX.setProgressValue(tmpLong);

            tmpLong = (((int)data[3]) << 8) | (data[2] & 0xff);
            tmpFloat = (float)tmpLong / 100;
            portStates[1] = String.valueOf(tmpFloat);
            progressBarY.setProgressTitle(portStates[1] + "g");

            tmpLong += (progressBarY.getProgressMax() >> 1);
            if(tmpLong > progressBarY.getProgressMax()) {
                tmpLong = progressBarY.getProgressMax();
            }
            progressBarY.setProgressValue(tmpLong);

            tmpLong = (((int)data[5]) << 8) | (data[4] & 0xff);
            tmpFloat = (float)tmpLong / 100;
            portStates[2] = String.valueOf(tmpFloat);
            progressBarZ.setProgressTitle(portStates[2] + "g");
            tmpLong += (progressBarZ.getProgressMax() >> 1);
            if(tmpLong > progressBarZ.getProgressMax()) {
                tmpLong = progressBarZ.getProgressMax();
            }
            progressBarZ.setProgressValue(tmpLong);

            // Send batch port event to Yodiwo Cloud
            NodeService.SendPortMsg(this, ThingManager.HexiwearAccel, portStates, 0);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void displayCharDataFromCloud(int portID, String state) {
        if (portID == ThingManager.HexiwearAccelPortX) {
            int tmpLong = (int)(Float.valueOf(state) * 100);

            progressBarX.setProgressTitle(state + "g");
            tmpLong += (progressBarX.getProgressMax() >> 1);
            if(tmpLong > progressBarX.getProgressMax()) {
                tmpLong = progressBarX.getProgressMax();
            }
            progressBarX.setProgressValue(tmpLong);
        }
        else if (portID == ThingManager.HexiwearAccelPortY) {
            int tmpLong = (int)(Float.valueOf(state) * 100);

            progressBarY.setProgressTitle(state + "g");
            tmpLong += (progressBarY.getProgressMax() >> 1);
            if(tmpLong > progressBarY.getProgressMax()) {
                tmpLong = progressBarY.getProgressMax();
            }
            progressBarY.setProgressValue(tmpLong);
        }
        else if (portID == ThingManager.HexiwearAccelPortZ) {
            int tmpLong = (int)(Float.valueOf(state) * 100);

            progressBarZ.setProgressTitle(state + "g");
            tmpLong += (progressBarZ.getProgressMax() >> 1);
            if(tmpLong > progressBarZ.getProgressMax()) {
                tmpLong = progressBarZ.getProgressMax();
            }
            progressBarZ.setProgressValue(tmpLong);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(NodeService.BROADCAST_THING_UPDATE);
        return intentFilter;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Handles various events fired by the Service.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                invalidateOptionsMenu();
                Intent intentAct = new Intent(AccelActivity.this, DeviceScanActivity.class);
                startActivity(intentAct);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuid = intent.getStringExtra(BluetoothLeService.EXTRA_CHAR);

                displayCharData(uuid, data);
            } else if (NodeService.BROADCAST_THING_UPDATE.equals(action)) {
                Bundle b = intent.getExtras();
                int portID = b.getInt(NodeService.EXTRA_UPDATED_PORT_ID, -1);
                String thingKey = b.getString(NodeService.EXTRA_UPDATED_THING_KEY);
                String thingName = b.getString(NodeService.EXTRA_UPDATED_THING_NAME);
                String portState = b.getString(NodeService.EXTRA_UPDATED_STATE);
                Boolean isEvent = b.getBoolean(NodeService.EXTRA_UPDATED_IS_EVENT);
                Boolean isInternalEvent = b.getBoolean(NodeService.EXTRA_IS_INTERNAL_EVENT, false);
                int revNum = b.getInt(NodeService.EXTRA_UPDATED_REVNUM, -1);

                if (thingKey.equals(ThingManager.getInstance(context).GetThingKey(ThingManager.HexiwearAccel))) {
                    displayCharDataFromCloud(portID, portState);
                }
            }
        }
    };
}
