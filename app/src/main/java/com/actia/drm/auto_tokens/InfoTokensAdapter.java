package com.actia.drm.auto_tokens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actia.infraestructure.ConstantsApp;
import com.actia.mexico.launcher_t12_generico_2018.R;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class InfoTokensAdapter extends BaseAdapter {
    Context context;
    ArrayList<TokenMovie> listTokensMovies;


    public InfoTokensAdapter(Context context, ArrayList<TokenMovie> listTokensMovies) {
        this.context = context;
        this.listTokensMovies = listTokensMovies;
    }

    @Override
    public int getCount() {
        return listTokensMovies.size();
    }

    @Override
    public Object getItem(int position) {
        return listTokensMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        TokenMovie tokensMoviesCell = (TokenMovie) getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_info_token, null);


            viewHolder.lytBackCellTok = convertView.findViewById(R.id.lytBackCellTok);
            viewHolder.lblNameMovieCellTok = convertView.findViewById(R.id.lblNameMovieCellTok);
            viewHolder.lblStatusMovieCellTok = convertView.findViewById(R.id.lblStatusMovieCellTok);
            viewHolder.lblNumberErrorsCellTok = convertView.findViewById(R.id.lblNumberErrorsCellTok);
            viewHolder.lblLastDateUpdateCellTok = convertView.findViewById(R.id.lblLastDateUpdateCellTok);
            viewHolder.lblDateDownloadXmlCellTok = convertView.findViewById(R.id.lblDateDownloadXmlCellTok);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String lastUpdateDownloadXMlToken = tokensMoviesCell.getXmlLastDateUpdate();
//        if (lastUpdateDownloadXMlToken.equals("000")|| lastUpdateDownloadXMlToken==null){
//            lastUpdateDownloadXMlToken= "Sin descarga";
//        }

        viewHolder.lblNameMovieCellTok.setText((position+1)+"   "+ tokensMoviesCell.getTitleMovie());
        viewHolder.lblStatusMovieCellTok.setText(ParseEstatusTokenMovie.getStatusStringFromIdStatus(tokensMoviesCell.getRegistrationStatus(), context));
        viewHolder.lblNumberErrorsCellTok.setText(context.getString(R.string.number_errors_token)+"    "+tokensMoviesCell.getNumberOfErrors() + "");
        viewHolder.lblDateDownloadXmlCellTok.setText(context.getString(R.string.last_update_token)+"    "+lastUpdateDownloadXMlToken);
        viewHolder.lblLastDateUpdateCellTok.setText(" Ultima actualizacion "+ tokensMoviesCell.getTokenLastUpdateAttemp());

        if (tokensMoviesCell.getRegistrationStatus()== ConstantsApp.OPC_TOKEN_WITH_ERROR){
//        if (position==1){
            viewHolder.lytBackCellTok.setBackgroundColor(context.getResources().getColor(R.color.red_transp80));
        }else if (tokensMoviesCell.getRegistrationStatus()== ConstantsApp.OPC_TOKEN_OK){
//        }else if (position ==2){
            viewHolder.lytBackCellTok.setBackgroundColor(context.getResources().getColor(R.color.green_transp80));
        }else if (tokensMoviesCell.getRegistrationStatus()== ConstantsApp.OPC_TOKEN_NOT_REGISTERED){
//        }else if (position==3){
            viewHolder.lytBackCellTok.setBackgroundColor(context.getResources().getColor(R.color.yellow_transp80));
        }


        return convertView;
    }

    public static class ViewHolder {
        public TextView lblNameMovieCellTok;
        public TextView lblStatusMovieCellTok;
        public TextView lblNumberErrorsCellTok;
        public TextView lblLastDateUpdateCellTok;
        public TextView lblDateDownloadXmlCellTok;
        public LinearLayout lytBackCellTok;
    }

}
