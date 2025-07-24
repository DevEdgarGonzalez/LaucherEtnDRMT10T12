package com.actia.infraestructure.database.core_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Edgar Gonzalez on 17/10/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class DataBaseManager extends SQLiteOpenHelper {
    Context context;
    private static final String DB_NAME = "telemetryADO";
    private static final int DB_VERSION = 1;
    private final String TAG = "CreateDatabase";


    protected DataBaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("Create table %s (%s integer primary key autoincrement, %s varchar (100), %s integer, %s integer, %s varchar (20), %s varchar (20) )",
                ContratoDb.NAME_TABLES.TokensMovies,
                ContratoDb.ColumnsTokensMovies.Id,                                      //int id
                ContratoDb.ColumnsTokensMovies.TitleMovieDrm,                           //String titleMovie
                ContratoDb.ColumnsTokensMovies.registrationStatus,                      //int registrationStatus
                ContratoDb.ColumnsTokensMovies.numberOfErrors,                          //int numberOfErrors
                ContratoDb.ColumnsTokensMovies.xmlLastDateUpdate,                               //String xmlLastDateUpdate
                ContratoDb.ColumnsTokensMovies.tokenLastUpdateAttemp                               //String xmlLastDateUpdate
        ));


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
