package com.actia.encuesta;

import static com.actia.utilities.utilities_root.UtilsRoot.getSerialNumber;
import static com.actia.utilities.utilities_ui.HideSystemNavBar.hideNavigationBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.actia.help_movie.PlayAdvertisingpActivity;
import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.shared_preferences.PreferencesApp;
import com.actia.mapas.SingletonConfig;
import com.actia.mensajeria.UDP_Broadcast;
import com.actia.mexico.generic_2018_t10_t12.LoadSDCardActivity;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_dialog_brightness.DialogBrightnessFragment;
import com.actia.utilities.utilities_internet.AsynckCheckInternetConn;
import com.actia.utilities.utils_language.UtilsLanguage;
import com.hsalf.smilerating.SmileRating;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class EncuestaActivity extends BaseActivity implements InfoEncuestaAdapter.OnItemListener, LoaderManager.LoaderCallbacks<InfoEncuesta> {

    private static final String ENCUESTA_REQUEST_URL_SERVER = SingletonConfig.getConfigSingleton().getIP_SERVER_ACTIES();
    private static final String ENCUESTA_REQUEST_URL_XML = SingletonConfig.getConfigSingleton().getENCUESTA_XML_URL();
    private static final String ENCUESTA_REQUEST_URL_REPOSITORY = SingletonConfig.getConfigSingleton().getENCUESTA_UPLOAD_URL();

    private static final String TAG = EncuestaActivity.class.getSimpleName();
    private static final int ENCUESTA_LOADER_ID = 1;
    private static final int ENVIO_LOADER_ID = 2;

    private ConfigMasterMGC configMasterMGC = new ConfigMasterMGC();

    private String sourceFile = configMasterMGC.getAbsoluteInternalPath() + "/encuestas/";


    private InfoEncuestaAdapter mAdapter;
    private RecyclerView encuestaRecyclerView;
    private RelativeLayout bienvenidaRelativeLayout;
    private RelativeLayout encuestaRelativeLayout;
    private RelativeLayout graciasRelativeLayout;
    private TextView emptyTextView;
    private TextView mensajeBienvenidaTextView;
    private TextView textoGraciasTextView;
    public static Button siguientePreguntaButton;
    public static Button anteriorPreguntaButton;
    public static Button enviarEncuestaButton;
    public static Button iniciaEncuestaButton;
    public static Button noGraciasButton;
    public static Button cerrarEncuesta;
    private ProgressBar spinnerProgressBar;
    private LinearLayoutManager layoutManager;
    private int position = 0;
    private int stars;
    private InfoEncuesta mInfoEncuesta = new InfoEncuesta("", 0, "", 0, new ArrayList<PreguntasEncuesta>());
    private ArrayList<InfoRespuestas> respuestas = new ArrayList<InfoRespuestas>();
    private int shortAnimationDuration;

    private View decorView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta);


        bienvenidaRelativeLayout = (RelativeLayout) findViewById(R.id.bienvenida_relative_layout);
        encuestaRelativeLayout = (RelativeLayout) findViewById(R.id.encuesta_relative_layout);
        graciasRelativeLayout = (RelativeLayout) findViewById(R.id.gracias_relative_layout);
        encuestaRecyclerView = (RecyclerView) findViewById(R.id.list);
        emptyTextView = (TextView) findViewById(R.id.empty_text_view);
        mensajeBienvenidaTextView = (TextView) findViewById(R.id.mensajeBienvenida_text_view);
        textoGraciasTextView = (TextView) findViewById(R.id.textoGracias_text_view);
        spinnerProgressBar = (ProgressBar) findViewById(R.id.spinner_progress_bar);
        siguientePreguntaButton = (Button) findViewById(R.id.siguientePregunta_button);
        anteriorPreguntaButton = (Button) findViewById(R.id.anteriorPregunta_button);
        enviarEncuestaButton = (Button) findViewById(R.id.enviarEncuesta_button);
        iniciaEncuestaButton = (Button) findViewById(R.id.iniciaEncuesta_button);
        noGraciasButton = (Button) findViewById(R.id.noGracias_button);
        cerrarEncuesta = (Button) findViewById(R.id.cerrarEncuesta_button);

        siguientePreguntaButton.setVisibility(View.GONE);
        siguientePreguntaButton.setOnClickListener(clickListenerSiguiente);

        enviarEncuestaButton.setVisibility(View.GONE);
        enviarEncuestaButton.setOnClickListener(clickListenerEnviarEncuesta);

        anteriorPreguntaButton.setVisibility(View.GONE);
        anteriorPreguntaButton.setOnClickListener(clickListenerAnterior);

        iniciaEncuestaButton.setOnClickListener(clickListenerIniciarEncuesta);
        noGraciasButton.setOnClickListener(clickListenerNoGracias);
        cerrarEncuesta.setOnClickListener(clickListenerCerrarEncuesta);

        encuestaRecyclerView.getViewTreeObserver().addOnScrollChangedListener(scrollChangeListener);

        setupUI(findViewById(R.id.parent));

        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        ImageView homeImageView = (ImageView) findViewById(R.id.home_image_view);

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAdapter = new InfoEncuestaAdapter(this, new InfoEncuesta("", 0, "", 0, new ArrayList<PreguntasEncuesta>()), this);
        /*ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();*/

        // Get a reference to the LoaderManager, in order to interact with loaders.

        emptyTextView.setVisibility(View.GONE);
        LoaderManager loaderManager = getSupportLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).

        AsynckCheckInternetConn stp100connection = new AsynckCheckInternetConn(this);
        try {
            MainActivity.isStp100 = stp100connection.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        loaderManager.initLoader(ENCUESTA_LOADER_ID, null, this);


        hideNavigationBar(this);

        decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    hideNavigationBar(EncuestaActivity.this);
                }
            }
        });

    }

    public void onItemRatingChanged(int position, int stars) {
        Log.e("TAG", "onItemRatingChanged: Escuchando posicion " + position + " estrellas " + stars);
        String comentario = "";

        try {

            //se utiliza para llamar al ultimo elemento de la lista y refrescar la información que se obtiene de el para los resultados
            if (this.position >= (mAdapter.getItemCount() - 1)) {
                if (stars > 0) {
                    if (this.position >= respuestas.size() || this.position < 0) {

                        EditText editTextfromposition = (EditText) encuestaRecyclerView.findViewHolderForAdapterPosition(mAdapter.getItemCount() - 1).itemView.findViewById(R.id.comentario_edit_text);

                        if (editTextfromposition != null)
                            comentario = editTextfromposition.getText().toString();

                        this.stars = stars;
                        //index does not exists
                        respuestas.add(this.position, new InfoRespuestas(mInfoEncuesta.getPreguntas().get(this.position).getName()
                                , mInfoEncuesta.getPreguntas().get(this.position).getOptions().get(stars - 1).getOpcion_id(),
                                comentario, stars));

                    } else {
                        // index exists

                        EditText editTextfromposition = (EditText) encuestaRecyclerView.findViewHolderForAdapterPosition(mAdapter.getItemCount() - 1).itemView.findViewById(R.id.comentario_edit_text);

                        if (editTextfromposition != null)
                            comentario = editTextfromposition.getText().toString();

                        this.stars = stars;
                        if (respuestas.contains(respuestas.get(this.position))) {
                            respuestas.remove(this.position);
                            respuestas.add(this.position, new InfoRespuestas(mInfoEncuesta.getPreguntas().get(this.position).getName()
                                    , mInfoEncuesta.getPreguntas().get(this.position).getOptions().get(stars - 1).getOpcion_id(),
                                    comentario, stars));
                        }
                    }
                    enviarEncuestaButton.setVisibility(View.VISIBLE);
                }
            } else {
                if (stars > 0) {
                    if (this.position >= respuestas.size() || this.position < 0) {

                        EditText editTextfromposition = (EditText) encuestaRecyclerView.findViewHolderForAdapterPosition(mAdapter.getItemCount() - 1).itemView.findViewById(R.id.comentario_edit_text);

                        if (editTextfromposition != null)
                            comentario = editTextfromposition.getText().toString();

                        //index does not exists
                        respuestas.add(this.position, new InfoRespuestas(mInfoEncuesta.getPreguntas().get(this.position).getName()
                                , mInfoEncuesta.getPreguntas().get(this.position).getOptions().get(stars - 1).getOpcion_id(),
                                comentario, stars));
                    } else {
                        // index exists
                        if (respuestas.contains(respuestas.get(this.position))) {
                            respuestas.remove(this.position);
                            respuestas.add(this.position, new InfoRespuestas(mInfoEncuesta.getPreguntas().get(this.position).getName()
                                    , mInfoEncuesta.getPreguntas().get(this.position).getOptions().get(stars - 1).getOpcion_id(),
                                    comentario, stars));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "onItemRatingChanged: ", ex);
        }

    }

    private View.OnClickListener clickListenerSiguiente = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            position++;
            Log.e("TAG", "onClick: " + position + " stars: " + stars);
            if (position < (mAdapter.getItemCount())) {
                encuestaRecyclerView.scrollToPosition(position);
                anteriorPreguntaButton.setVisibility(View.VISIBLE);
            }
        }
    };

    private View.OnClickListener clickListenerAnterior = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("TAG", "onClick: " + position + " " + (mAdapter.getItemCount() - 1));
            if (position > 0) {
                position--;
                Log.e("TAG", "onClick: " + (position));
                encuestaRecyclerView.scrollToPosition(position);
                if (position == 0)
                    anteriorPreguntaButton.setVisibility(View.GONE);
            }
        }
    };

    private View.OnClickListener clickListenerEnviarEncuesta = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("TAG", "onClick: " + position + " " + (mAdapter.getItemCount() - 1));

            onItemRatingChanged(position, stars);
            createXmlSurveyReplay();

            LoaderManager loaderManager = getSupportLoaderManager();
            AsynckCheckInternetConn stp100connection = new AsynckCheckInternetConn(EncuestaActivity.this);
            try {
                if (stp100connection.execute().get())
                    loaderManager.initLoader(ENVIO_LOADER_ID, null, loaderCallbacksEnvio);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            encuestaRelativeLayout.setVisibility(View.GONE);
            crossfade(encuestaRecyclerView, graciasRelativeLayout);
        }
    };

    private View.OnClickListener clickListenerCerrarEncuesta = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private View.OnClickListener clickListenerIniciarEncuesta = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("TAG", "onClick: Iniciando Encuesta");
            crossfade(bienvenidaRelativeLayout, encuestaRelativeLayout);

        }
    };

    private View.OnClickListener clickListenerNoGracias = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("TAG", "onClick: " + position + " " + (mAdapter.getItemCount() - 1));
            createXmlSurveyReplay();

            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(ENVIO_LOADER_ID, null, loaderCallbacksEnvio);

            onBackPressed();
        }
    };

    private ViewTreeObserver.OnScrollChangedListener scrollChangeListener = new ViewTreeObserver.OnScrollChangedListener() {

        @Override
        public void onScrollChanged() {
            try{

            int scrollY = layoutManager.findFirstVisibleItemPosition();
            Log.e("TAG", "onScrollChanged: " + scrollY);
            position = scrollY;
            String comentario = "";

            SmileRating ratingBarfromposition = encuestaRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.respuesta_rating_bar);
            EditText editTextfromposition = encuestaRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.comentario_edit_text);

//            if (respuestas.size() > position) {
//                ratingBarfromposition.setSelectedSmile(respuestas.get(position).getRating());
//            }

            if (editTextfromposition != null)
                comentario = editTextfromposition.getText().toString();

            int rating = ratingBarfromposition.getRating();

            if (rating > 0) {

                siguientePreguntaButton.setVisibility(View.VISIBLE);
                if (position >= respuestas.size() || position < 0) {
                    //index does not exists
                    respuestas.add(position, new InfoRespuestas(mInfoEncuesta.getPreguntas().get(position).getName()
                            , mInfoEncuesta.getPreguntas().get(position).getOptions().get(rating - 1).getOpcion_id(),
                            comentario, rating));
                } else {
                    // index exists
                    if (respuestas.contains(respuestas.get(position))) {
                        respuestas.remove(position);

                        respuestas.add(position, new InfoRespuestas(mInfoEncuesta.getPreguntas().get(position).getName()
                                , mInfoEncuesta.getPreguntas().get(position).getOptions().get(rating - 1).getOpcion_id(),
                                comentario, rating));
                    }
                }


            } else {

                siguientePreguntaButton.setVisibility(View.GONE);

                if (position >= respuestas.size() || position < 0) {
                    //index does not exists
                    respuestas.add(position, new InfoRespuestas(mInfoEncuesta.getPreguntas().get(position).getName()
                            , "",
                            comentario, rating));
                } else {
                    // index exists
                    if (respuestas.contains(respuestas.get(position))) {
                        respuestas.remove(position);

                        respuestas.add(position, new InfoRespuestas(mInfoEncuesta.getPreguntas().get(position).getName()
                                , "",
                                comentario, rating));
                    }
                }
            }

            if (position >= (mAdapter.getItemCount() - 1)) {
                siguientePreguntaButton.setVisibility(View.GONE);
            }

            if (position >= (mAdapter.getItemCount() - 1) && rating > 0){
                enviarEncuestaButton.setVisibility(View.VISIBLE);
            } else {
                enviarEncuestaButton.setVisibility(View.GONE);
            }

            if (position > 0)
                anteriorPreguntaButton.setVisibility(View.VISIBLE);
            else
                anteriorPreguntaButton.setVisibility(View.GONE);

            }catch (Exception ex){
            }
        }
    };

    private void createXmlSurveyReplay() {


        String xmlEncabezado = "<?xml version='1.0' encoding='utf-8' ?>\n" +
                "<survey device_id='' encuesta_client='encuesta_cliente_' encuesta_id='encuesta_id_' datetime='0' os='' version=''>\n" +
                "<form id='id_' name='encuesta_cliente_'>\n";
        String xmlBody = "";
        String xmlPie = "</form>\n" +
                "</survey>";
        String respuestas = "  <field name='name_' select='select_' comment='comment_' />\n";

        xmlEncabezado = xmlEncabezado.replaceAll("encuesta_cliente_", mInfoEncuesta.getEncuesta_client())
                .replaceAll("encuesta_id_", String.valueOf(mInfoEncuesta.getEncuesta_id()))
                .replaceAll("id_", String.valueOf(mInfoEncuesta.getForm_id()));

        if (this.respuestas.size() > 0) {

            for (int i = 0; i < this.respuestas.size(); i++) {

                xmlBody += respuestas.replace("name_", this.respuestas.get(i).getName())
                        .replace("select_", this.respuestas.get(i).getSelect())
                        .replace("comment_", this.respuestas.get(i).getComment());

            }
        } else {
            for (int i = 0; i < mAdapter.getItemCount(); i++) {

                xmlBody += respuestas.replace("name_", "")
                        .replace("select_", "")
                        .replace("comment_", "");

            }
        }

        sourceFile += writeToFile(xmlEncabezado + xmlBody + xmlPie);
    }

    private String writeToFile(String data) {

        String fileName = getSerialNumber() + "_timestamp";
        long contador = 0;

        File dir = new File(configMasterMGC.getAbsoluteInternalPath(), "encuestas");
        if (!dir.exists()) {
            dir.mkdir();
        }

        contador = dir.listFiles().length;

        try {
            File gpxfile = new File(dir, fileName + "_" + (contador + 1) + ".xml");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName + "_" + (contador + 1) + ".xml";
    }

    private void crossfade(final View from, View to) {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        to.setAlpha(0f);
        to.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        to.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        from.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        from.setVisibility(View.GONE);
                    }
                });
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(EncuestaActivity.this);
                    return false;
                }
            });
        } else {
            view.setOnKeyListener(hideKeyboardDonePress);
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isAcceptingText() && activity.getCurrentFocus().getWindowToken() != null) {
                inputMethodManager.hideSoftInputFromWindow(
                        activity.getCurrentFocus().getWindowToken(),
                        0
                );

            }


        } catch (Exception ex) {
            Log.e(TAG, "hideSoftKeyboard: ", ex);
        }
    }


    private String getBienvenidaIdioma(String mensaje) {

        String msgIdioma = "";
        if (mensaje.contains("¬")) {
            if (UtilsLanguage.isAppInEnglish()) {
                msgIdioma = mensaje.split("¬¬")[0];
                msgIdioma = msgIdioma.split("¬")[1];
            } else {
                msgIdioma = mensaje.split("¬¬")[0];
                msgIdioma = msgIdioma.split("¬")[0];
            }
        } else {
            return mensaje;
        }
        return msgIdioma;
    }

    private String getGraciasIdioma(String mensaje) {

        String msgIdioma = "";
        if (mensaje.contains("¬")) {
            if (UtilsLanguage.isAppInEnglish()) {
                msgIdioma = mensaje.split("¬¬")[1];
                msgIdioma = msgIdioma.split("¬")[1];
            } else {
                msgIdioma = mensaje.split("¬¬")[1];
                msgIdioma = msgIdioma.split("¬")[0];
            }
        } else {
            return getApplicationContext().getResources().getString(R.string.thanks_msg);
        }
        return msgIdioma;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            MainActivity.udpBroadcast.setListener(null, null);
            MainActivity.udpBroadcast.setListener(UDP_Broadcast.mUDP_BroadcastListener, this);
        } catch(Exception ex){
            Log.e(TAG, "onResume: ", ex);
        }

        hideNavigationBar(EncuestaActivity.this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(EncuestaActivity.this);
    }

    private View.OnKeyListener hideKeyboardDonePress =new View.OnKeyListener()
    {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
            if (event.getKeyCode() == KeyEvent.FLAG_EDITOR_ACTION)
            {
                hideSoftKeyboard(EncuestaActivity.this);
            }
            return false;
        }
    };

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        //finish();
    }*/

    @NonNull
    public Loader<InfoEncuesta> onCreateLoader(int i, @Nullable Bundle bundle) {
        try {
            return new EncuestaLoader(EncuestaActivity.this, ENCUESTA_REQUEST_URL_SERVER + ENCUESTA_REQUEST_URL_XML);
        } catch (Exception Ex) {
            Log.e(TAG, "onCreateLoader: ", Ex);
            return null;
        }
    }

    public void onLoadFinished(@NonNull Loader<InfoEncuesta> loader, InfoEncuesta infoEncuesta) {

        spinnerProgressBar.setVisibility(View.GONE);

        if (infoEncuesta != null && !infoEncuesta.getPreguntas().isEmpty()) {
            mAdapter.setInfoEncuesta(infoEncuesta);
            mInfoEncuesta = infoEncuesta;
        } else {
            onBackPressed();
        }

        if (mAdapter.getItemCount() < 1) {
            bienvenidaRelativeLayout.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            mensajeBienvenidaTextView.setText(getBienvenidaIdioma(mInfoEncuesta.getEncuesta_msg()));
            textoGraciasTextView.setText(getGraciasIdioma(mInfoEncuesta.getEncuesta_msg()));
            bienvenidaRelativeLayout.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }

        layoutManager = new LinearLayoutManager(EncuestaActivity.this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        encuestaRecyclerView.setAdapter(mAdapter);
        encuestaRecyclerView.setLayoutManager(layoutManager);
    }

    public void onLoaderReset(@NonNull Loader<InfoEncuesta> loader) {
        mAdapter.setInfoEncuesta(new InfoEncuesta("", 0, "", 0, new ArrayList<PreguntasEncuesta>()));
    }

    private LoaderManager.LoaderCallbacks<Integer> loaderCallbacksEnvio = new LoaderManager.LoaderCallbacks<Integer>() {

        @NonNull
        public Loader<Integer> onCreateLoader(int i, @Nullable Bundle bundle) {
            return new EnvioEncuestaLoader(EncuestaActivity.this, ENCUESTA_REQUEST_URL_SERVER + ENCUESTA_REQUEST_URL_REPOSITORY, sourceFile, false);
        }

        public void onLoadFinished(@NonNull Loader<Integer> loader, Integer result) {

            //Mostrar pantalla de gracias haya enviado o no la encuesta
            if (result == 200) {
                //si la envio hay que eliminarla de la memoria del dispositivo
                File deletedFile = new File(sourceFile);
                deletedFile.delete();

            }
            //crossfade(encuestaRecyclerView, graciasRelativeLayout);
            //encuestaRelativeLayout.setVisibility(View.GONE);
            //si no la envio solo salir de la pantalla

        }

        public void onLoaderReset(@NonNull Loader<Integer> loader) {
        }
    };

}