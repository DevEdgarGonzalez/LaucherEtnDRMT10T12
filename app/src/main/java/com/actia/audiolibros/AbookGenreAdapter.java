package com.actia.audiolibros;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.ImageWorker;

import java.io.File;

public class AbookGenreAdapter extends RecyclerView.Adapter<AbookGenreAdapter.AbookGenreViewHolder> {
    private final Context context;
    private final File[] arrayDirs;
    private final OnAbookGenreListener mListener;


    public AbookGenreAdapter(Context context, File[] arrayDirs, OnAbookGenreListener mListener) {
        this.context = context;
        this.arrayDirs = arrayDirs;
        this.mListener = mListener;
    }

    @Override
    public AbookGenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_abook_genre, parent, false);
        return new AbookGenreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AbookGenreViewHolder holder, int position) {
        holder.bind(arrayDirs[position]);
    }

    @Override
    public int getItemCount() {
        return arrayDirs.length;
    }

    class AbookGenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgvCategoryAbook_itema;
        TextView tvNameCategory__itema;


        public AbookGenreViewHolder(View itemView) {
            super(itemView);
            imgvCategoryAbook_itema = itemView.findViewById(R.id.imgvCover_itmcag);
            tvNameCategory__itema = itemView.findViewById(R.id.tvNameGenre_itmcag);
        }
         public void bind(File path){
             File imgPath = new File(path, path.getName() + ".png");
             ImageWorker m = new ImageWorker();
             m.loadBitmap(imgPath.getPath(),imgvCategoryAbook_itema,context,150,200);
             tvNameCategory__itema.setText(path.getName());
             imgvCategoryAbook_itema.setOnClickListener(this);

         }

        @Override
        public void onClick(View v) {
            if (mListener!=null){
                File fileItemSelected = arrayDirs[getAdapterPosition()];
                mListener.onClickCategory(fileItemSelected,getPathImg(fileItemSelected));
            }
        }




    }


    private String getPathImg(File path){
        File imgPath = new File(path, path.getName() + ".png");
        return imgPath.getPath();
    }
}
