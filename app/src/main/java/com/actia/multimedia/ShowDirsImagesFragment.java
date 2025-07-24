package com.actia.multimedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_file.FileExtensionFilterImages;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class ShowDirsImagesFragment extends Fragment implements  MultimediaMainAdapater.ClickListenerItemMultimedia {


    private static final String ARG_PATH_MAIN = "arg_path_main";
    private static final String ARG_SHOW_CLOSE_BTN = "showBtnClose";

    private String pathRoot;
    private boolean showBtnClose = true;
    private File[] elementsNosotros;



    public ShowDirsImagesFragment() {
    }

    public static ShowDirsImagesFragment newInstance(String mainPath, boolean showBtnClose) {
        ShowDirsImagesFragment fragment = new ShowDirsImagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH_MAIN,mainPath);
        args.putBoolean(ARG_SHOW_CLOSE_BTN,showBtnClose);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pathRoot= getArguments().getString(ARG_PATH_MAIN);
            if (getArguments().containsKey(ARG_SHOW_CLOSE_BTN)){
                showBtnClose=getArguments().getBoolean(ARG_SHOW_CLOSE_BTN);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment= inflater.inflate(R.layout.fragment_show_images_dirs, container, false);
        RecyclerView recyclerNosotrosMainFgmnt = (RecyclerView) viewFragment.findViewById(R.id.recyclerNosotrosMainFgmnt);

        elementsNosotros = ConfigMasterMGC.getConfigSingleton().getItemsSd(pathRoot);

        MultimediaMainAdapater nosotrosMainAdapater =  new MultimediaMainAdapater(getContext(),elementsNosotros);
        nosotrosMainAdapater.setOnClickListenerMultimedia(this);


        //get custom grid or grid normal
        GridLayoutManager gridLayoutManager;
        if (ConfigMasterMGC.enableCustomization == false) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            gridLayoutManager = new OrderElementsFourThree(getActivity(), elementsNosotros.length).getGridManagerVtwo();
        }

        recyclerNosotrosMainFgmnt.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(0,0,0,30));
        recyclerNosotrosMainFgmnt.setLayoutManager( gridLayoutManager);
        recyclerNosotrosMainFgmnt.setAdapter(nosotrosMainAdapater);




        View  ibtnCloseShowImageFgmnt = viewFragment.findViewById(R.id.ibtnCloseShowImageFgmnt);
        if (showBtnClose==false){
            ibtnCloseShowImageFgmnt.setVisibility(View.INVISIBLE);
        }
        ibtnCloseShowImageFgmnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        return viewFragment;
    }


    public void onItemClickMultimedia(View view, int position) {
        File[] arrayImagesFromDir = elementsNosotros[position].listFiles(new FileExtensionFilterImages());

        ArrayList<File> alistImagesToShow = new ArrayList<>();

        for (File aFile: arrayImagesFromDir){
            String nameCategory =  elementsNosotros[position].getName();
            String imageName = aFile.getName().split("\\.")[0];
            if (imageName != null && nameCategory!=null) {
                if (!imageName.equals(nameCategory)){
                    alistImagesToShow.add(aFile);
                }
            }
        }
        if (alistImagesToShow!=null && alistImagesToShow.size()>0){
            Collections.sort(alistImagesToShow);
            MultimediaWindowPopUp nosotrosWindowPopUp = new MultimediaWindowPopUp(getContext(), alistImagesToShow);
            nosotrosWindowPopUp.showWindowPopup();
        }else{
            Toast.makeText(getContext(), getContext().getString(R.string.we_images_dont_avaliable), Toast.LENGTH_SHORT).show();
        }
    }
}
