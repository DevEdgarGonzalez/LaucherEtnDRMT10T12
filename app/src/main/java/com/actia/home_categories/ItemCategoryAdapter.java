package com.actia.home_categories;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.actia.infraestructure.ItemsHome;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.UtilsImageView;

import java.util.ArrayList;

public class ItemCategoryAdapter extends RecyclerView.Adapter<ItemCategoryAdapter.ItemCategoryViewHolder> {

    private final Context context;
    private final ArrayList<ItemsHome> alistItemsHome;
    private final OnHomeCategoryListener mListener;
    public ArrayList<Animation> arrayAnimation = null;
    private int animationRandom = 0;

    public ItemCategoryAdapter(Context context, ArrayList<ItemsHome> alistItemsHome, OnHomeCategoryListener mListener) {
        this.context = context;
        this.alistItemsHome = alistItemsHome;
        this.mListener = mListener;


        Animation one = AnimationUtils.loadAnimation(context, R.anim.animone);
        Animation two = AnimationUtils.loadAnimation(context, R.anim.animtwo);
        Animation three = AnimationUtils.loadAnimation(context, R.anim.animthree);
        Animation four = AnimationUtils.loadAnimation(context, R.anim.animfour);
        Animation five = AnimationUtils.loadAnimation(context, R.anim.animfive);
        Animation six = AnimationUtils.loadAnimation(context, R.anim.animsix);


        arrayAnimation = new ArrayList<>();
        arrayAnimation.add(one);
        arrayAnimation.add(two);
        arrayAnimation.add(three);
        arrayAnimation.add(four);
        arrayAnimation.add(five);
        arrayAnimation.add(six);
    }

    @Override
    public ItemCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_category, parent, false);
        return new ItemCategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemCategoryViewHolder holder, int position) {
        holder.bind(alistItemsHome.get(position));
    }

    @Override
    public int getItemCount() {
        return alistItemsHome.size();
    }

    class ItemCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgvCoverCategory_itmcat;
        //TextView tvNameCat_itemcat;
        View llytBk_itemcat;


        public ItemCategoryViewHolder(View itemView) {
            super(itemView);
            imgvCoverCategory_itmcat = itemView.findViewById(R.id.imgvCoverCat_itemcat);
            //tvNameCat_itemcat = (TextView) itemView.findViewById(R.id.tvNameCat_itemcat);
            llytBk_itemcat = itemView.findViewById(R.id.llytBk_itemcat);
        }

        private void bind(ItemsHome itemsHome) {

            //UtilsImageView.loadImagePath(context,itemsHome.pathImg,imgvCoverCategory_itmcat);
            Bitmap bitmap = BitmapFactory.decodeFile(itemsHome.pathImg);
            imgvCoverCategory_itmcat.setImageBitmap(bitmap);
            llytBk_itemcat.setOnClickListener(this);
            //addAnimation(imgvCoverCategory_itmcat);
            /*tvNameCat_itemcat.setText(UtilsLanguage.getTitleCategoryByLanguage(itemsHome));
            tvNameCat_itemcat.setTypeface(getTypefaceMainActivity(context));*/

        }

        @Override
        public void onClick(View v) {
            if (mListener != null)
                mListener.onCategorySelected(alistItemsHome.get(getAdapterPosition()));
        }
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

}
