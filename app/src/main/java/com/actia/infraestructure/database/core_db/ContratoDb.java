package com.actia.infraestructure.database.core_db;

/**
 * Created by Edgar Gonzalez on 17/10/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class ContratoDb {

    public interface NAME_TABLES{
        String TokensMovies = "TokensMovies";

    }
    public interface ColumnsTokensMovies {
        String Id = "id";
        String TitleMovieDrm = "titleMovieDrm";
        String registrationStatus = "registrationStatus";
        String numberOfErrors = "numberOfErrors";
        String xmlLastDateUpdate = "xmlLastDateUpdate";
        String tokenLastUpdateAttemp = "tokenLastUpdateAttemp";
    }


}
