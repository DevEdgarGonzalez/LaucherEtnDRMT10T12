package com.actia.utilities.list_devices_bt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.IBluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConstantsApp;
import com.actia.mexico.launcher_t12_generico_2018.R;


import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;


public class ManagerBluetoothLauncher {
    private static final String TAG_FGMNT_LISTDEVICES_DLG = "dialog_list_devices";
    private static final String TAG = "ManageBtLauncherLog";

    Activity activity;
    BluetoothAdapter bluetoothAdapter;
    FragmentManager fragmentManager;
    ImageView imgvBluetoothMain;
    public static IBluetoothHeadset iBtHeadset;
    static Context context;
    Handler handlerStartBtAdapter = new Handler();

    public interface OnBluetoothListenr {
        void onBluetoothConected(String nameDeviceConnected);
    }

    public ManagerBluetoothLauncher(Activity activity, FragmentManager fragmentManager, ImageView imgvBluetoothMain) {
        this.activity = activity;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.fragmentManager = fragmentManager;
        this.imgvBluetoothMain = imgvBluetoothMain;


        if (MainActivity.iBtHeadset != null) {
            if (isBluetoothHeadsetConnected()) {
                drawDeviceConnected();

            }
        }
        activity.registerReceiver(receiverConnectDevice, filterConnectDevice);
    }


    public void onClickBloetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(activity, activity.getString(R.string.bt_device_doesnt_have_bluetooth), Toast.LENGTH_SHORT).show();
            return;
        }
        if (MainActivity.iBtHeadset == null) {
            Toast.makeText(activity, activity.getString(R.string.bt_headset_not_working) + "", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                showDialogTurnOnBluetooth();
            } else {
                showDialogListDevices();
            }
        }


    }

    private void showDialogListDevices() {
        if (isBluetoothHeadsetConnected()) {
            showDialogUnpairDevices();

        } else {
            unpairDevices();
            ListDevicesDialogFragment dialogFragment = ListDevicesDialogFragment.newInstance();
            dialogFragment.show(fragmentManager, TAG_FGMNT_LISTDEVICES_DLG);
        }
    }

    public boolean isBluetoothHeadsetConnected() {
        return bluetoothAdapter != null &&
                bluetoothAdapter.isEnabled() &&
                bluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }


    private void showDialogTurnOnBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, ConstantsApp.REQUEST_ENABLE_BT);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantsApp.REQUEST_ENABLE_BT) {
            switch (resultCode) {
                case BluetoothAdapter.STATE_CONNECTING:
                case BluetoothAdapter.STATE_CONNECTED:
                case Activity.RESULT_OK:
                    showDialogListDevices();
                    break;

                case BluetoothAdapter.STATE_DISCONNECTED:
                case BluetoothAdapter.STATE_DISCONNECTING:
                    Toast.makeText(activity, activity.getResources().getString(R.string.bt_bluetooth_denied), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onActivityResult: " + activity.getResources().getString(R.string.bt_bluetooth_denied));
                    break;

                default:
                    Log.d(TAG, "default");
                    break;
            }
        }
    }

    private void unpairDevices() {
        try {
            List<BluetoothDevice> connectDevices = MainActivity.iBtHeadset.getConnectedDevices();
            if (connectDevices!=null && connectDevices.size() > 0) {
                for (BluetoothDevice device : connectDevices) {
                    try {
                        MainActivity.iBtHeadset.disconnect(device);
                        Method m = device.getClass()
                                .getMethod("removeBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);
                    } catch (Exception e) {
                        Log.d(TAG, "unpairDevices: Removing has been failed. " + e.getMessage());
                    }
                }
            }
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            if (bondedDevices!=null && bondedDevices.size() > 0) {
                for (BluetoothDevice device : bondedDevices) {
                    try {
                        MainActivity.iBtHeadset.disconnect(device);
                        Method m = device.getClass()
                                .getMethod("removeBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);
                    } catch (Exception e) {
                        Log.d(TAG, "unpairDevices: Removing has been failed. " + e.getMessage());
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }


    public String getNameDeviceConnected() {
        try {
            List<BluetoothDevice> connectedDevices = MainActivity.iBtHeadset.getConnectedDevices();
            for (BluetoothDevice device : connectedDevices) {
                int bondState = device.getBondState();
                if (bondState != BluetoothDevice.BOND_NONE) {

                    return device.getName();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    private void showDialogUnpairDevices() {
        if (MainActivity.iBtHeadset == null) return;
//        String nameDeviceConected="\""+device.getName()+"\"";
        String nameDeviceConected = getNameDeviceConnected();

        String messageDialog = "ya hay un dispositivo emparejado, ¿desea desvincularlo?";


        if (nameDeviceConected != null && !nameDeviceConected.isEmpty()) {
            messageDialog = "\"" + nameDeviceConected + "\"" + " esta conectado actualmente, ¿desea desvincularlo?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("Alerta")
                .setMessage(messageDialog)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unpairDevices();
                        drawDontDeviceConnected();
                        ListDevicesDialogFragment dialogFragment = ListDevicesDialogFragment.newInstance();
                        dialogFragment.show(fragmentManager, TAG_FGMNT_LISTDEVICES_DLG);
                        unpairDevices();

                    }
                });
        builder.setCancelable(true);
        builder.show();

    }

    private void drawDontDeviceConnected() {
        imgvBluetoothMain.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_bluetooth_state_on_black));
    }

    private void drawDeviceConnected() {
        new Handler(). postDelayed(new Runnable() {
            @Override
            public void run() {
                imgvBluetoothMain.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_bluetooth_state_connected));
            }
        },3000);

    }


    private final BroadcastReceiver receiverConnectDevice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drawDeviceConnected();
        }
    };

    private final IntentFilter filterConnectDevice = new IntentFilter("ConnectDevice");


}
