package com.actia.conciertos;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.peliculas.Movie;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_ui.UtilitiesCardView;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 10/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class ConcertRecyclerAdapter extends RecyclerView.Adapter<ConcertRecyclerAdapter.ConcertViewHolder> {
    private final Context context;
    ArrayList<Movie> alistConcert = null;
    ClickListenerConcert clickListenerConcert;

    public ConcertRecyclerAdapter(Context context, ArrayList<Movie> alistConcert) {
        this.context = context;
        this.alistConcert = alistConcert;
    }

    @Override
    public ConcertViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_concert, parent, false);
        return new ConcertViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConcertViewHolder holder, int position) {
        ImageWorker imageWorker = new ImageWorker();
        Movie movieActual = alistConcert.get(position);

        holder.txtvNameConcert.setText(movieActual.name);

        String pathImage = movieActual.image;
        if (pathImage != null) {
            imageWorker.loadBitmap(pathImage, holder.imgvCoverConcert, context, 165, 200);
        } else {
            holder.imgvCoverConcert.setImageResource(R.drawable.no_movie);
        }

    }

    @Override
    public int getItemCount() {
        return alistConcert.size();
    }

    public class ConcertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardvConcert;
        ImageView imgvCoverConcert;
        TextView txtvNameConcert;

        public ConcertViewHolder(View itemView) {
            super(itemView);
            cardvConcert = itemView.findViewById(R.id.cardvConcert);
            imgvCoverConcert = itemView.findViewById(R.id.imgvCoverConcert);
            txtvNameConcert = itemView.findViewById(R.id.txtvNameConcert);

            UtilitiesCardView.setTransparentBackgroundCardView(cardvConcert, context);
            imgvCoverConcert.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (clickListenerConcert!=null){
                clickListenerConcert.OnItemClickConcert(view, getAdapterPosition());
            }
        }
    }

    public void setOnClickListenerConcert(ClickListenerConcert clickListenerConcert){
        this.clickListenerConcert = clickListenerConcert;
    }

    public interface  ClickListenerConcert{
        void OnItemClickConcert(View view, int position);
    }



}
