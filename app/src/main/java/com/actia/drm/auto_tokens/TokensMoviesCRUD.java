package com.actia.drm.auto_tokens;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.database.core_db.ContratoDb;
import com.actia.infraestructure.database.core_db.OperationsDb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar Gonzalez on 17/10/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class TokensMoviesCRUD extends OperationsDb {

    private final String TAG = "CRUD TOKENS MOVIES";
    private static final TokensMoviesCRUD tokensMoviesCRUD = new TokensMoviesCRUD();

    public TokensMoviesCRUD() {
    }

    public static TokensMoviesCRUD getInstance(Context context){
        startInstanceDb(context);
        return tokensMoviesCRUD;
    }

    private Cursor getCursorOneTokensMovies(String titleMovieDrm) {
        SQLiteDatabase db = dataBaseManager.getReadableDatabase();
        String sql = String.format("Select *from %s where %s=?",
                ContratoDb.NAME_TABLES.TokensMovies,
                ContratoDb.ColumnsTokensMovies.TitleMovieDrm);
        String[] selectionArgs = {titleMovieDrm};

        return db.rawQuery(sql, selectionArgs);
    }



    private Cursor getCursorAllTokensMovies() {
        SQLiteDatabase db = dataBaseManager.getReadableDatabase();
        String sql = String.format("Select * from %s",
                ContratoDb.NAME_TABLES.TokensMovies);
        return db.rawQuery(sql, null);
    }

    private Cursor getCursorUpdateExist(String dateUpdate) {
        SQLiteDatabase db = dataBaseManager.getReadableDatabase();
        String sql = String.format("Select *from %s where %s=?",
                ContratoDb.NAME_TABLES.TokensMovies,
                ContratoDb.ColumnsTokensMovies.xmlLastDateUpdate);
        String[] selectionArgs = {dateUpdate};

        return db.rawQuery(sql, selectionArgs);
    }

    private Cursor getCursorByRegStatus(int status) {
        SQLiteDatabase db = dataBaseManager.getReadableDatabase();
        String sql = String.format("Select *from %s where %s=?",
                ContratoDb.NAME_TABLES.TokensMovies,
                ContratoDb.ColumnsTokensMovies.registrationStatus);
        String[] selectionArgs = {String.valueOf(status)};

        return db.rawQuery(sql, selectionArgs);
    }

    private Cursor getCursorByTWOStatus(int statusOne, int statusTwo) {
        SQLiteDatabase db = dataBaseManager.getReadableDatabase();
        String sql = String.format("Select *from %s where %s=? or  %s=?",
                ContratoDb.NAME_TABLES.TokensMovies,
                ContratoDb.ColumnsTokensMovies.registrationStatus,
                ContratoDb.ColumnsTokensMovies.registrationStatus);
        String[] selectionArgs = {String.valueOf(statusOne), String.valueOf(statusTwo)};

        return db.rawQuery(sql, selectionArgs);
    }


    private Cursor getCursorAllMoviesWithoutToken() {
        SQLiteDatabase db = dataBaseManager.getReadableDatabase();
        String sql = String.format("Select * from %s where %s=?",
                ContratoDb.NAME_TABLES.TokensMovies,
                ContratoDb.ColumnsTokensMovies.registrationStatus);
        String[] selectionArgs = {String.valueOf(ConstantsApp.OPC_TOKEN_NOT_REGISTERED)};
        return db.rawQuery(sql, selectionArgs);
    }

    /***public**/



    private ContentValues getValuesTokensMovies(TokenMovie tokensMovies) {
        ContentValues values = new ContentValues();
//        values.put(ContratoDb.ColumnsTokensMovies.Id , tokensMovies.getId());
        values.put(ContratoDb.ColumnsTokensMovies.TitleMovieDrm , tokensMovies.getTitleMovie());
        values.put(ContratoDb.ColumnsTokensMovies.registrationStatus, tokensMovies.getRegistrationStatus());
        values.put(ContratoDb.ColumnsTokensMovies.numberOfErrors, tokensMovies.getNumberOfErrors());
        values.put(ContratoDb.ColumnsTokensMovies.xmlLastDateUpdate, tokensMovies.getXmlLastDateUpdate());
        values.put(ContratoDb.ColumnsTokensMovies.tokenLastUpdateAttemp, tokensMovies.getTokenLastUpdateAttemp());
        return values;


    }

    private TokenMovie cursorToTokensMovies(Cursor cursor){
        TokenMovie tokensMovies =              new TokenMovie(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getString(5)
                );
        return tokensMovies;

    }

    private ArrayList<TokenMovie> cursorToarrayListTokensMovies (Cursor cursor){
        ArrayList<TokenMovie> listTokensMovies=  new ArrayList<>();
        if (cursor.moveToFirst()){
            do{
                listTokensMovies.add(cursorToTokensMovies(cursor));
            }while (cursor.moveToNext());
        }
        return listTokensMovies;
    }



    public TokenMovie readOneTokensMovies (String titleMovieDrm) {
        Cursor c = getCursorOneTokensMovies(titleMovieDrm);
        if (c.moveToFirst()){
            return cursorToTokensMovies(c);
        }
        return null;
    }

    public ArrayList<TokenMovie> readAllTokensMovies(){
        Cursor cursor = getCursorAllTokensMovies();
        return  cursorToarrayListTokensMovies(cursor);
    }
    public boolean deleteTokensMovies(String titleMovieDrm) {
        boolean isDeleteOk = false;
        SQLiteDatabase db = dataBaseManager.getWritableDatabase();

        String whereClause = String.format("%s=?",
                ContratoDb.ColumnsTokensMovies.TitleMovieDrm);

        String[] whereArgs = {titleMovieDrm};
        int result = db.delete(ContratoDb.NAME_TABLES.TokensMovies, whereClause, whereArgs);
        if (result > 0) {
            isDeleteOk = true;
        }
        return isDeleteOk;

    }

    public boolean createTokensMovies(TokenMovie tokensMovies) {
        boolean isCreateOk = false;
        SQLiteDatabase db = dataBaseManager.getWritableDatabase();
        ContentValues values = getValuesTokensMovies(tokensMovies);

        try {
            db.insertOrThrow(ContratoDb.NAME_TABLES.TokensMovies, null, values);
            isCreateOk = true;
            Log.i(TAG, "insert in Xxxxx: " + values.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "ERROR in Query: " + values.toString());
            Log.i(TAG, "Cause: " + e.toString());
        }
        return isCreateOk;

    }

    public boolean updateTokensMovies(TokenMovie tokenMovie) {
        boolean isUpdateOk = false;
        SQLiteDatabase db = dataBaseManager.getWritableDatabase();

        ContentValues values = getValuesTokensMovies(tokenMovie);

        String selection = String.format("%s=?",
                ContratoDb.ColumnsTokensMovies.TitleMovieDrm);

        String[] whereArgs = {String.valueOf(tokenMovie.getTitleMovie())};

        int result = db.update(ContratoDb.NAME_TABLES.TokensMovies, values, selection, whereArgs);

        if (result > 0) {
            isUpdateOk = true;
        }
        return isUpdateOk;

    }


    public ArrayList<TokenMovie> readAllMoviesWithoutTokens(){
        Cursor cursor = getCursorAllMoviesWithoutToken();
        cursor.getCount();
        return  cursorToarrayListTokensMovies(cursor);
    }

    /**
     * obtiene un arreglo de TokensMovies de todas las peliculas en la DB
     * @return
     */
    public ArrayList<TokenMovie> readALLMovies(){
        Cursor cursor = getCursorAllTokensMovies();
        cursor.getCount();
        return  cursorToarrayListTokensMovies(cursor);
    }

    /**
     * obtiene el arreglo<File> de todas las peliculas
     * @return
     */
    public ArrayList<File> getAllMoviesInArrayFile(){
        ArrayList<File> listFileMoviesWithoutTokens= new ArrayList<>();
        ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();
        Cursor cursor = getCursorAllTokensMovies();
        if (cursor.moveToFirst()){
            do {
//                File fileWithoutToken = new File(configMasterMGC.getPathTokens() + "/" + cursor.getString(1));
                File fileWithoutToken = new File(configMasterMGC.getPathTokens(),cursor.getString(1));
                    listFileMoviesWithoutTokens.add(fileWithoutToken);
            }while (cursor.moveToNext());
        }
        return listFileMoviesWithoutTokens;
    }


    public File[] getAllMoviesWithoutTokenInFileArray(){
        List<File> listFileMoviesWithoutTokens= new ArrayList<>();
        ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();
        Cursor cursor = getCursorAllMoviesWithoutToken();
        if (cursor.moveToFirst()){
            do {
                File fileWithoutToken = new File(configMasterMGC.getPathTokens() + "/" + cursor.getString(1));
                if (fileWithoutToken.exists()){
                    listFileMoviesWithoutTokens.add(fileWithoutToken);
                }
            }while (cursor.moveToNext());
        }
        return listFileMoviesWithoutTokens.toArray(new File[listFileMoviesWithoutTokens.size()]);
    }


    public boolean existRecordsInTable(){
        Cursor cursor = getCursorAllTokensMovies();
        return cursor.moveToFirst();
    }

    public boolean pendingUpdate (String dateUpdateXml){
        Cursor cursor = getCursorUpdateExist(dateUpdateXml);
            if (cursor.moveToFirst()){  //Si tiene registros esa fecha asi que no hay que ingresarlos
                return false;
            }
        int noReg = cursor.getCount();
            return true;            //no se encontraron registros con esta fecha asi que si hay que actualizar los tokens del archivo XML

    }

    public boolean existMovieWithTokenValidatedInDb(String name){
        Cursor cursor = getCursorOneTokensMovies(name);
        return cursor.moveToFirst();
    }


    public File[] readMoviesByStatus(int opcStatus){


        List<File> listFileMoviesWithoutTokens= new ArrayList<>();
        ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();
        Cursor cursor = getCursorByRegStatus(opcStatus);
        if (cursor.moveToFirst()){
            do {
                File fileWithoutToken = new File(configMasterMGC.getPathTokens() + "/" + cursor.getString(1));
                if (fileWithoutToken.exists()){
                    listFileMoviesWithoutTokens.add(fileWithoutToken);
                }
            }while (cursor.moveToNext());
        }
        return listFileMoviesWithoutTokens.toArray(new File[listFileMoviesWithoutTokens.size()]);
    }

    public File[] readMoviesByTwoStatusInfoTokens(int opcStatusOne, int opcStatusTwo){


        List<File> listFileMoviesWithoutTokens= new ArrayList<>();
        ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();
        Cursor cursor = getCursorByTWOStatus(opcStatusOne,opcStatusTwo);
        if (cursor.moveToFirst()){
            do {
                File fileWithoutToken = new File(configMasterMGC.getPathTokens() + "/" + cursor.getString(1));
                if (fileWithoutToken.exists()){
                    listFileMoviesWithoutTokens.add(fileWithoutToken);
                }
            }while (cursor.moveToNext());
        }
        return listFileMoviesWithoutTokens.toArray(new File[listFileMoviesWithoutTokens.size()]);
    }

    public int getNumberOfRecordsByStatus(int opcStatus){
        int numberOfRecords = 0;
        Cursor cursor = getCursorByRegStatus(opcStatus);
        if (cursor.moveToFirst()){
            do {
                numberOfRecords++;
            }while (cursor.moveToNext());
        }
        return numberOfRecords;
    }
    public int getNumberAllRecords(){
        Cursor cursor = getCursorAllTokensMovies();
        return  cursor.getCount();
    }

    public int getStatusOfaMovie(String titleMovie){
        int status = ConstantsApp.OPC_TOKEN_UNKNOWN;
        Cursor cursor = getCursorOneTokensMovies(titleMovie);
        if (cursor.moveToFirst()){
            status = cursor.getInt(2);                      ////int registrationStatus;
        }
        return status;
    }
    public String getLastUpdateTokenWasabi(String titleMovie){
        String latsUpdateTokenToken = "";
        Cursor cursor = getCursorOneTokensMovies(titleMovie);
        if (cursor.moveToFirst()){
            latsUpdateTokenToken = cursor.getString(5);         //String tokenLastUpdateAttemp
        }
        return latsUpdateTokenToken;
    }
}
