package com.actia.multimedia;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_file.FileUtils;

import java.io.File;
import me.biubiubiu.justifytext.library.JustifyTextView;

public class MultimediaShowTextFragment extends Fragment {
    private static final String ARG_PATH_LONG_CONTENT ="longContent";
    private static final String ARG_PATH_SHORT_CONTENT ="shortContent";
    private File fileTxtLongContent;
    private File fileTxtShortContent;
    private OnShowTextFgmntListener mListener;


    public MultimediaShowTextFragment() {
    }

    public static MultimediaShowTextFragment newInstance(String pathLongContent, String pathShortContent) {
        MultimediaShowTextFragment fragment = new MultimediaShowTextFragment();
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
        return inflater.inflate(R.layout.fragment_multimedia_show_text, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        JustifyTextView jtvLongContentWe = (JustifyTextView) view.findViewById(R.id.jtvLongContentWe);
        TextView tvShortContentWe = (TextView) view.findViewById(R.id.tvShortContentWe);
//        View llytParentShowText =  view.findViewById(R.id.llytParentShowText);
        View ibtnFgmntShowtext =  view.findViewById(R.id.ibtnFgmntShowtext);



        jtvLongContentWe.setText(FileUtils.getTextFileMovie(fileTxtLongContent.getPath()));
        tvShortContentWe.setText(FileUtils.getTextFileMovie(fileTxtShortContent.getPath()));

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
