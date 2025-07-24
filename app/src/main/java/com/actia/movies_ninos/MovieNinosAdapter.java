package com.actia.movies_ninos;

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
import com.actia.peliculas.Movie;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_ui.UtilitiesCardView;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 27/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class MovieNinosAdapter extends RecyclerView.Adapter<MovieNinosAdapter.MovieNinosViewHolder> {
    ArrayList<Movie> alistMovies;
    Context context;
    ClickListener clickListener;
    OrderElementsFourThree order;

    public MovieNinosAdapter(ArrayList<Movie> alistMovies, Context context) {
        this.alistMovies = alistMovies;
        this.context = context;
        order = new OrderElementsFourThree((Activity)context, alistMovies.size());
    }

    @Override
    public MovieNinosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_title_movie, parent,false);

        return new MovieNinosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieNinosViewHolder holder, int position) {
        Movie movieActual =  alistMovies.get(position);
        if (movieActual.image!=null){
            ImageWorker imageWorker = new ImageWorker();
            imageWorker.loadBitmap(movieActual.image,holder.imgvItemTitleMovie, context,165,200);
        }else{
            holder.imgvItemTitleMovie.setImageResource(R.drawable.no_movie);
        }

        //FIXME: Comentar cuando no se quiere customizar, para que funcione correctamente se debe de descomentar una parte en ABookFragmentActivity
        if (ContextApp.enableCustomization==true){
            order.configureElementInLayout(holder.llytItemTitleMov, position);
        }

        holder.lblItemTitleMovie.setText(movieActual.name.replace("_es", "")
                                                            .replace("_ips", "")
                                                            .replace("_en","")
                                                            .replace("_", " ")
                                                            .replace("max", ""));

    }

    @Override
    public int getItemCount() {
        return alistMovies.size();
    }

    class MovieNinosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgvItemTitleMovie;
        TextView lblItemTitleMovie;
        CardView cardvTitleMovie;
        LinearLayout llytItemTitleMov;

        public MovieNinosViewHolder(View itemView) {
            super(itemView);

            lblItemTitleMovie = itemView.findViewById(R.id.lblItemTitleMovie);
            imgvItemTitleMovie = itemView.findViewById(R.id.imgvItemTitleMovie);
            cardvTitleMovie= itemView.findViewById(R.id.cardvTitleMovie);
            llytItemTitleMov = itemView.findViewById(R.id.llytItemTitleMov);




            UtilitiesCardView.setTransparentBackgroundCardView(cardvTitleMovie,context);

            llytItemTitleMov.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener !=null){
                clickListener.OnItemClickMovNinos(view, getAdapterPosition());
            }
        }
    }


    public void setOnclickListenerMovNinos(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void OnItemClickMovNinos(View vie, int position);
    }
}
