package com.actia.audiolibros;

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
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_ui.UtilitiesCardView;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 13/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class ABookRecyclerAdapter extends RecyclerView.Adapter<ABookRecyclerAdapter.ABookViewHolder>{

    private final ArrayList<AudioBook> listAudioBooks;
    private final Context context;
    private ABookClickListener abookClickListener;
    OrderElementsFourThree order;

    public ABookRecyclerAdapter(ArrayList<AudioBook> listAudioBooks, Context context) {
        this.listAudioBooks = listAudioBooks;
        this.context = context;
        order = new OrderElementsFourThree((Activity)context, listAudioBooks.size());
    }

    @Override
    public ABookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_abook,parent,false);
        return new ABookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ABookViewHolder holder, int position) {
        AudioBook audioBook =  listAudioBooks.get(position);
        ImageWorker m = new ImageWorker();
        m.loadBitmap(audioBook.getPathImage(), holder.imgvCoverItmBook,context,150,250);
        holder.lblTitleItmBook.setText(audioBook.getName()+"");


        //FIXME: Comentar cuando no se quiere customizar, para que funcione correctamente se debe de descomentar una parte en ABookFragmentActivity
        if (ContextApp.enableCustomization==true){
            order.configureElementInLayout(holder.llytItemAbook, position);
        }

    }

    @Override
    public int getItemCount() {
        return listAudioBooks.size();
    }


    public  class ABookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imgvCoverItmBook;
        public TextView lblTitleItmBook;
        public CardView cardvAbook;
        public LinearLayout llytItemAbook;

        public ABookViewHolder(View itemView) {
            super(itemView);

            imgvCoverItmBook = itemView.findViewById(R.id.imgvCoverItmBook);
            lblTitleItmBook = itemView.findViewById(R.id.lblTitleItmBook);
            cardvAbook = itemView.findViewById(R.id.cardvAbook);
            llytItemAbook = itemView.findViewById(R.id.llytItemAbook);

            llytItemAbook.setOnClickListener(this);
            imgvCoverItmBook.setOnClickListener(this);

            UtilitiesCardView.setTransparentBackgroundCardView(cardvAbook,context);
        }

        @Override
        public void onClick(View view) {
            if (abookClickListener!=null){
                abookClickListener.OnItemClick(view, getAdapterPosition() );
            }
        }
    }

    public void setClickListener(ABookClickListener aBookClickListener){
        this.abookClickListener =aBookClickListener;
    }

    public interface ABookClickListener{
        void OnItemClick(View view, int position);
    }




}
