package com.actia.utilities.list_devices_bt;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.actia.home_categories.MainActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;



public class ListDevicesDialogFragment extends DialogFragment {


    private static final String TAG = "ListDevicesDialogLog";
    private BluetoothAdapter mBluetoothAdapter;


    private static final String ARG_ALIST_DEVICES = "lis_devices";


    private final ArrayList<BluetoothDevice> alistDevices = new ArrayList<>();
    private ListDevicesAdapter adapter;


    private OnFragmentInteractionListener mListener;

    private RecyclerView rvItemsListDevices;
    private String title;

    public ListDevicesDialogFragment() {
    }

    public static ListDevicesDialogFragment newInstance() {
        ListDevicesDialogFragment fragment = new ListDevicesDialogFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filteFoundDeviceBt = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filteFoundDeviceBt.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filteFoundDeviceBt.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filteFoundDeviceBt.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filteFoundDeviceBt.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        getActivity().registerReceiver(mReceiverBtFound, filteFoundDeviceBt);

//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_list_devices_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        if (mBluetoothAdapter == null) {
            dismiss();
            return;
        }
        rvItemsListDevices = view.findViewById(R.id.rvDevicesDialogBluetooth);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        if (mBluetoothAdapter == null) return;

        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
//                showDialogTurnOnBluetooth();
            } else {
                startFindDevices();
            }
        }

        getDialog().setTitle(getString(R.string.dlg_bluetooth_searching));
        adapter = new ListDevicesAdapter(alistDevices, getActivity(), new ListDevicesAdapter.OnItemDeviceBtListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemSelected(BluetoothDevice deviceSelected) {
                Log.d(TAG, "onItemSelected: " + deviceSelected.getName());
                try {
//                    pairDevice(deviceSelected);
//                    deviceSelected.setPairingConfirmation(true);
                    boolean isConected = MainActivity.iBtHeadset.connect(deviceSelected);

                    if (isConected){
                        Intent intentManager = new Intent();
                        intentManager.setAction("ConnectDevice");
                        Bundle extras = new Bundle();
                        extras.putString("nameDevice", deviceSelected.getName());
                        intentManager.putExtras(extras);
                        Log.d(TAG, "onItemSelected: conected device: " +deviceSelected.getName() );
                        getActivity().sendBroadcast(intentManager);

                        if (getDialog()!=null && getDialog().isShowing()){
                            getDialog().dismiss();
                        }
                    }else{
                        Toast.makeText(getActivity(), "No se puede conectar a este dispositivo", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onItemSelected: No se puede conectar a este dispositivo: " +deviceSelected.getName() );
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvItemsListDevices.setAdapter(adapter);
        rvItemsListDevices.setLayoutManager(layoutManager);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onClosedialog();
    }

    public void addDevice(BluetoothDevice device) {
        if (device == null || device.getName() == null || device.getName().isEmpty()) {
            return;
        }
        for (BluetoothDevice deviceInList : alistDevices) {
            if (deviceInList.getAddress().equals(device.getAddress())) {
                return;
            }
        }


        title = getActivity().getString(R.string.dlg_bluetooth_found_device);
        getDialog().setTitle(title);
        if (!alistDevices.contains(device)) {
            alistDevices.add(device);
        }
        adapter.notifyDataSetChanged();

    }


    private void startFindDevices() {

        mBluetoothAdapter.startDiscovery();
        title = getActivity().getString(R.string.dlg_bluetooth_searching);

        Log.d(TAG, "startFindDevices: ");
        Set<BluetoothDevice> pairDevices = mBluetoothAdapter.getBondedDevices();
        if (pairDevices != null && pairDevices.size() > 0) {

            ArrayList<BluetoothDevice> alistDevices = new ArrayList<>();
            for (BluetoothDevice device : pairDevices) {
                title = getActivity().getString(R.string.dlg_bluetooth_found_device);

                int type = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    type = device.getType();
                }
                if (getDialog() != null && getDialog().isShowing()) {
                    getDialog().setTitle(title);
                }
                alistDevices.add(device);
            }
        }
    }


    private final BroadcastReceiver mReceiverBtFound = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String  nameDevice = "";
            if (device!=null) nameDevice =device.getName();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "ACTION_FOUND " + device.getName());
//                BluetoothClass as = device.getBluetoothClass();


                addDevice(device);

            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                Log.d(TAG, "ACTION_ACL_CONNECTED "+ nameDevice);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
                Log.d(TAG, "ACTION_DISCOVERY_FINISHED");

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
                Log.d(TAG, "ACTION_ACL_DISCONNECT_REQUESTED  " + nameDevice);
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                Log.d(TAG, "ACTION_ACL_DISCONNECTED "+nameDevice);
            }else if ("HEADSET_INTERFACE_CONNECTED".equals(action)) {
                Log.d(TAG, "HEADSET_INTERFACE_CONNECTED "+nameDevice);
                if (MainActivity.iBtHeadset != null) {
                    try {
                        MainActivity.iBtHeadset.connect(device);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() != null && mReceiverBtFound != null) {
            getActivity().unregisterReceiver(mReceiverBtFound);
        }

        if (mListener!=null){
            mListener.onClosedialog();
        }
    }


    private void pairDevice(BluetoothDevice device) {
        try {

            Method m = device.getClass()
                    .getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();

        } catch (IllegalStateException e) {
            Log.d(TAG, "show: " + e.getMessage());
        }

    }


    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
    }
}
