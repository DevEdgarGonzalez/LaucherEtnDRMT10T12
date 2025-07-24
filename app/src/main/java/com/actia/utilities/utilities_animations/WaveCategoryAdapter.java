package com.actia.utilities.utilities_animations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.ImageWorker;


/**
 * Created by Edgar Gonzalez on 07/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class  WaveCategoryAdapter extends BaseAdapter{

    private final Context mContext;
    private String pathImg;
    private final LayoutInflater inflater;
    private final Animation animationTraslate;
    Drawable imgDrawable;
//    static private TypedArray images;
//    Drawable dwImgBackg;

//    private int imgBarCategoryActual;



    public WaveCategoryAdapter(Context mContext, String pathImg) {
        this.mContext = mContext;
        this.pathImg = pathImg;
        animationTraslate = getAnimationForScreenTactil();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        if (images==null){
//            images = mContext.getResources().obtainTypedArray(R.array.arrayBarMain);
//        }
//
//        imgBarCategoryActual = images.getResourceId(idCategory,1);
//        dwImgBackg = LoadBackgraoundWave.loadBkByNameMain(pathImg, mContext);
    }
    public WaveCategoryAdapter(Context mContext, Drawable imgDrawable) {
        this.mContext = mContext;
        this.imgDrawable = imgDrawable;
        animationTraslate = getAnimationForScreenTactil();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    private Animation getAnimationForScreenTactil(){
        Animation animationTraslate = null;
        if (ContextApp.TYPE_SCREEN== ConstantsApp.TYPE_SCREEN_10PLG){
            animationTraslate = AnimationUtils.loadAnimation(mContext, R.anim.anim_traslate_left_rigth_10plg);
        }else if (ContextApp.TYPE_SCREEN== ConstantsApp.TYPE_SCREEN_10PLG){
            animationTraslate = AnimationUtils.loadAnimation(mContext, R.anim.anim_traslate_left_rigth_12plg);
        }else{
            animationTraslate = AnimationUtils.loadAnimation(mContext, R.anim.anim_traslate_left_rigth_7plg);
        }
        return animationTraslate;
    }


    public int getCount() {
        return 1;
    }

    public Object getItem(int position) {
        return 1;
    }

    public long getItemId(int position) {
        return 1;
    }

    // create a new ImageView for each item referenced by the Adapter
    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageWorker m = new ImageWorker();
        ViewHolder vh;

        Drawable drawableCategory = null;
        if (pathImg!=null){
            drawableCategory =  Drawable.createFromPath(pathImg);

        }else if (imgDrawable!=null){
            drawableCategory = imgDrawable;
        }

        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.cell_image_animated_one, null);
            vh.imgvAnimationCellOneImg = convertView.findViewById(R.id.imgvAnimationCellOneImg);
            vh.imgvAnimationCellOneImg.setImageDrawable(drawableCategory);
//            vh.imgvBarOneAnim = (ImageView) convertView.findViewById(R.id.imgvBarOneAnim);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

//        RelativeLayout.LayoutParams a =  new RelativeLayout.LayoutParams(250,500);
//        a.setMargins(0,0,0,50);
//        vh.imgvBarOneAnim.setLayoutParams(a);

        m.loadBitmap(pathImg, vh.imgvAnimationCellOneImg, mContext, 250, 250);    //El tamaño de la imagen se cambia desde el xml
//        vh.imgvBarOneAnim.setBackground(dwImgBackg);
        vh.imgvAnimationCellOneImg.startAnimation(animationTraslate);
        vh.imgvAnimationCellOneImg.setImageDrawable(drawableCategory);
        animationTraslate.setFillAfter(true);
        convertView.setId(0);

        return convertView;
    }



    static class ViewHolder {
        ImageView imgvAnimationCellOneImg;
//        ImageView imgvBarOneAnim;
    }

}
