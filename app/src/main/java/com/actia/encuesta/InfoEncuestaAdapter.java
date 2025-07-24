package com.actia.encuesta;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utils_language.UtilsLanguage;
import com.hsalf.smilerating.SmileRating;

import java.util.List;

public class InfoEncuestaAdapter extends RecyclerView.Adapter<InfoEncuestaAdapter.ViewHolderItem> {
    private InfoEncuesta mInfoEncuesta;
    private Context mContext;
    private OnItemListener mOnItemListener;

    public InfoEncuestaAdapter(Context context, InfoEncuesta infoPreguntas, OnItemListener onItemListener) {
        this.mInfoEncuesta = infoPreguntas;
        this.mContext = context;
        this.mOnItemListener = onItemListener;

    }

    public class ViewHolderItem extends RecyclerView.ViewHolder implements SmileRating.OnRatingSelectedListener{
        TextView preguntaTextView;
        TextView textoRatingTextView;
        SmileRating respuestaRatingBar;
        EditText comentarioEditText;

        OnItemListener onItemListener;

        public ViewHolderItem(View itemView, OnItemListener onItemListener) {
            super(itemView);

            preguntaTextView = (TextView) itemView.findViewById(R.id.pregunta_text_view);
            textoRatingTextView = (TextView) itemView.findViewById(R.id.textoRating_text_view);
            respuestaRatingBar = (SmileRating) itemView.findViewById(R.id.respuesta_rating_bar);
            comentarioEditText = (EditText) itemView.findViewById(R.id.comentario_edit_text);
            this.onItemListener = onItemListener;

            respuestaRatingBar.setOnRatingSelectedListener(this);

        }

        public TextView getPreguntaTextView() {
            return preguntaTextView;
        }

        public TextView getTextoRatingTextView() {
            return textoRatingTextView;
        }

        public SmileRating getRespuestaRatingBar() {
            return respuestaRatingBar;
        }

        public EditText getComentarioEditText() {
            return comentarioEditText;
        }

        @Override
        public void onRatingSelected(int rating, boolean b) {
            Log.e("onRatingChanged", "positionAdapter: " + getAdapterPosition() + " rating: " + rating);
            onItemListener.onItemRatingChanged(getAdapterPosition(), rating);
            if ((int) rating > 0 && getAdapterPosition() < (getItemCount() - 1)) {
                EncuestaActivity.siguientePreguntaButton.setVisibility(View.VISIBLE);
            }

            getComentarioEditText().setFocusableInTouchMode(true);

        }

    }

    public interface OnItemListener {
        void onItemRatingChanged(int position, int stars);
    }

    @NonNull
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_encuesta, viewGroup, false);
        return new ViewHolderItem(view, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem viewHolderItem, int position) {

        getItemViewType(position);
        PreguntasEncuesta infoEncuesta = mInfoEncuesta.getPreguntas().get(position);


        String textoPregunta = infoEncuesta.getLabel();
        List<OpcionesEncuesta> opcionesEncuestas = infoEncuesta.getOptions();
        int cantidadOpciones = opcionesEncuestas.size();

        viewHolderItem.getPreguntaTextView().setText(getPreguntaIdioma(textoPregunta));

        for (int i = 0; i < 5; i++)
            viewHolderItem.getRespuestaRatingBar().setNameForSmile((i), getRespuestasIdioma(setTextoRating(position, i+1)));

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getItemCount() {
        return mInfoEncuesta.getPreguntas().size();
    }

    public String setTextoRating(int position, int stars) {
        String textoRating = "";
        try{
            if (stars > 0) {
                textoRating = mInfoEncuesta.getPreguntas().get(position).getOptions().get(stars - 1).getTextoOpcion().replace("||","");
                Log.e("TAG", "setTextoRating: " + textoRating);
            }
        }catch (Exception ex){

            Log.e("InfoEncuestAdapter", "setTextoRating: ", ex);
        }

        return textoRating;
    }

    public void setInfoEncuesta(InfoEncuesta infoEncuesta){

        mInfoEncuesta = infoEncuesta;
        notifyDataSetChanged();
    }

    private String getPreguntaIdioma(String pregunta){
        String textoPregunta = "";
        if(pregunta.contains("¬")){
            if(UtilsLanguage.isAppInEnglish()){
                textoPregunta = pregunta.split("¬")[1];
            } else {
                textoPregunta = pregunta.split("¬")[0];
            }
        } else {
            return pregunta;
        }
        return textoPregunta;
    }

    private String getRespuestasIdioma(String respuesta){
        String textoRespuesta = "";
        if(respuesta.contains("¬")){
            if(UtilsLanguage.isAppInEnglish()){
                textoRespuesta = respuesta.split("¬")[1];
            } else {
                textoRespuesta = respuesta.split("¬")[0];
            }
        } else {
            return respuesta;
        }
        return textoRespuesta;
    }

}
