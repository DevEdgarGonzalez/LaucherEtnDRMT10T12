package com.actia.utilities;

import static com.actia.utilities.utilities_file.FileUtils.moveEstrenos;

import com.actia.drm.auto_tokens.InfoTokensActivity;
import com.actia.mapas.Map_Activity;
import com.actia.mexico.generic_2018_t10_t12.ActiaMaintenanceActivity;
import com.actia.home_categories.MainActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Catch all messages from service actia
 */
public class ReceiverServiceActia extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
        if (intent.getAction().equals("com.actia.mgc")) {
            Bundle messg = intent.getExtras();
            if (messg != null) {
                String c = messg.getString("command");
                if (c != null)
                    switch (c) {
                        case "_shutdown": {
                            Intent intentone = new Intent(context.getApplicationContext(), MainActivity.ShutDownActivity.class);
                            intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentone);
                            break;
                        }
                        case "_openMenuMaintance": {
                            Intent intentone = new Intent(context.getApplicationContext(), ActiaMaintenanceActivity.class);
                            intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentone);
                            break;
                        }
                        case "_goHome": {
                            Intent intentone = new Intent(context.getApplicationContext(), MainActivity.class);
                            intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentone);
                            ((Activity)context).finish();
                            break;
                        }
                        case "_tokensWindow": {
                            Intent intentone = new Intent(context.getApplicationContext(), InfoTokensActivity.class);
                            intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentone);
                            break;
                        }
                        case "_openMap": {
                            Intent intentone = new Intent(context.getApplicationContext(), Map_Activity.class);
                            intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentone);
                            break;
                        }
                        case "_moveEstrenos": {
                            moveEstrenos(context.getApplicationContext());
                            break;
                        }
                        default:
                            Toast.makeText(context, "Command not detected", Toast.LENGTH_LONG).show();
                            break;
                    }
            }
        }
    }
}