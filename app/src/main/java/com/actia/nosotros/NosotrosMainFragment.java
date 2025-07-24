package com.actia.nosotros;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ContextApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;
import com.actia.utilities.utilities_file.FileExtensionFilterImages;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class NosotrosMainFragment extends Fragment implements  NosotrosMainAdapater.ClickListenerItemNosotros {


//    private int positionCategory= ConstantesGeneric_t10_t12.CATEGORY_NO_DETECTED;
    private File[] elementsNosotros;


    public NosotrosMainFragment() {
    }

    public static NosotrosMainFragment newInstance(/*String pathImage*/) {
        NosotrosMainFragment fragment = new NosotrosMainFragment();
//        Bundle args = new Bundle();
//        args.putInt(ConstantesGeneric_t10_t12.ARG_POSITION_CATEGORY,ConstantesGeneric_t10_t12.CATEGORY_NO_DETECTED);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            positionCategory= getArguments().getInt(ConstantesGeneric_t10_t12.ARG_POSITION_CATEGORY);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment= inflater.inflate(R.layout.fragment_nosotros_main, container, false);
        RecyclerView recyclerNosotrosMainFgmnt = viewFragment.findViewById(R.id.recyclerNosotrosMainFgmnt);

        elementsNosotros = ConfigMasterMGC.getConfigSingleton().getItemsNosotros();

        NosotrosMainAdapater  nosotrosMainAdapater =  new NosotrosMainAdapater(getContext(),elementsNosotros);
        nosotrosMainAdapater.setOnClickListenerNosotros(this);


        //get custom grid or grid normal
        GridLayoutManager gridLayoutManager;
        if (ContextApp.enableCustomization == false) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        } else {
            gridLayoutManager = new OrderElementsFourThree(getActivity(), elementsNosotros.length).getGridManagerVtwo();
        }

        recyclerNosotrosMainFgmnt.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(0,0,0,30));
        recyclerNosotrosMainFgmnt.setLayoutManager( gridLayoutManager);
        recyclerNosotrosMainFgmnt.setAdapter(nosotrosMainAdapater);







        return viewFragment;
    }


    @Override
    public void onItemClickNosotros(View view, int position) {
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
            NosotrosWindowPopUp nosotrosWindowPopUp = new NosotrosWindowPopUp(getContext(), alistImagesToShow);
            nosotrosWindowPopUp.showWindowPopup();
        }else{
            Toast.makeText(getContext(), getContext().getString(R.string.images_dont_avaliable), Toast.LENGTH_SHORT).show();
        }
    }
}
