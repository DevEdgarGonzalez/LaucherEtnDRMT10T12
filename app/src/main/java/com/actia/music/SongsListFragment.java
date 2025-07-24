package com.actia.music;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.ListFragment;

import com.actia.infraestructure.ConstantsApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_order_arrays.SongComparator;
import com.actia.utilities.utilities_ui.UtilsFonts;
import com.actia.utilities.utilities_ui.ColorsList;
import com.actia.utilities.utilities_external_storage.ReadFileExternalStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class SongsListFragment extends ListFragment {


    ReadFileExternalStorage FilesExternal = new ReadFileExternalStorage();
    public static final String ARG_ID_GENERO_SELECIONADO = "genero_id";
    private Callbacks mCallbacks = CallbacksVacios;
    String GlobalGenre = null;
    ReadFileExternalStorage fileExternal = null;
    ColorsList palette;
    private ArrayList<String> colores;
    int count = 0;
    public ListView ListSongsView = null;
    public ArrayList<Song> listSongs = null;

    /**
     * Interface definition for a callback to be invoked when a item of the song list is clicked.
     */
    protected interface Callbacks {
        void onEntradaSelecionada(String[] id);
    }

    private static final Callbacks CallbacksVacios = new Callbacks() {
        @Override
        public void onEntradaSelecionada(String[] id) {
        }
    };

    public SongsListFragment() {
    }


    /**
     * Is invoked when a song has finished and the next song will begin.
     *
     * @param genre the genre name
     * @param index the song index in the ListView
     */
    void setItemListCurrentSong(String genre, int index) {
        if (genre.equals(GlobalGenre))
            ListSongsView.setItemChecked(index, true);
        if (index<ListSongsView.getFirstVisiblePosition() || index>ListSongsView.getLastVisiblePosition()-1){
            ListSongsView.setSelection(index);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ID_GENERO_SELECIONADO)) {
            fileExternal = new ReadFileExternalStorage();
//            TextView nombre_genero = (TextView) getActivity().findViewById(R.id.txt);
            GlobalGenre = getArguments().getString(ARG_ID_GENERO_SELECIONADO);
//            nombre_genero.setText(getArguments().getString(ARG_ID_GENERO_SELECIONADO));
            palette = new ColorsList();
            colores = palette.getColorsGeneral();
            new loadSongs(getActivity()).execute(GlobalGenre);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ListSongsView = this.getListView();
        ListSongsView.setFastScrollAlwaysVisible(true);
        ListSongsView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
        ListSongsView.setFastScrollEnabled(true);
        ListSongsView.setDivider(null);
        ListSongsView.setDividerHeight(0);
        ListSongsView.setCacheColorHint(Color.parseColor("#000000"));
        ListSongsView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ListSongsView.setFocusableInTouchMode(false);

        ListSongsView.setBackground(getResources().getDrawable(R.drawable.state_item_focus_control_remote_list));
        ListSongsView.setFocusable(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Error: La actividad debe implementar el callback del fragmento");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = CallbacksVacios;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView listView, View select, int position, long id) {
        super.onListItemClick(listView, select, position, id);
        PlayMusicActivity.mServ.setCurrentPlayListService(null);
        PlayMusicActivity.mServ.setCurrentPlayListService(listSongs);
        if (GlobalGenre != null)
            mCallbacks.onEntradaSelecionada(new String[]{GlobalGenre, Integer.toString(position), "1"});
    }

    /**
     * Load all songs from external sdcard
     */
    private class loadSongs extends AsyncTask<String, Void, ArrayList<Song>> {

        private ProgressDialog dialog;
        Context context;

        /**
         * Load all songs from external sdcard
         *
         * @param context the context
         */
        loadSongs(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage(getContext().getString(R.string.load_wait_please));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Song> doInBackground(String... params) {
            ArrayList<Song> alistSongs = null;
            try {
                alistSongs = FilesExternal.getSongsByGenre(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (alistSongs!=null && ConstantsApp.SORT_BY_ALPHABETICAL_CATEGORIES){
                Collections.sort(alistSongs, new SongComparator());
            }
            return alistSongs;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(ArrayList<Song> result) {
            if (dialog.isShowing())
                dialog.dismiss();
            if (result != null && result.size() > 0) {
                listSongs = result;
                new updateListView(getActivity()).execute(result);
            }
        }
    }

    /**
     * Update the listview
     */
    private class updateListView extends AsyncTask<ArrayList<Song>, Void, SongListAdapter> {

        Context context;
        ProgressDialog pDialog;

        /**
         * Update the listview
         *
         * @param context the context
         */
        updateListView(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getString(R.string.update_list));
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @SafeVarargs
        @Override
        protected final SongListAdapter doInBackground(ArrayList<Song>... params) {
            SongListAdapter Adaptador;

            Adaptador = new SongListAdapter(getActivity(), R.layout.item_song_music_gnr, params[0]) {
                @Override
                public void onEntrada(Song canciones, View view) {
                    if (canciones != null) {
                        if (count > (colores.size() - 1))
                            count = 0;

                        TextView nombre = view.findViewById(R.id.textView_titulo_song_music);
                        TextView tiempo = view.findViewById(R.id.textView_tiempo_song_music);
                        TextView autor = view.findViewById(R.id.textView_autor_song_music);
                        if (nombre != null && tiempo != null && autor != null
                                && canciones.Name != null
                                && canciones.Time != null
                                && canciones.Author != null) {
                            nombre.setText(canciones.Name);
//                            nombre.setTypeface(UtilsFonts.getTypefacePlayMusic(getContext()));
                            tiempo.setText(canciones.Time);
//                            tiempo.setTypeface(UtilsFonts.getTypefacePlayMusic(getContext()));
                            autor.setText(canciones.Author);
//                            autor.setTypeface(UtilsFonts.getTypefacePlayMusic(getContext()));
                        }
                    }
                }
            };
            return Adaptador;
        }

        @Override
        protected void onPostExecute(SongListAdapter result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            setListAdapter(result);
            if (PlayMusicActivity.mServ.getNameGenre() != null) {
                if (PlayMusicActivity.mServ.getNameGenre().equals(GlobalGenre)) {
                    setItemListCurrentSong(PlayMusicActivity.mServ.getNameGenre(),
                            PlayMusicActivity.mServ.getcurrentIdSong());
                }
            } else {
                PlayMusicActivity.mServ.setNameGenre(GlobalGenre);
                if (GlobalGenre != null)
                    mCallbacks.onEntradaSelecionada(new String[]{GlobalGenre, Integer.toString(PlayMusicActivity.mServ.getcurrentIdSong()), "0"});
                setItemListCurrentSong(GlobalGenre,
                        PlayMusicActivity.mServ.getcurrentIdSong());
            }
        }
    }
}
