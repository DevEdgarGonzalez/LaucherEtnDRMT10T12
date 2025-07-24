package com.actia.games;

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

import com.actia.infraestructure.ContextApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.infraestructure.ItemsHome;
import com.actia.utilities.utilities_ui.UtilitiesCardView;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 22/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class GamesRecyclerAdapter extends RecyclerView.Adapter<GamesRecyclerAdapter.GamesViewHolder> {

    private final ArrayList<ItemsHome> alistGames;
    private final Context context;
    private ClickListenerGames clickListenerGames;
    private final OrderElementsFourThree order;

    public GamesRecyclerAdapter(ArrayList<ItemsHome> alistGames, Context context) {
        this.alistGames = alistGames;
        this.context = context;
        order = new OrderElementsFourThree((Activity)context, alistGames.size());
    }

    @Override
    public GamesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GamesViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(GamesViewHolder holder, int position) {
        if (alistGames.get(position) != null && !alistGames.get(position).equals("")) {
            if (alistGames.get(position).getPathImg()!=null){
                Uri uriImage = Uri.parse(alistGames.get(position).getPathImg());
                holder.imgvCoverGame.setImageURI(uriImage);

            }

            holder.txtvNameGame.setText(alistGames.get(position).getTitle());

            //FIXME: Comentar cuando no se quiere customizar, para que funcione correctamente se debe de descomentar una parte en ABookFragmentActivity
            if (ContextApp.enableCustomization==true){
                order.configureElementInLayout(holder.llytIteGame, position);
            }
        }

    }

    @Override
    public int getItemCount() {
        return alistGames.size();
    }

    public class GamesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imgvCoverGame;
        TextView txtvNameGame;
        CardView cardvGame;
        LinearLayout llytIteGame;

        public GamesViewHolder(View itemView) {
            super(itemView);
            cardvGame = itemView.findViewById(R.id.cardvGame);
            imgvCoverGame = itemView.findViewById(R.id.imgvCoverGame);
            txtvNameGame = itemView.findViewById(R.id.txtvNameGame);
            llytIteGame = itemView.findViewById(R.id.llytIteGame);

            UtilitiesCardView.setTransparentBackgroundCardView(cardvGame, context);
            llytIteGame.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListenerGames!=null){
                clickListenerGames.onItemClickGames(view,getAdapterPosition());
            }
        }
    }

    public void setOnClickListenerGame(ClickListenerGames clickListenerGame){
        this.clickListenerGames = clickListenerGame;
    }

    public interface ClickListenerGames{
        void onItemClickGames(View view, int position);
    }



}
