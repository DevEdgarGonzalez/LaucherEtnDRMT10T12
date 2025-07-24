package com.actia.utilities.utilities_animations;

/**
 * Adapter for Home GridView
 *
 * @see ItemsHome
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ItemsHome;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.UtilsFonts;
import com.actia.utilities.utilities_ui.ImageWorker;

import java.util.ArrayList;
import java.util.HashMap;

public class WaveMultipleObjectsAdapter extends BaseAdapter {
    int positionABC;
    private final Context mContext;
    public static HashMap<Integer, ItemsHome> photosMap = null;
    @SuppressWarnings("FieldCanBeLocal")
    private final int count = 0;
    private int animationRandom = 0;
    private ArrayList<Integer> numbersRamdom;
    public ArrayList<Animation> arrayAnimation = null;
    //	private Animation facebookAnimation=null;
//	private Animation twitterAnimation=null;
    private LayoutInflater inflater;
    public ArrayList<ImageView> imgvToAnimate = new ArrayList<>();
//    private Animation rotate;

    @SuppressLint("UseSparseArrays")
    WaveMultipleObjectsAdapter(Context c, HashMap<Integer, ItemsHome> categoriesApp) {

        mContext = c;
        photosMap = categoriesApp;


        Animation one = AnimationUtils.loadAnimation(mContext, R.anim.animone);
        Animation two = AnimationUtils.loadAnimation(mContext, R.anim.animtwo);
        Animation three = AnimationUtils.loadAnimation(mContext, R.anim.animthree);
        Animation four = AnimationUtils.loadAnimation(mContext, R.anim.animfour);
        Animation five = AnimationUtils.loadAnimation(mContext, R.anim.animfive);
        Animation six = AnimationUtils.loadAnimation(mContext, R.anim.animsix);


        arrayAnimation = new ArrayList<>();
        arrayAnimation.add(one);
        arrayAnimation.add(two);
        arrayAnimation.add(three);
        arrayAnimation.add(four);
        arrayAnimation.add(five);
        arrayAnimation.add(six);
        ConfigMasterMGC configSingleton = ConfigMasterMGC.getConfigSingleton();

        if (configSingleton.getJSONConfig()) {
            loadNewUI(configSingleton);
        }
    }

    @SuppressLint("UseSparseArrays")
    @SuppressWarnings({"unused", "UnusedAssignment"})
    private void loadNewUI(ConfigMasterMGC configSingleton) {

        numbersRamdom = new ArrayList<>();

        for (int i = 0; i < (photosMap.size()); i++)
            numbersRamdom.add(i);

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public int getCount() {
        if (photosMap.size() < 6) {
            return photosMap.size();
        } else {
            return 5;
        }
    }

    public Object getItem(int position) {
        return photosMap.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageWorker m = new ImageWorker();
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.cell_image_animated_multi, null);
            vh.item_home = convertView.findViewById(R.id.grid_item_image);
            vh.cell_bkg = convertView.findViewById(R.id.cell_bkg);
            vh.tv_home = convertView.findViewById(R.id.tv_home);
//            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        ItemsHome photoclient = photosMap.get(numbersRamdom.get(position));
//        Drawable backg= LoadBackgraoundWave.loadBkByNameMain(photoclient.getPathImg(),mContext);

        if (!photoclient.isAplication()) {
            if (photoclient.getPathImg() != null) {
//            		   m.loadBitmap(photoclient.getPathImg(),vh.imgvAnimationCellOneImg, mContext,150,150);
//                vh.cell_bkg.setBackgroundResource(imgBarCategoryActual);
//                vh.cell_bkg.setBackground(backg);

                m.loadBitmap(photoclient.getPathImg(), vh.item_home, mContext, 167, 167);
                vh.item_home.setAlpha(0.43f);
                vh.tv_home.setText(photoclient.getTitle() + " ");
//                vh.tv_home.setTypeface(UtilsFonts.getTypefaceMainActivity(mContext));
            } else vh.item_home.setImageResource(R.drawable.default_photo);
        } else if (photoclient.isAplication()) {
            if (photoclient.getPathImg() != null) {
//                	   m.loadBitmap(photoclient.getPathImg(),vh.imgvAnimationCellOneImg, mContext,150,150);
//                vh.cell_bkg.setBackground(backg);

                RelativeLayout.LayoutParams a = new RelativeLayout.LayoutParams(200, 400);
                a.setMargins(0, 100, 0, 200);
//                vh.cell_bkg.setLayoutParams(a);

                if (vh == null) return convertView;
                m.loadBitmap(photoclient.getPathImg(), vh.item_home, mContext, 167, 167);
                addAnimation(vh.item_home);
//                imgvToAnimate.add(vh.item_home);
                vh.tv_home.setText(photoclient.getTitle() + " ");
//                vh.tv_home.setTypeface(UtilsFonts.getTypefaceMainActivity(mContext));
            } else vh.item_home.setImageResource(R.drawable.default_aplication_home);
        }
        convertView.setId(numbersRamdom.get(position));

//        if (imgvToAnimate.size()==5){
//            addAndOrganizeAnimations();
////            reorganizeAnimations();
//        }

        return convertView;
    }

    public void addAndOrganizeAnimations() {
        positionABC = 0;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (positionABC < imgvToAnimate.size()) {
                    imgvToAnimate.get(positionABC).startAnimation(arrayAnimation.get(positionABC));
                    imgvToAnimate.get(positionABC).setVisibility(View.VISIBLE);
                    positionABC++;
                    new Handler().postDelayed(this, 500);
                }
            }
        }, 0);


    }

    /**
     * Add animation random for each app at the home section.
     *
     * @param imageView A ImageView to add animation.
     */
    private synchronized void addAnimation(final ImageView imageView) {


        if (animationRandom == arrayAnimation.size())
            animationRandom = 0;

        imageView.setAnimation(arrayAnimation.get(animationRandom));


        animationRandom++;

    }


    static class ViewHolder {
        ImageView item_home;
        LinearLayout cell_bkg;
        TextView tv_home;
    }


    public void reorganizeAnimations() {
        positionABC = 0;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (positionABC < arrayAnimation.size()) {
                    arrayAnimation.get(positionABC).start();
                    positionABC++;
                    new Handler().postDelayed(this, 300);
                }

            }
        }, 000);


    }

}
