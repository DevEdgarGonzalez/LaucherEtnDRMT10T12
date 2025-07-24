package com.actia.infraestructure.database.core_db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Edgar Gonzalez on 17/10/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class OperationsDb {
    public static  DataBaseManager dataBaseManager;
    protected  static  OperationsDb instanceOpDb = new OperationsDb();

    public OperationsDb() {
    }
    protected static void startInstanceDb(Context context){
        if (dataBaseManager== null){
            dataBaseManager = new DataBaseManager(context);
        }

    }

    protected  static OperationsDb getInstanceOpDb(Context context){
        startInstanceDb(context);
        return instanceOpDb;
    }
    public SQLiteDatabase getDB() {
        return dataBaseManager.getWritableDatabase();
    }



    public Cursor readQueryDB(String query) {
        SQLiteDatabase db = dataBaseManager.getReadableDatabase();
        return db.rawQuery(query, null);
    }



}
