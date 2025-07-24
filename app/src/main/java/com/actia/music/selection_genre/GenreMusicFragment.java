package com.actia.music.selection_genre;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.music.PlayMusicActivity;

import java.util.ArrayList;

public class GenreMusicFragment extends Fragment implements GenreMusicTsk.OnCompleteLoadGenre {
    View root;
//	View currentSelectedTextView=null;

    private CallbacksGeneros generosCallbacks = CallbacksGenerosVacios;
    GridView grid_generos;
    //	boolean init=false;
//	private ArrayList<Integer> numbersRamdom;
    ArrayList<String> allmThumbIds=null;
    String auxGenero;
    private ArrayList<Integer> numbersRamdom;

    public GenreMusicFragment() {
        // Required empty public constructor
    }

    /**
     * Interface definition for a callback to be invoked when a genre is clicked.
     */
    public interface CallbacksGeneros {
        /**
         * Override method called when the interface is invoked.
         * @param id The genre name
         */
        void onGeneroSelecionada(String id);
    }

    private static final CallbacksGeneros CallbacksGenerosVacios = new CallbacksGeneros() {
        @Override
        public void onGeneroSelecionada(String id) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        if (arguments != null && getArguments().containsKey("GEN")) {
            auxGenero = getArguments().getString("GEN");
        }

    }


    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_genre, null);
        grid_generos = root.findViewById(R.id.gridview_musicc);

        allmThumbIds = new ArrayList<>();

//        if(PlayMusicActivity.Genre.length<16){
//            for(int z=0;z<15;z++){
//                if(z< PlayMusicActivity.Genre.length){
//                    allmThumbIds.add(PlayMusicActivity.Genre[z]);
//                }
//                else allmThumbIds.add("");
//            }
//        }else Collections.addAll(allmThumbIds, PlayMusicActivity.Genre);
//
//        numbersRamdom = new ArrayList<>();
//        for(int i = 0; i < allmThumbIds.size(); i++)
//            numbersRamdom.add(i);
//        Collections.shuffle(numbersRamdom);

        new GenreMusicTsk(this, root.getContext(),allmThumbIds).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        grid_generos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {

                String aux = (String) parent.getItemAtPosition(v.getId());
                if(!aux.equals("")){
                    generosCallbacks.onGeneroSelecionada(aux);
                }
//				if(!GenreMusicAdapter.allmThumbIds.get(v.getId()).equals("")){
//					 generosCallbacks.onGeneroSelecionada(GenreMusicAdapter.allmThumbIds.get(v.getId()));
//				}

            }
        });
        return root;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof CallbacksGeneros)) {
            throw new IllegalStateException("Error: La actividad debe implementar el callback del fragmento");
        }
        generosCallbacks = (CallbacksGeneros) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        generosCallbacks = CallbacksGenerosVacios;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    @Override
    public void OnCompleteLoadGenreListener(GenreMusicAdapter adapter) {
        grid_generos.setAdapter(adapter);
        if(PlayMusicActivity.mServ.getNameGenre()!=null && (auxGenero!=null && auxGenero.isEmpty())){
            generosCallbacks.onGeneroSelecionada(PlayMusicActivity.mServ.getNameGenre());

            int f = adapter.getCount();

            for (int i = 0; i< f; i++) {
//				int item = numbersRamdom.get(i);
//				String allthumbs = (String) adapter.getItem(item);
                String allthumbs = (String) adapter.getItem(i);
                if (allthumbs.equals(PlayMusicActivity.mServ.getNameGenre())) {
                    grid_generos.setItemChecked(i,true);
                    return;
                }
            }
        }else{
//	        generosCallbacks.onGeneroSelecionada(PlayMusicActivity.Genre[0]);
//			for (int i = 0; i < numbersRamdom.size(); i++) {
////				   if (numbersRamdom.get(i)==0) {
////					   grid_generos.setItemChecked(i,true);
////				    return;
////				  }
//		    }

            int f = adapter.getCount();
            String allthumbs;
            int mPos = 0;

            for (int i = 0; i< f; i++) {
                allthumbs = (String) adapter.getItem(i);
                if(allthumbs.equals(auxGenero)){
                    mPos = i;
                    break;
                }
            }

            generosCallbacks.onGeneroSelecionada(auxGenero);
            grid_generos.setItemChecked(mPos,true);

        }
    }
}
