package com.actia.abook_ninos;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actia.audiolibros.AudioBook;
import com.actia.infraestructure.ContextApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_ui.UtilitiesCardView;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 24/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class AbookNinosRecyclerAdapter  extends RecyclerView.Adapter<AbookNinosRecyclerAdapter.AbookNinosViewHolder>{
    ArrayList<AudioBook> alistAbooks;
    Context context;
    ClickListenerAbookChild clickListenerAbookChild;
    OrderElementsFourThree order;


    public AbookNinosRecyclerAdapter(ArrayList<AudioBook> alistAbooks, Context context) {
        this.alistAbooks = alistAbooks;
        this.context = context;
        order = new OrderElementsFourThree((Activity)context, alistAbooks.size());
    }

    @Override
    public AbookNinosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_abook_children,parent,false);

        return new AbookNinosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AbookNinosViewHolder holder, int position) {
        AudioBook audioBook =  alistAbooks.get(position);
        ImageWorker m = new ImageWorker();
        m.loadBitmap(audioBook.getPathImage(), holder.imgvCoverItmBook,context,150,250);
        holder.lblTitleItmBook.setVisibility(View.VISIBLE);
        holder.lblTitleItmBook.setText(audioBook.getName()+"");

        //FIXME: Comentar cuando no se quiere customizar, para que funcione correctamente se debe de descomentar una parte en ABookFragmentActivity
        if (ContextApp.enableCustomization==true){
            order.configureElementInLayout(holder.llytItemAbookChildren, position);
        }


    }

    @Override
    public int getItemCount() {
        return alistAbooks.size();
    }

    public class AbookNinosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imgvCoverItmBook;
        TextView lblTitleItmBook;
        CardView cardvAbookChild;
        LinearLayout llytItemAbookChildren;


        public AbookNinosViewHolder(View itemView) {
            super(itemView);
            imgvCoverItmBook = itemView.findViewById(R.id.imgvCoverItmBookChild);
            lblTitleItmBook = itemView.findViewById(R.id.lblTitleItmBookChild);
            cardvAbookChild = itemView.findViewById(R.id.cardvAbookChild);
            llytItemAbookChildren = itemView.findViewById(R.id.llytItemAbookChildren);
            llytItemAbookChildren.setOnClickListener(this);
            imgvCoverItmBook.setOnClickListener(this);
            UtilitiesCardView.setTransparentBackgroundCardView(cardvAbookChild,context);




        }

        @Override
        public void onClick(View view) {
            if (clickListenerAbookChild !=null){
                clickListenerAbookChild.setOnClickListener(view, getAdapterPosition());
            }
        }
    }

    public void setClickListenerAbookChild(ClickListenerAbookChild clickListenerAbookChild){
        this.clickListenerAbookChild = clickListenerAbookChild;
    }

    public interface ClickListenerAbookChild {
        void setOnClickListener(View view, int position);
    }
}
