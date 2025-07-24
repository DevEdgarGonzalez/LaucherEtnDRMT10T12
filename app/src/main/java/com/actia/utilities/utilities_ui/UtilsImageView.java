package com.actia.utilities.utilities_ui;

import android.content.Context;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import android.widget.ImageView;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


public class UtilsImageView {

    public static void loadImagePath(Context context, String path, ImageView imageView){
        Glide.with(context)

                .load(path)
                .apply(getOptions(context))
                .into(imageView);



    }

    private static RequestOptions getOptions(Context context){

        CircularProgressDrawable circularProgressDrawable =new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        return new RequestOptions()
//                .centerCrop()
                .centerInside()
                .placeholder(circularProgressDrawable)
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .priority(Priority.HIGH);
    }
}
