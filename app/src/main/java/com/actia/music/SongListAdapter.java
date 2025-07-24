package com.actia.music;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 *An adapter t show the songs in a ListView
 */
public abstract class SongListAdapter extends BaseAdapter {
	
    private final ArrayList<Song> canciones;
    private final int R_layout_IdView;
    private final Context contexto;
//    int count=0;
      
    protected SongListAdapter(Context contexto, int R_layout_IdView, ArrayList<Song> canciones) {
        super();
        this.contexto = contexto;
        this.canciones = canciones; 
        this.R_layout_IdView = R_layout_IdView;    
    }
      
    @Override
    public View getView(int posicion, View view, ViewGroup pariente) {
		LayoutInflater inflater = (LayoutInflater) contexto
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View item;
        if (view == null) {
//        	item=new View(contexto);
        	item = inflater.inflate(R_layout_IdView, null);
        	//if(posicion<colores.size())
        	//item.setBackgroundColor(Color.parseColor(colores.get(posicion)));
        }
        else 
        	item= view;
       
        onEntrada (canciones.get(posicion), item);
        return item; 
    }

	@Override
	public int getCount() {
		return canciones.size();
	}

	@Override
	public Object getItem(int posicion) {
		return canciones.get(posicion);
	}

	@Override
	public long getItemId(int posicion) {
		return posicion;
	}
	
	/** Devuelve cada una de las entradas con cada una de las vistas a la que debe de ser asociada
	 * @param entrada La entrada que sera la asociada a la view. La entrada es del tipo del paquete/handler
	 * @param view View particular que contendor los datos del paquete/handler
	 */
	public abstract void onEntrada (Song entrada, View view);
    
}
