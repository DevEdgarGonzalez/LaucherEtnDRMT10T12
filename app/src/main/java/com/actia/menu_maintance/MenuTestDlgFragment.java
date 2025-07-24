package com.actia.menu_maintance;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actia.infraestructure.ConstantsApp;
import com.actia.mexico.launcher_t12_generico_2018.R;


public class MenuTestDlgFragment extends DialogFragment {


    private OnFragmentInteractionListener mListener;

    public MenuTestDlgFragment() {
    }

    public static MenuTestDlgFragment newInstance() {
        MenuTestDlgFragment fragment = new MenuTestDlgFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewDialog = inflater.inflate(R.layout.fragment_menu_test_dlg, container, false);
        ImageButton ibtnCloseMenuTesting = viewDialog.findViewById(R.id.ibtnCloseMenuTesting);
        Button btnOpenAppBenchMarkMenuTesting = viewDialog.findViewById(R.id.btnOpenAppBenchMarkMenuTesting);

        ibtnCloseMenuTesting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    getDialog().dismiss();
                }
            }
        });

        btnOpenAppBenchMarkMenuTesting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName(ConstantsApp.nameAppTesting, ConstantsApp.nameAppTestingFirsActivity));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), getString(R.string.app_testing_not_found)+"  \n"+ getString(R.string.msg_verify_app_install), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


        return viewDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = super.onCreateDialog(savedInstanceState);
        dlg.setCancelable(true);
        dlg.setTitle("Menu");
        return dlg;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
