package com.actia.peliculas;

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

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.UtilitiesCardView;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;
import com.actia.utilities.utils_language.UtilsLanguage;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Edgar Gonzalez on 13/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class GenreMovieRecyclerAdapter extends RecyclerView.Adapter<GenreMovieRecyclerAdapter.GenreMovieViewHolder> {
    String[] alistGnreMovies;
    Context context;
    MovieGenreClickListener movieGenreClickListener;
    private final String videoPath;
    OrderElementsFourThree order;

    public GenreMovieRecyclerAdapter(String[] alistGnreMovies, String pathRoot, Context context) {
        ConfigMasterMGC config = ConfigMasterMGC.getConfigSingleton();
        this.alistGnreMovies = alistGnreMovies;
        this.context = context;
        videoPath = pathRoot;
        order = new OrderElementsFourThree((Activity)context, alistGnreMovies.length);

        if (alistGnreMovies != null && ConstantsApp.SORT_BY_ALPHABETICAL_CATEGORIES == true) {
            Arrays.sort(alistGnreMovies);
        }
    }

    @Override
    public GenreMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMovieGnre = LayoutInflater.from(context).inflate(R.layout.item_genre_movie, parent, false);
        return new GenreMovieViewHolder(itemMovieGnre);
    }

    @Override
    public void onBindViewHolder(GenreMovieViewHolder holder, int position) {

        if (alistGnreMovies[position] != null && !alistGnreMovies[position].equals("")) {

            String pathIcon;
            /*if (ContextApp.enableCustomization==true){
                //FIXME: Comentar cuando no se quiere customizar, para que funcione correctamente se debe de descomentar una parte en ABookFragmentActivity
                order.configureElementInLayout(holder.llytGenreItemMovie, position);
            }*/

            pathIcon = getPathImgChildrenMovies(position);
            File fileKids = new File(pathIcon);

            if (fileKids.exists()) {
                Uri mUri = Uri.parse(pathIcon);
                holder.imgvCoverCategoryItemMovie.setImageURI(mUri);
            } else
                holder.imgvCoverCategoryItemMovie.setImageResource(R.drawable.bkg_movie);

            /*pathIcon = videoPath + "/" + alistGnreMovies[position] + "/" + alistGnreMovies[position].split("_")[0] + ".png";
            File file = new File(pathIcon);


            if (file.exists()) {
                Uri mUri = Uri.parse(pathIcon);
                holder.imgvCoverCategoryItemMovie.setImageURI(mUri);
            } else
                holder.imgvCoverCategoryItemMovie.setImageResource(R.drawable.bkg_movie);*/


        }


    }

    @Override
    public int getItemCount() {
        return alistGnreMovies.length;
    }


    public class GenreMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout llytGenreItemMovie;
        public CardView cardvGenreItemMovie;
        public ImageView imgvCoverCategoryItemMovie;

        public GenreMovieViewHolder(View itemView) {
            super(itemView);
            llytGenreItemMovie = itemView.findViewById(R.id.llytGenreItemMovie);
            cardvGenreItemMovie = itemView.findViewById(R.id.cardvGenreItemMovie);
            imgvCoverCategoryItemMovie = itemView.findViewById(R.id.imgvCoverCategoryItemMovie);
            UtilitiesCardView.setTransparentBackgroundCardView(cardvGenreItemMovie,context);
            llytGenreItemMovie.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (movieGenreClickListener != null) {
                movieGenreClickListener.OnItemClick(view, getAdapterPosition());
            }
        }
    }

    /**
     * Metodo el cual tomara la implementacion de la interface "MovieGenreClickListener" (las acciones al ser presionado el boton) y la inicialiazara en esta clase
     *
     * @param movieGenreClickListener
     */
    public void setClickListener(MovieGenreClickListener movieGenreClickListener) {
        this.movieGenreClickListener = movieGenreClickListener;
    }


    /**
     * Interface que implementara la logica del evento cuando se de click en el boton
     * se le envia el elemento clickeado y su posicion
     */
    public interface MovieGenreClickListener {
        void OnItemClick(View view, int position);
    }

    private String getPathImgChildrenMovies(int position) {
        String pathIcon;
        if (!alistGnreMovies[position].contains(ConstantsApp.nameCategoryChildren)) {
            pathIcon = videoPath + "/" + alistGnreMovies[position] + "/" + alistGnreMovies[position] + ".png";
        } else {
            String children = ConfigMasterMGC.getConfigSingleton().getVIDEO_CHILDREN_PATH();
            pathIcon = children + "/" + alistGnreMovies[position] + ".png";
        }

        return pathIcon;
    }


}
