package com.actia.utilities.list_devices_bt;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.actia.mexico.launcher_t12_generico_2018.R;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 16/04/2019.
 * Actia de Mexico S.A. de C.V..
 */
public class ListDevicesAdapter extends RecyclerView.Adapter<ListDevicesAdapter.ListDevicesHolder> {
    private final ArrayList<BluetoothDevice> alistDevices;
    private final Context context;
    private final OnItemDeviceBtListener mListener;

    public ListDevicesAdapter(ArrayList<BluetoothDevice> alistDevices, Context context, OnItemDeviceBtListener listener) {
        this.alistDevices = alistDevices;
        this.context = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ListDevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_device_bluethoot,parent,false);

        return new ListDevicesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDevicesHolder holder, int position) {
        holder.tvNameItemDevice.setText(alistDevices.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return alistDevices.size();
    }

    public class ListDevicesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout llytContainerItemDevice ;
        TextView tvNameItemDevice;
        ImageView ivTypeItemDevice;
        CardView cvContainerItemDevice;

        public ListDevicesHolder(View itemView) {
            super(itemView);

            llytContainerItemDevice = itemView.findViewById(R.id.llytContainerItemDevice);
            tvNameItemDevice = itemView.findViewById(R.id.tvNameItemDevice);
            ivTypeItemDevice = itemView.findViewById(R.id.ivTypeItemDevice);
            cvContainerItemDevice = itemView.findViewById(R.id.cvContainerItemDevice);

            llytContainerItemDevice.setOnClickListener(this);
            llytContainerItemDevice.setFocusable(true);
            llytContainerItemDevice.requestFocus();


        }

        @Override
        public void onClick(View v) {
            if (mListener!=null){
                mListener.onItemSelected(alistDevices.get(getPosition()));
            }
        }
    }

    public  interface  OnItemDeviceBtListener{
        void onItemSelected(BluetoothDevice deviceSelected);
    }
}
