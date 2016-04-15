package com.mikroe.hexiwear_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.yodiwo.androidagent.core.NodeService;
import com.yodiwo.androidagent.core.PairingActivity;
import com.yodiwo.androidagent.core.SettingsActivity;
import com.yodiwo.androidagent.core.SettingsProvider;
import com.yodiwo.androidagent.plegma.ConfigParameter;
import com.yodiwo.androidagent.plegma.Port;
import com.yodiwo.androidagent.plegma.PortKey;
import com.yodiwo.androidagent.plegma.Thing;
import com.yodiwo.androidagent.plegma.ThingKey;
import com.yodiwo.androidagent.plegma.ThingUIHints;
import com.yodiwo.androidagent.plegma.ePortConf;
import com.yodiwo.androidagent.plegma.ePortType;
import com.yodiwo.androidagent.plegma.ioPortDirection;

import java.util.ArrayList;

public class MainScreenActivity extends ActionBarActivity {

    public static boolean isUnpairedByUser;
    private static boolean ActivityInitialized;
    private SettingsProvider settingsProvider = null;

// =============================================================================================
    // Things names

    public static final String HexiwearGyro = "HexiwearGyro";
    public static final int HexiwearGyroPortX = 0;
    public static final int HexiwearGyroPortY = 1;
    public static final int HexiwearGyroPortZ = 2;

    public static final String HexiwearAccel = "HexiwearAccel";
    public static final int HexiwearAccelPortX = 0;
    public static final int HexiwearAccelPortY = 1;
    public static final int HexiwearAccelPortZ = 2;

    public static final String HexiwearWeather = "HexiwearWeather";
    public static final int HexiwearWeatherPortTemperature = 0;
    public static final int HexiwearWeatherPortHumidity = 1;
    public static final int HexiwearWeatherPortPressure = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityInitialized = false;

        if (settingsProvider == null)
            settingsProvider = SettingsProvider.getInstance(this);

        // Check if device is paired
        if (settingsProvider.getNodeKey() != null && settingsProvider.getNodeSecretKey() != null) {
            ActivityInitialized = true;
            isUnpairedByUser = false;

            NodeService.ImportThings(setThings(this));
            NodeService.Startup(this);
        }

        setContentView(R.layout.main_screen);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override

    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver, makeConnectivityUpdateIntentFilter());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override

    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = null;

        // Select the opened activity
        switch (id) {
            // Start settings activity
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            // Start Pairing activity
            case R.id.action_repairing:
                intent = new Intent(this, PairingActivity.class);
                break;
        }

        if (intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startTempScreen(View view) {
        Intent intentAct = new Intent(MainScreenActivity.this, WeatherActivity.class);
        startActivity(intentAct);
    }

    public void startAccelScreen(View view) {
        Intent intentAct = new Intent(MainScreenActivity.this, AccelActivity.class);
        startActivity(intentAct);
    }

    public void startGyroScreen(View view) {
        Intent intentAct = new Intent(MainScreenActivity.this, GyroscopeActivity.class);
        startActivity(intentAct);
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
                Intent intentAct = new Intent(MainScreenActivity.this, DeviceScanActivity.class);
                startActivity(intentAct);
            }
            else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                //------------------------ IP Connectivity -----------------------------------------
                int networkType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);

                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();

                if (activeNetInfo == null || !activeNetInfo.isConnected()) {
                    /* if null: airplane mode, or mobile data & wifi off, or out of signal */

                    //update global connectivity so that we don't constantly try to connect to cloud
                    NodeService.SetNetworkConnStatus(context, false);

                    return;
                }

                //update global connectivity: we do have the internets; rejoice
                NodeService.SetNetworkConnStatus(context, true);
            }
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static IntentFilter makeConnectivityUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        return intentFilter;
    }

    // =============================================================================================
    // Create Things
    // =============================================================================================

    private ArrayList<Thing> setThings(Context context) {
        ArrayList<Thing> preDefinedThings = new ArrayList<>();
        ArrayList<ConfigParameter> Config;
        ConfigParameter conf;

        if (settingsProvider == null)
            settingsProvider = SettingsProvider.getInstance(context);

        String nodeKey = settingsProvider.getNodeKey();
        String thingKey = "";
        Thing thing = null;

        // ----------------------------------------------
        // Gyro

        thingKey = ThingKey.CreateKey(nodeKey, HexiwearGyro);
        thing = new Thing(thingKey, HexiwearGyro, new ArrayList<ConfigParameter>(), new ArrayList<Port>(), "", "",
                new ThingUIHints("/Content/VirtualGateway/img/icon-gyroscope.png", ""));

        thing.Ports.add(new Port(PortKey.CreateKey(thingKey, Integer.toString(HexiwearGyroPortX)),
                "X-axis (deg/s)", "",
                ioPortDirection.Output, ePortType.Integer, "0", 0, ePortConf.None));
        thing.Ports.add(new Port(PortKey.CreateKey(thingKey, Integer.toString(HexiwearGyroPortY)),
                "Y-axis (deg/s)", "",
                ioPortDirection.Output, ePortType.Integer, "0", 0, ePortConf.None));
        thing.Ports.add(new Port(PortKey.CreateKey(thingKey, Integer.toString(HexiwearGyroPortZ)),
                "Z-axis (deg/s)", "",
                ioPortDirection.Output, ePortType.Integer, "0", 0, ePortConf.None));

        preDefinedThings.add(thing);

        // ----------------------------------------------
        // Accel

        thingKey = ThingKey.CreateKey(nodeKey, HexiwearAccel);
        thing = new Thing(thingKey, HexiwearAccel, new ArrayList<ConfigParameter>(), new ArrayList<Port>(), "", "",
                new ThingUIHints("/Content/VirtualGateway/img/accelerometer.jpg", ""));

        thing.Ports.add(new Port(PortKey.CreateKey(thingKey, Integer.toString(HexiwearAccelPortX)),
                "X-axis (g)", "",
                ioPortDirection.Output, ePortType.Decimal, "0", 0, ePortConf.None));
        thing.Ports.add(new Port(PortKey.CreateKey(thingKey, Integer.toString(HexiwearAccelPortY)),
                "Y-axis (g)", "",
                ioPortDirection.Output, ePortType.Decimal, "0", 0, ePortConf.None));
        thing.Ports.add(new Port(PortKey.CreateKey(thingKey, Integer.toString(HexiwearAccelPortZ)),
                "Z-axis (g)", "",
                ioPortDirection.Output, ePortType.Decimal, "0", 0, ePortConf.None));

        preDefinedThings.add(thing);

        // ----------------------------------------------
        // Weather

        thingKey = ThingKey.CreateKey(nodeKey, HexiwearWeather);
        thing = new Thing(thingKey, HexiwearWeather, new ArrayList<ConfigParameter>(), new ArrayList<Port>(), "", "",
                new ThingUIHints("/Content/Designer/img/BlockImages/icon-openweathermap.png", ""));

        thing.Ports.add(new Port(PortKey.CreateKey(thingKey, Integer.toString(HexiwearWeatherPortTemperature)),
                "Temperature (Celsius)", "",
                ioPortDirection.Output, ePortType.Decimal, "0", 0, ePortConf.None));
        thing.Ports.add(new Port(PortKey.CreateKey(thingKey, Integer.toString(HexiwearWeatherPortHumidity)),
                "Humidity (%)", "",
                ioPortDirection.Output, ePortType.Decimal, "0", 0, ePortConf.None));
        thing.Ports.add(new Port(PortKey.CreateKey(thingKey, Integer.toString(HexiwearWeatherPortPressure)),
                "Pressure (kPa)", "",
                ioPortDirection.Output, ePortType.Decimal, "0", 0, ePortConf.None));

        preDefinedThings.add(thing);

        return preDefinedThings;
    }
}
