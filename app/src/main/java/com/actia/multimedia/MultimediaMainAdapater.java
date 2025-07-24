package com.actia.multimedia;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_ui.UtilitiesCardView;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.io.File;

/**
 * Created by Edgar Gonzalez on 20/02/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class MultimediaMainAdapater extends RecyclerView.Adapter<MultimediaMainAdapater.MultimediaViewHolder> {
    Context context;
    File[] dirsMultimedia;
    ClickListenerItemMultimedia clickListenerItemMultimedia;
    OrderElementsFourThree order;

    public MultimediaMainAdapater(Context context, File[] dirsNosotros) {
        this.context = context;
        this.dirsMultimedia = dirsMultimedia;
        order = new OrderElementsFourThree((Activity)context, dirsMultimedia.length);
    }

    @Override
    public MultimediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_category_multimedia,parent, false);

        return new MultimediaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MultimediaViewHolder holder, int position) {
        File currentItemMultimedia = dirsMultimedia[position];
        if (currentItemMultimedia!=null && !currentItemMultimedia.equals("")){
            ImageWorker imageWorker= new ImageWorker();
            holder.txtvNameMultimediaCategory.setText(currentItemMultimedia.getName());
            imageWorker.loadBitmap(currentItemMultimedia.getPath(), holder.imgvCoverMultimediaCategory,context,165,200);

            String pathIcon = currentItemMultimedia+"/"+currentItemMultimedia.getName() + ".png";

            File file = new File(pathIcon);

            //FIXME: Comentar cuando no se quiere customizar, para que funcione correctamente se debe de descomentar una parte en ABookFragmentActivity
            if (ConfigMasterMGC.enableCustomization == true){
                order.configureElementInLayout(holder.llytItemMultimedia, position);
            }

            if (file.exists()){
                Uri mUri = Uri.parse(pathIcon);
                holder.imgvCoverMultimediaCategory.setImageURI(mUri);
            }else{
                holder.imgvCoverMultimediaCategory.setImageResource(R.drawable.bkg_movie);
            }


        }
    }

    @Override
    public int getItemCount() {
        return dirsMultimedia.length;
    }

    public class MultimediaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardvMultimediaCategory;
        ImageView imgvCoverMultimediaCategory;
        TextView txtvNameMultimediaCategory;
        LinearLayout llytItemMultimedia;


        public MultimediaViewHolder(View itemView) {
            super(itemView);
            cardvMultimediaCategory= (CardView) itemView.findViewById(R.id.cardvMultimediaCategory);
            imgvCoverMultimediaCategory = (ImageView) itemView.findViewById(R.id.imgvCoverMultimediaCategory);
            txtvNameMultimediaCategory = (TextView) itemView.findViewById(R.id.txtvNameMultimediaCategory);
            llytItemMultimedia = (LinearLayout) itemView.findViewById(R.id.llytItemMultimedia);

            UtilitiesCardView.setTransparentBackgroundCardView(cardvMultimediaCategory, context);
            llytItemMultimedia.setOnClickListener(this);



        }

        @Override
        public void onClick(View view) {
            if (clickListenerItemMultimedia!=null){
                clickListenerItemMultimedia.onItemClickMultimedia(view, getAdapterPosition());
            }
        }
    }

    public void setOnClickListenerMultimedia(ClickListenerItemMultimedia clickListenerMultimedia){
        this.clickListenerItemMultimedia = clickListenerMultimedia;
    }

    public interface ClickListenerItemMultimedia{
        void onItemClickMultimedia(View view, int position);

    }
}
