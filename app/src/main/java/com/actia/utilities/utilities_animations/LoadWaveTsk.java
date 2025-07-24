package com.actia.utilities.utilities_animations;

/**
 * load asynchronously the Home
 * Call OnLoadHome interface when Load is finish.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.actia.infraestructure.ItemsHome;
import com.actia.mexico.launcher_t12_generico_2018.R;

import java.util.HashMap;

public class LoadWaveTsk extends AsyncTask<Void, Void, WaveMultipleObjectsAdapter> {

    private ProgressDialog dialog;
    private final Context context;
    private final OnLoadWave interf;
    private final HashMap<Integer, ItemsHome> categoriesApp;

    /**
     * Interface definition for a callback to be invoked when the home are loaded.
     */
    public interface OnLoadWave {
        void OnLoadWaveListener(WaveMultipleObjectsAdapter adapter);
    }


    public LoadWaveTsk(Context context, OnLoadWave interf, HashMap<Integer, ItemsHome> categoriesApp) {
        this.context = context;
        this.interf = interf;
        this.categoriesApp = categoriesApp;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.title_load_home));
        dialog.show();
    }

    @Override
    protected WaveMultipleObjectsAdapter doInBackground(Void... params) {
        return new WaveMultipleObjectsAdapter(context, categoriesApp);
    }

    @Override
    protected void onPostExecute(WaveMultipleObjectsAdapter adapter) {
        interf.OnLoadWaveListener(adapter);
        if (dialog.isShowing())
            dialog.dismiss();
    }
}
