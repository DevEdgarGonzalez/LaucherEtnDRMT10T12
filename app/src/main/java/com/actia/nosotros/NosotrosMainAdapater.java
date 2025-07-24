package com.actia.nosotros;

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
import com.actia.utilities.utilities_ui.ImageWorker;
import com.actia.utilities.utilities_ui.UtilitiesCardView;
import com.actia.utilities.utilities_ui.utilitiesOrderItems.OrderElementsFourThree;

import java.io.File;

/**
 * Created by Edgar Gonzalez on 20/02/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class NosotrosMainAdapater  extends RecyclerView.Adapter<NosotrosMainAdapater.NosotrosViewHolder> {
    Context context;
    File[] dirsNosotros;
    ClickListenerItemNosotros clickListenerItemNosotros;
    OrderElementsFourThree order;

    public NosotrosMainAdapater(Context context, File[] dirsNosotros) {
        this.context = context;
        this.dirsNosotros = dirsNosotros;
        order = new OrderElementsFourThree((Activity)context, dirsNosotros.length);
    }

    @Override
    public NosotrosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_category_nosotros,parent, false);

        return new NosotrosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NosotrosViewHolder holder, int position) {
        File currentItemNosotros = dirsNosotros[position];
        if (currentItemNosotros!=null && !currentItemNosotros.equals("")){
            ImageWorker imageWorker= new ImageWorker();
            holder.txtvNameNosotrosCategory.setText(currentItemNosotros.getName());
            imageWorker.loadBitmap(currentItemNosotros.getPath(), holder.imgvCoverNosotrosCategory,context,165,200);

            String pathIcon = currentItemNosotros+"/"+currentItemNosotros.getName() + ".png";

            File file = new File(pathIcon);

            //FIXME: Comentar cuando no se quiere customizar, para que funcione correctamente se debe de descomentar una parte en ABookFragmentActivity
            if (ContextApp.enableCustomization==true){
                order.configureElementInLayout(holder.llytItemNosotros, position);
            }

            if (file.exists()){
                Uri mUri = Uri.parse(pathIcon);
                holder.imgvCoverNosotrosCategory.setImageURI(mUri);
            }else{
                holder.imgvCoverNosotrosCategory.setImageResource(R.drawable.bkg_movie);
            }


        }
    }

    @Override
    public int getItemCount() {
        return dirsNosotros.length;
    }

    public class NosotrosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardvNosotrosCategory;
        ImageView imgvCoverNosotrosCategory;
        TextView txtvNameNosotrosCategory;
        LinearLayout llytItemNosotros;


        public NosotrosViewHolder(View itemView) {
            super(itemView);
            cardvNosotrosCategory= itemView.findViewById(R.id.cardvNosotrosCategory);
            imgvCoverNosotrosCategory = itemView.findViewById(R.id.imgvCoverNosotrosCategory);
            txtvNameNosotrosCategory = itemView.findViewById(R.id.txtvNameNosotrosCategory);
            llytItemNosotros = itemView.findViewById(R.id.llytItemNosotros);

            UtilitiesCardView.setTransparentBackgroundCardView(cardvNosotrosCategory, context);
            llytItemNosotros.setOnClickListener(this);



        }

        @Override
        public void onClick(View view) {
            if (clickListenerItemNosotros!=null){
                clickListenerItemNosotros.onItemClickNosotros(view, getAdapterPosition());
            }
        }
    }

    public void setOnClickListenerNosotros(ClickListenerItemNosotros clickListenerNosotros){
        this.clickListenerItemNosotros = clickListenerNosotros;
    }

    public interface ClickListenerItemNosotros{
        void onItemClickNosotros(View view, int position);

    }
}
