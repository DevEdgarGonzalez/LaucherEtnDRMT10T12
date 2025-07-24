package com.actia.music;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.UtilsFonts;
import com.actia.utilities.utilities_ui.UtilitiesCardView;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.io.File;

/**
 * Created by Edgar Gonzalez on 17/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class MusicGenresRecyclerAdapter extends RecyclerView.Adapter<MusicGenresRecyclerAdapter.GenreMusicViewHolder>{
    Context context;
    String [] arrayGenreMusic;
    ClickListenerGenreMusic clickListenerGenreMusic;
    String pathMusic;
    OrderElementsFourThree order;

    public MusicGenresRecyclerAdapter(Context context, String[] arrayGenreMusic) {
        ConfigMasterMGC config = ConfigMasterMGC.getConfigSingleton();
        this.context = context;
        this.arrayGenreMusic = arrayGenreMusic;
        pathMusic = config.getMUSIC_PATH();
        order = new OrderElementsFourThree((Activity)context, arrayGenreMusic.length);

    }

    @Override
    public GenreMusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemGenreMusic = LayoutInflater.from(context).inflate(R.layout.item_gernre_music,parent,false);
        return new GenreMusicViewHolder(itemGenreMusic);
    }

    @Override
    public void onBindViewHolder(GenreMusicViewHolder holder, int position) {

        if (arrayGenreMusic[position]!=null && !arrayGenreMusic[position].equals("")){
            UtilitiesCardView.setTransparentBackgroundCardView(holder.cardvGenreMusic,context);


            //holder.txtvGenreMusic.setText(UtilsLanguage.getName(arrayGenreMusic[position]));
//            holder.txtvGenreMusic.setTypeface(UtilsFonts.getTypefaceCategoryMusic(context));


           /* if (ContextApp.enableCustomization==true){
                //FIXME: Comentar cuando no se quiere customizar, para que funcione correctamente se debe de descomentar una parte en ABookFragmentActivity
                order.configureElementInLayout(holder.llytItemGenreMusic, position);
            }*/


            String pathIcon = pathMusic + "/" + arrayGenreMusic[position] + "/" + arrayGenreMusic[position].split("_")[0] + ".png";
            File file = new File(pathIcon);


            if (file.exists()) {
                Uri mUri = Uri.parse(pathIcon);
                holder.imgvCoverItemGnreMusic.setImageURI(mUri);
            } else
                holder.imgvCoverItemGnreMusic.setImageResource(R.drawable.bkg_movie);

        }


    }

    @Override
    public int getItemCount() {
        return arrayGenreMusic.length;
    }

    public class GenreMusicViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardvGenreMusic;
        //TextView txtvGenreMusic;
        ImageView imgvCoverItemGnreMusic;
        LinearLayout llytItemGenreMusic;

        public GenreMusicViewHolder(View itemView) {
            super(itemView);
            cardvGenreMusic = itemView.findViewById(R.id.cardvGenreMusic);
            //txtvGenreMusic = itemView.findViewById(R.id.txtvGenreMusic);
            imgvCoverItemGnreMusic = itemView.findViewById(R.id.imgvCoverItemGnreMusic);
            llytItemGenreMusic = itemView.findViewById(R.id.llytItemGenreMusic);
            llytItemGenreMusic.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListenerGenreMusic!=null){
                clickListenerGenreMusic.OnItemClickGenreMusic(view, getAdapterPosition());
            }
        }
    }

    public void setOnItemGenreMusicListener(ClickListenerGenreMusic clickListenerGenreMusic){
        this.clickListenerGenreMusic =  clickListenerGenreMusic;
    }


    public interface ClickListenerGenreMusic{
        void OnItemClickGenreMusic(View view, int position);
    }
}
