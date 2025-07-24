package com.actia.drm.auto_tokens;

import android.content.Context;

import com.actia.infraestructure.ConstantsApp;
import com.actia.mexico.launcher_t12_generico_2018.R;


/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class ParseEstatusTokenMovie {
    public static String getStatusStringFromIdStatus(int idStatus, Context context) {
        String nameStatus = "";

        switch (idStatus) {
            case ConstantsApp.OPC_TOKEN_OK:
                nameStatus = context.getString(R.string.validate_token);
                break;
            case ConstantsApp.OPC_TOKEN_NOT_REGISTERED:
                nameStatus = context.getString(R.string.process_token);
                break;
            case ConstantsApp.OPC_TOKEN_WITH_ERROR:
                nameStatus = context.getString(R.string.error_token);
                break;
            default:
                nameStatus = context.getString(R.string.uknow_token);
                break;
        }

        return nameStatus;
    }
}
