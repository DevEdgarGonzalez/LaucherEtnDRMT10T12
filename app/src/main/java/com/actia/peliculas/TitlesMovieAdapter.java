package com.actia.peliculas;

import android.app.Activity;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actia.infraestructure.ContextApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_ui.UtilitiesCardView;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 15/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class TitlesMovieAdapter extends RecyclerView.Adapter<TitlesMovieAdapter.TitleMovieViewHolder> {
    private final ArrayList<Movie> alistMovies;
    private final Context context;
    OrderElementsFourThree order;

    ClickListenerTitleMovie clickListenerTitleMovie;

    public TitlesMovieAdapter(ArrayList<Movie> alistMovies, Context context) {
        this.alistMovies = alistMovies;
        this.context = context;
        order = new OrderElementsFourThree((Activity)context, alistMovies.size());
    }

    @Override
    public TitleMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title_movie,parent,false);

        return new TitleMovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TitleMovieViewHolder holder, int position) {
        ImageWorker imageWorker =  new ImageWorker();

        Movie movieActual = alistMovies.get(position);

        holder.lblItemTitleMovie.setText(movieActual.name.replace("_es", "")
                                                        .replace("_ips", "")
                                                        .replace("_en","")
                                                        .replace("_", " ")
                                                        .replace("max", ""));

        if (ContextApp.enableCustomization==true){
            //FIXME: Comentar cuando no se quiere customizar, para que funcione correctamente se debe de descomentar una parte en ABookFragmentActivity
            order.configureElementInLayout(holder.llytItemTitleMov, position);
        }

        String pathImage = movieActual.image;
        if (pathImage!=null){
            imageWorker.loadBitmap(pathImage,holder.imgvItemTitleMovie,context,165,200);
        }else{
            holder.imgvItemTitleMovie.setImageResource(R.drawable.no_movie);
        }
    }

    @Override
    public int getItemCount() {
        return alistMovies.size();
    }

    public class TitleMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imgvItemTitleMovie;
        public TextView lblItemTitleMovie;
        public CardView cardvTitleMovie;
        public LinearLayout llytItemTitleMov;

        public TitleMovieViewHolder(View itemView) {
            super(itemView);
            imgvItemTitleMovie = itemView.findViewById(R.id.imgvItemTitleMovie);
            lblItemTitleMovie = itemView.findViewById(R.id.lblItemTitleMovie);
            cardvTitleMovie = itemView.findViewById(R.id.cardvTitleMovie);
            llytItemTitleMov = itemView.findViewById(R.id.llytItemTitleMov);

            UtilitiesCardView.setTransparentBackgroundCardView(cardvTitleMovie,context);


            llytItemTitleMov.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (clickListenerTitleMovie!=null){
                clickListenerTitleMovie.OnItemClick(view, getAdapterPosition());
            }
        }
    }

    public void setOnClickListenerTitleMov(ClickListenerTitleMovie clickListenerTitleMovie){
        this.clickListenerTitleMovie = clickListenerTitleMovie;
    }


    public interface ClickListenerTitleMovie {
        void OnItemClick(View view, int position);
    }
}
