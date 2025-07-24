package com.actia.mexico.imageslider;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utils_language.UtilsLanguage;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.io.File;


public class ImageSliderFragment extends Fragment {


    private static final String ARG_PATH = "path";
    private String rootPath = null;
    private SliderLayout sliderLayout;
    private View ibtnClose_fis;
    private View llytContainerSlider_fis;
    private File pathNosotros;
    private File[] filesImages;

    private ImageView imgvOneImage_fis;

    public ImageSliderFragment() {
    }

    public static ImageSliderFragment newInstance(String path) {
        ImageSliderFragment fragment = new ImageSliderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rootPath = getArguments().getString(ARG_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_slider, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sliderLayout = view.findViewById(R.id.sliderLayout_fis);
        ibtnClose_fis = view.findViewById(R.id.ibtnClose_fis);
        llytContainerSlider_fis = view.findViewById(R.id.llytContainerSlider_fis);
        imgvOneImage_fis = view.findViewById(R.id.imgvOneImage_fis);
        pathNosotros = new File(rootPath);
        filesImages = pathNosotros.listFiles(UtilsLanguage.getFileExtensionImageByLanguage(pathNosotros));

        if (filesImages==null){
            getActivity().onBackPressed();
            return;
        }


        if (filesImages.length == 1) {
            configureImageViewOneImage();
        } else {
            configureSlider();
        }


        ibtnClose_fis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }

    private void configureImageViewOneImage() {
        llytContainerSlider_fis.setVisibility(View.INVISIBLE);
        Drawable img = Drawable.createFromPath(filesImages[0].getPath());
        if (img == null) {
            getActivity().onBackPressed();
        }
        imgvOneImage_fis.setImageDrawable(img);
    }

    private void configureSlider() {
        imgvOneImage_fis.setVisibility(View.INVISIBLE);
        for (int i = 0; i < filesImages.length; i++) {
            TextSliderView textSliderView = new TextSliderView(getContext());
            textSliderView
                    .description("")
                    .image(filesImages[i])
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {

                        }
                    });
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", "");
            sliderLayout.addSlider(textSliderView);
        }
    }
}