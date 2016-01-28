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

public class WeatherActivity extends Activity {

    TextView txtView_temperature;
    TextView txtView_humidity;
    TextView txtView_pressure;
    private HexiwearService hexiwearService;

    private final ArrayList<String> uuidArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_screen);

        uuidArray.add(HexiwearService.UUID_CHAR_TEMPERATURE);
        uuidArray.add(HexiwearService.UUID_CHAR_HUMIDITY);
        uuidArray.add(HexiwearService.UUID_CHAR_PRESSURE);

        txtView_temperature = (TextView) findViewById(R.id.textView_tempValue);
        txtView_humidity = (TextView) findViewById(R.id.textView_humValue);
        txtView_pressure = (TextView) findViewById(R.id.textView_pressValue);
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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override

    protected void onDestroy() {

        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void displayData(TextView txtView, String data) {
        if (data != null) {
            txtView.setText(data);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void displayCharData(String uuid, byte[] data) {
        String tmpString;
        int tmpLong;
        float tmpFloat;
        String portState;

        if (uuid.equals(HexiwearService.UUID_CHAR_TEMPERATURE)) {
            tmpLong = (((int)data[1]) << 8) | (data[0] & 0xff);
            tmpFloat = (float)tmpLong / 100;
            tmpString = tmpFloat + (" \u2103");
            portState = String.valueOf(tmpFloat);
            displayData(txtView_temperature, tmpString);

            // Send port event to Yodiwo Cloud
            NodeService.SendPortMsg(this, ThingManager.HexiwearWeather, ThingManager.HexiwearWeatherPortTemperature, portState, 0);
        }

        else if (uuid.equals(HexiwearService.UUID_CHAR_HUMIDITY)) {
            tmpLong = (data[1] << 8) & 0xff00 | (data[0] & 0xff);
            tmpFloat = (float)tmpLong / 100;
            tmpString = tmpFloat + (" %");
            portState = String.valueOf(tmpFloat);
            displayData(txtView_humidity, tmpString);

            // Send port event to Yodiwo Cloud
            NodeService.SendPortMsg(this, ThingManager.HexiwearWeather, ThingManager.HexiwearWeatherPortHumidity, portState, 0);
        }

        else if (uuid.equals(HexiwearService.UUID_CHAR_PRESSURE)) {
            tmpLong = (data[1] << 8) & 0xff00 | (data[0] & 0xff);
            tmpFloat = (float)tmpLong / 100;
            tmpString = tmpFloat + (" kPa");
            portState = String.valueOf(tmpFloat);
            displayData(txtView_pressure, tmpString);

            // Send port event to Yodiwo Cloud
            NodeService.SendPortMsg(this, ThingManager.HexiwearWeather, ThingManager.HexiwearWeatherPortPressure, portState, 0);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void displayCharDataFromCloud(int portID, String state) {
        if (portID == ThingManager.HexiwearWeatherPortTemperature) {
            String tmpString = state + (" \u2103");
            displayData(txtView_temperature, tmpString);
        }
        else if (portID == ThingManager.HexiwearWeatherPortHumidity) {
            String tmpString = state + (" %");
            displayData(txtView_humidity, tmpString);
        }
        else if (portID == ThingManager.HexiwearWeatherPortPressure) {
            String tmpString = state + (" kPa");
            displayData(txtView_pressure, tmpString);
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
//                finish();
                Intent intentAct = new Intent(WeatherActivity.this, DeviceScanActivity.class);
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

                if (thingKey.equals(ThingManager.getInstance(context).GetThingKey(ThingManager.HexiwearWeather))) {
                    displayCharDataFromCloud(portID, portState);
                }
            }
        }
    };
}
