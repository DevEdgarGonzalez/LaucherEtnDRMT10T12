package com.actia.home_categories;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ItemsHome;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_external_storage.UtilitiesFile;
import com.actia.utilities.utilities_ui.ConfigSpaceCardViewDecoration;
import com.actia.utilities.utils_language.UtilsLanguage;
import com.google.gson.Gson;

import java.util.ArrayList;


public class HomeCategoriesFragment extends Fragment implements OnHomeCategoryListener  {

    private ItemCategoryAdapter adapter;
    private RecyclerView rvContainerCategories_fgmntct;
    private OnHomeCategoryListener mListener= null;
    private final Gson gson= new Gson();


    public HomeCategoriesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories_home, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvContainerCategories_fgmntct = view.findViewById(R.id.rvContainerCategories_fgmntct);
        ConfigMasterMGC configMaster = ConfigMasterMGC.getConfigSingleton();
        configMaster.getJSONConfig();
        ArrayList<ItemsHome> home = configMaster.getAppHome("home");


        if(!home.isEmpty()){
            adapter = new ItemCategoryAdapter(getContext(), home, this);
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false);
            rvContainerCategories_fgmntct.setAdapter(adapter);
            rvContainerCategories_fgmntct.setLayoutManager(layoutManager);

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

            if(dpHeight > 800 && dpWidth > 1400) {
                rvContainerCategories_fgmntct.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(75, 75, 35, 35));
            }
            else if (dpHeight < 800 && dpWidth < 1400){
                rvContainerCategories_fgmntct.addItemDecoration(ConfigSpaceCardViewDecoration.setSpaceAllSides(50, 50, 17, 17));
            }
        }
    }

    @Override
    public void onCategorySelected(ItemsHome category) {
        try {
            Intent intent = new Intent();
            intent.setClassName(getContext(), category.getClassName());
            if (category.getPackageName() != null && !category.getPackageName().isEmpty()) {
                intent.putExtra(ConstantsApp.ARG_MODULO, category.getPackageName());
            }

            /*if(UtilsLanguage.isAppInEnglish())
                intent.putExtra(ConstantsApp.ARG_IMG_SUBGENRE, category.getPathImg_en());
            else*/
                intent.putExtra(ConstantsApp.ARG_IMG_SUBGENRE, category.getPathImg());

            intent.putExtra(ConstantsApp.ARG_CATEGORY, gson.toJson(category));

            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeCategoryListener){
            mListener = (OnHomeCategoryListener) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener=null;
    }
}