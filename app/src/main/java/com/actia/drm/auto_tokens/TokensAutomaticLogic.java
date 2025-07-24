package com.actia.drm.auto_tokens;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.shared_preferences.PreferencesApp;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.utilities_date.UtilsDate;
import com.actia.utilities.utilities_file.TokenFilter;
import com.actia.utilities.utilities_internet.UtilitiesNetwork;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class TokensAutomaticLogic {
    private final Context context;
    private final Handler handlerPrincipal = new Handler();
    private int seconds = ConstantsApp.SECONDS_DELAY_Validation_tokens;
    private final TokensMoviesCRUD tokensMoviesCRUD;
    private UserRegisterTsk tskTokens;
    private final ConfigMasterMGC config;
    private Runnable runnableValidateTokens;
    private final int tipeActivity;


    /**
     * Constructor
     */
    public TokensAutomaticLogic(Context context, int tipeActivity) {
        this.context = context;
        tokensMoviesCRUD = TokensMoviesCRUD.getInstance(context);
        config = ConfigMasterMGC.getConfigSingleton();
        this.tipeActivity = tipeActivity;
        if (tipeActivity == ConstantsApp.SCREEN_INFO_TOKENS) {
            ConstantsApp.isRunningTskTokens = false;
        }
    }


    /**
     * Configura handler y lo ejecuta
     */
    public void startProccesValidateTokens() {

        seconds = seconds * 1000;
        initializeRunneable();
        handlerPrincipal.postDelayed(runnableValidateTokens, 2000);

    }

    /**
     * Proceso en el cual contiene toda la logica de la administracion de los tokens
     */
    private void initializeRunneable() {
        runnableValidateTokens = new Runnable() {

            @Override
            public void run() {                                                             //Inicia handler
                if (ConstantsApp.isRunningTskTokens == false) {                            //Verifica si hay un task ejecutandose (task validacion usuario o validacion de tokens))

                    boolean isConnected = UtilitiesNetwork.isOnline();                              //verifica si coneccion valida a internet
                    if (isConnected == true) {

                        detectNewTokens();
                        try {
                            if (!isValidKey()) {                                                    //Termina el Handler si la llave no es valida (solo valida en  shared preferences)
                                ConstantsApp.isRunningTskTokens = true;
                                handlerPrincipal.removeCallbacks(runnableValidateTokens);
                                return;
                            }
                            sendTokens();                                   //Envia los tokens que se encuentren en la DB con que esten pendientes de validar

                        } catch (Exception e) {
                            e.printStackTrace();//Se añade catch para inahabilitar handler cuando se cambie de actividad evitando asi que muera al momento de intentar pintar los dialogos de los asynktask contenidos
                            handlerPrincipal.removeCallbacksAndMessages(null);
                        }

                    } else {
                        if (tipeActivity == ConstantsApp.SCREEN_INFO_TOKENS) {
                            Toast.makeText(context, context.getString(R.string.no_conection), Toast.LENGTH_SHORT).show();
                        }
                        Log.i("Service TokensAut", "No se encuentra conectado a internet");
                    }
                }

                if (tipeActivity == ConstantsApp.SCREEN_ADVERSITING) {             //si es pantalla de publicidad circulante inicia el lanzamiento de cada lapso de tiempo
                    handlerPrincipal.postDelayed(this, seconds);
                }
            }
        };

    }

    public void detectNewTokens() {
        if (existNewTokensFromDownloadWIFI()) {         //Se validan si hay tokens a nuevos por archivo  "dbxmultimedia"...
            saveDataNewTokensInDb();                        //busca los tokens registrados en el archivo XML en la carpeta DRM y los alamcena en DB
        }

        if (existNewTokensLoadedManuallyInSd()) {       //Compara el numero de archivos XML en carpeta DRM contra el numero de registros en la tabla TOKENS
            saveInDbNewTokensLoadedManuallyInSd();              //Almacena en DB local los datos de los archivos xml que no se encuentren en la tabla TOKENS
        }
        deleteTokensDbWhitoutDrm(tokensMoviesCRUD);

    }


    /**
     * Guarda en DB los tokens que se encuentren en carpeta DRM y que no esten en la tabla tokens
     */
    private void saveInDbNewTokensLoadedManuallyInSd() {
        File dirToken = new File(config.getPathTokens());
        File[] tokensInSd = dirToken.listFiles(new TokenFilter());         //getAllTokensFromSdCardDirectory
        for (File unFile : tokensInSd) {
            if (!tokensMoviesCRUD.existMovieWithTokenValidatedInDb(unFile.getName())) {

                tokensMoviesCRUD.createTokensMovies(
                        new TokenMovie(
                                0,                                                  //int id;
                                unFile.getName(),                                   //String titleMovie;
                                ConstantsApp.OPC_TOKEN_NOT_REGISTERED,             //int registrationStatus;
                                0,                                                  //int numberOfErrors;
                                context.getString(R.string.manually_loaded),                                          //String xmlLastDateUpdate;
                                UtilsDate.getDatecomplete()                         //String tokenLastUpdateAttemp
                        )
                );

            } else {
                Log.i("TokensLogic", "No hay tokens cargados manualmente");
            }

        }

    }


    /**
     * Compara si la cantidad de tokens en la carpeta DRM es diferente que en la Db
     *
     * @return true si faltan tokens por guardar en la db
     */
    private boolean existNewTokensLoadedManuallyInSd() {


        File dirToken = new File(config.getPathTokens());
        File[] tokensInSD = dirToken.listFiles(new TokenFilter());         //getAllTokensFromSdCardDirectory
        int tokensInDb = tokensMoviesCRUD.getNumberAllRecords();
        return tokensInSD.length != tokensInDb;
    }


    /**
     * inicia validacion de los tokens que esten almacenados en la DB local y que cuenten con estatus "no registrados"
     */
    private void sendTokens() {

        ConfigMasterMGC config = ConfigMasterMGC.getConfigSingleton();
        File[] listTokensToSend;

        if (tipeActivity == ConstantsApp.SCREEN_INFO_TOKENS) {
            listTokensToSend = tokensMoviesCRUD.readMoviesByTwoStatusInfoTokens(ConstantsApp.OPC_TOKEN_NOT_REGISTERED, ConstantsApp.OPC_TOKEN_WITH_ERROR);
        } else {
            listTokensToSend = tokensMoviesCRUD.readMoviesByStatus(ConstantsApp.OPC_TOKEN_NOT_REGISTERED);
        }


        if (listTokensToSend != null && listTokensToSend.length > 0) {

            new UserRegisterTsk(context, config.getPathUserDRM(), 1, listTokensToSend, tipeActivity).execute();

        } else {

            Log.i("ValidateTokens:  ", "no se obtuvieron tokens a registrar");

        }
    }


    /**
     * obtiene el nombre de los tokens y la fecha  que indica el archivo dbxmultimedia
     */
    private void saveDataNewTokensInDb() {
        File[] tokensNew = getArrayFileNewTokensFromDBXMultimedia();
        String dateUpdate = ParserXmlTokensNewMovies.getDateLastUpdate();
        saveFileToTokenTitleInDb(tokensNew, dateUpdate);

    }


    /**
     * crea  en caso de no existir tokens en db o actualiza en caso de si existir en DB los tokens que indique el archivo dbcmultimedia (tokens descargados por wifi)
     */
    private void saveFileToTokenTitleInDb(File[] tokens, String dateUpdate) {
        for (File unFile : tokens) {
            if (unFile != null && !unFile.getName().equals("")) {

                TokenMovie tokeAct = new TokenMovie(
                        0,                                                  //int id;
                        unFile.getName(),                                   //String titleMovie;
                        ConstantsApp.OPC_TOKEN_NOT_REGISTERED,             //int registrationStatus;
                        0,                                                  //int numberOfErrors;
                        dateUpdate,                                          //String xmlLastDateUpdate;
                        UtilsDate.getDatecomplete()                         //String tokenLastUpdateAttemp
                );


                if (tokensMoviesCRUD.existMovieWithTokenValidatedInDb(unFile.getName()) == false) {
                    tokensMoviesCRUD.createTokensMovies(tokeAct);
                } else {
                    if (tokensMoviesCRUD.getStatusOfaMovie(unFile.getName()) == ConstantsApp.OPC_TOKEN_OK) {
                        tokeAct.setRegistrationStatus(ConstantsApp.OPC_TOKEN_OK);                                          //dejara el estatus ok porque ya se valido anteriormente
                        tokeAct.setTokenLastUpdateAttemp(tokensMoviesCRUD.getLastUpdateTokenWasabi(unFile.getName()));      //toma del registro de la bd de ese token la fecha en que se valido ese token
                    }
                    tokensMoviesCRUD.updateTokensMovies(tokeAct);
                }
            }
        }
    }


    /**
     * obtiene del directorio el texto del xml "//mnt/extsdcard/config/dbxmlmultimedia.xml" para despues tomar su token del //mnt/extsdcard/drm
     *
     * @return NULL or ArrayFile:  arreglo que contiene los xml a enviar a validacion de las nuevas peliculas
     */
    private File[] getArrayFileNewTokensFromDBXMultimedia() {
        String xmlTextElementsDownload = StorageContentNewTokens.getXmlInStringNewTokens();
        File[] arrayFilesTokens = ParserXmlTokensNewMovies.getArrayFileToXml(xmlTextElementsDownload, context);
        return arrayFilesTokens;
    }


    /**
     * verifica si hay nuevos tokens descargados por wifi
     * Esto se valida con la fecha que indica el archivo dbxmultimedia
     *
     * @return true en caso de NO existir tokens en la DB con la fecha que indica el archivo dbxmultimedia
     */
    private boolean existNewTokensFromDownloadWIFI() {
        boolean existPendingUpdate = false;
        String dateLastUpdate = ParserXmlTokensNewMovies.getDateLastUpdate();

        if (dateLastUpdate != null) {
            existPendingUpdate = tokensMoviesCRUD.pendingUpdate(dateLastUpdate);
        }

        return existPendingUpdate;
    }


    /**
     * Quita el proceso activo
     */
    public void cancelTskTokens


    () {
        handlerPrincipal.removeCallbacks(runnableValidateTokens);
        if (tskTokens != null) {
            if (tskTokens.getStatus() == AsyncTask.Status.RUNNING) {
                tskTokens.cancel(true);
            }
        }
    }


    /**
     * Consulta la ultima fecha de modificacion del archivo
     *
     * @return la fecha de ultima modificacion en formato yyyy-MM-dd hh:mm:ss
     */
    public String getDateKeyInSD() {
        ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();
        File f = new File(configMasterMGC.getPathUserDRM());

        long last = f.lastModified();
        return UtilsDate.getStringDateFromTimestamp(last);
    }


    /**
     * Valida si anteriormente se valido la llave y si aun no llega al limite de errores para continuar el proceso
     * este metodo solo consulta los datos en las preferencias compartidas
     *
     * @return true en caso de que la llave no se ha registrado aun o si aun no llega al limite permitido
     * false si ya cumplio con los limites permitidos
     */
    public boolean isValidKey() {
        PreferencesApp keyInShared = new PreferencesApp(context);
        boolean seRealizaProceso = false;
        String dateKeyInSD = getDateKeyInSD();
        String dateKeyShared = keyInShared.getModificationDateXml();

        if (dateKeyInSD.equals("") || !dateKeyShared.equals(dateKeyInSD)) {
            keyInShared.setModificationDateXml(dateKeyInSD);
            keyInShared.setValidationErors(0);
        }


        if (keyInShared.getValidationErors() <= ConstantsApp.MAX_ERRORS_KEY_WASABI - 1) {
            seRealizaProceso = true;
        }

        return seRealizaProceso;
    }


//TODO Statics methods

    /**
     * Este metodo valida si hay registros en la DB, en caso de no existir añade los que estan en la carpeta DRM
     * y borra los tokens que existen en la Db pero que no tengan archivo en la carpeta DRM
     *
     * @param context para la ubicacion de la DB
     */
    public static void initializeDatabase(Context context) {
        TokensMoviesCRUD tokMovCRUD = TokensMoviesCRUD.getInstance(context);
        if (tokMovCRUD.existRecordsInTable() == false) {                             //Verifica si hay registros en tabla Tokens,
            saveDataTokensInitialsInDb(tokMovCRUD);                                                       //si no hay inserta todos los XML de la carpeta DRM en la tabla TOKENS
        }

        deleteTokensDbWhitoutDrm(tokMovCRUD);

    }


    /**
     * Alamacena todos los tokens que se encuentren en la carpeta DRM
     * este metodo se lanza cuando no hay ningun registro en la DB
     *
     * @param tokMovCRUD
     */
    private static void saveDataTokensInitialsInDb(TokensMoviesCRUD tokMovCRUD) {

        ConfigMasterMGC config = ConfigMasterMGC.getConfigSingleton();

        File dirToken = new File(config.getPathTokens());
        File[] tokensInitials = dirToken.listFiles(new TokenFilter());         //getAllTokensFromSdCardDirectory


        saveFileToTokenTitleInDbInitial(tokensInitials, ConstantsApp.DATE_FROM_SD, tokMovCRUD);
    }


    /**
     * Alamacena todos los tokens que se encuentren en la carpeta DRM
     * este metodo se lanza cuando no hay ningun registro en la DB
     */
    private static void saveFileToTokenTitleInDbInitial(File[] tokens, String dateUpdate, TokensMoviesCRUD tokMovCRUD) {
        for (File unFile : tokens) {
            if (unFile != null && !unFile.getName().equals("")) {

                TokenMovie tokeAct = new TokenMovie(
                        0,                                                  //int id;
                        unFile.getName(),                                   //String titleMovie;
                        ConstantsApp.OPC_TOKEN_NOT_REGISTERED,             //int registrationStatus;
                        0,                                                  //int numberOfErrors;
                        dateUpdate,                                          //String xmlLastDateUpdate;
                        "N/A"                         //String tokenLastUpdateAttemp
                );


                if (!tokMovCRUD.existMovieWithTokenValidatedInDb(unFile.getName())) {
                    tokMovCRUD.createTokensMovies(tokeAct);
                } else {
                    tokMovCRUD.updateTokensMovies(tokeAct);
                }

            }

        }
    }


    /**
     * Borra Los registros en DB que no cuenten con Xml en carpeta DRM
     *
     * @param tokMovCRUD
     */
    public static void deleteTokensDbWhitoutDrm(TokensMoviesCRUD tokMovCRUD) {
        ArrayList<File> alistMovies = tokMovCRUD.getAllMoviesInArrayFile();
        for (File unFile : alistMovies) {
            if (unFile.exists() == false) {
                tokMovCRUD.deleteTokensMovies(unFile.getName());
            }
        }
    }


}
