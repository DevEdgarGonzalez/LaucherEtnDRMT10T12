package com.actia.nosotros;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_external_storage.UtilitiesFile;

import java.io.File;

import me.biubiubiu.justifytext.library.JustifyTextView;

public class NosotrosShowTextFragment extends Fragment {
    private static final String ARG_PATH_LONG_CONTENT ="longContent";
    private static final String ARG_PATH_SHORT_CONTENT ="shortContent";
    private File fileTxtLongContent;
    private File fileTxtShortContent;
    private OnShowTextFgmntListener mListener;


    public NosotrosShowTextFragment() {
    }

    public static NosotrosShowTextFragment newInstance(String pathLongContent, String pathShortContent) {
        NosotrosShowTextFragment fragment = new NosotrosShowTextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH_LONG_CONTENT, pathLongContent);
        args.putString(ARG_PATH_SHORT_CONTENT, pathShortContent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_PATH_LONG_CONTENT)){
                fileTxtLongContent = new File(getArguments().getString(ARG_PATH_LONG_CONTENT));
            }
            if (getArguments().containsKey(ARG_PATH_SHORT_CONTENT)){
                fileTxtShortContent = new File(getArguments().getString(ARG_PATH_SHORT_CONTENT));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nosotros_show_text, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        JustifyTextView jtvLongContentWe = view.findViewById(R.id.jtvLongContentWe);
        TextView tvShortContentWe = view.findViewById(R.id.tvShortContentWe);
//        View llytParentShowText =  view.findViewById(R.id.llytParentShowText);
        View ibtnFgmntShowtext =  view.findViewById(R.id.ibtnFgmntShowtext);



        jtvLongContentWe.setText(UtilitiesFile.getTextFileMovie(fileTxtLongContent.getPath(),""));
        tvShortContentWe.setText(UtilitiesFile.getTextFileMovie(fileTxtShortContent.getPath(),""));

        ibtnFgmntShowtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null){
                    mListener.onCloseShowTextFgmntInteraction();
                }
            }
        });

//        llytParentShowText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mListener!=null){
//                    mListener.onCloseShowTextFgmntInteraction();
//                }
//            }
//        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShowTextFgmntListener){
            mListener= (OnShowTextFgmntListener) context;
        }else{
            throw new RuntimeException(context.toString()
            +"must implement OnShowTextFgmntListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    public interface OnShowTextFgmntListener{
        void onCloseShowTextFgmntInteraction();
    }
}
