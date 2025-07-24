package com.actia.mapas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;


public class ConnectingFragmentDialog extends DialogFragment {

    private String mDialogText;
    private String mDialogTitle;



    public void setDialogText(String text) {
        mDialogText = text;
    }

    public void setDialogTitle(String title) {
        mDialogTitle = title;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mDialogText)
                .setCancelable(true)
                .setTitle(mDialogTitle);

        return builder.create();
    }

}
