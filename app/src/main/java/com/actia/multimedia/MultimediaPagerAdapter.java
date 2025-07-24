package com.actia.multimedia;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.actia.mexico.launcher_t12_generico_2018.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 20/02/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class MultimediaPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<File> alistFileImgNosotros;

    public MultimediaPagerAdapter(Context context, ArrayList<File> alistFileImgNosotros) {
        this.context = context;
        this.alistFileImgNosotros = alistFileImgNosotros;
        layoutInflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return alistFileImgNosotros.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = layoutInflater.inflate(R.layout.item_pager_multimedia, container, false);

        ImageView imgvCoverItemPagerNosotros = (ImageView) itemView.findViewById(R.id.imgvCoverItemPagerNosotros);

        File currentImage =  alistFileImgNosotros.get(position);
        if (currentImage!=null && currentImage.exists()){
            Uri uriCurrentImage = Uri.parse(currentImage.getPath());
            imgvCoverItemPagerNosotros.setImageURI(uriCurrentImage);
        }else{
            imgvCoverItemPagerNosotros.setImageResource(R.drawable.album_default);
        }
        container.addView(itemView);
        return  itemView;

    }
}
