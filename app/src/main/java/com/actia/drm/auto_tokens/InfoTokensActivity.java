package com.actia.drm.auto_tokens;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actia.infraestructure.BaseActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.shared_preferences.PreferencesApp;
import com.actia.mexico.launcher_t12_generico_2018.R;

import java.io.File;
import java.util.ArrayList;

public class InfoTokensActivity extends BaseActivity {
    private ListView lstTokensInfTok;
    private TextView lblTokTotInfoTok;
    private TextView lblTokCorrectInfoTok;
    private TextView lblTokNotRegInfoTok;
    private TextView lblTokErrorInfoTok;
    private TextView lblStatusKeyInfoTok;
    private Button btnValidateAgain;
    PreferencesApp genericPreferences;
    private TokensAutomaticLogic tokensAutomaticLogic;


    int total;
    int correct;
    int error;
    int notRegister;


    private TokensMoviesCRUD tokensMoviesCRUD;
    ArrayList<TokenMovie> allTokens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tokens);

        ConfigMasterMGC config = new ConfigMasterMGC();

        File dirConfig = new File(config.getCONFIG_DIRECTORY());
        File dirTokens = new File(config.getPathTokens());

        if (!dirConfig.exists()){
            dirConfig.mkdirs();
        }
        if (!dirTokens.exists()){
            dirTokens.mkdirs();
        }


        if (!dirConfig.exists()||!dirTokens.exists()) {
            Toast.makeText(this, getString(R.string.no_exist_dir_key_tokens), Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }


        tokensAutomaticLogic = new TokensAutomaticLogic(InfoTokensActivity.this, ConstantsApp.SCREEN_INFO_TOKENS);
        tokensAutomaticLogic.detectNewTokens();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startElementsUI();
                loadDataDb();
                setadaptersElements();
                drawElementsUI();
                addListeners();
            }
        }, 500);

    }

    private void addListeners() {
        btnValidateAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //fun: valida si  hay tokens y llaveDrm en carpeta
                ConfigMasterMGC config = ConfigMasterMGC.getConfigSingleton();
                File dirKeyUser = new File(config.getPathUserDRM());
                File dirTokens = new File(config.getPathTokens());

                if (!dirKeyUser.exists()||!dirTokens.exists()||dirTokens.listFiles().length==0){
                    Toast.makeText(InfoTokensActivity.this, getString(R.string.no_exist_dir_key_tokens), Toast.LENGTH_SHORT).show();
                    return;
                }



                if (notRegister > 0 || error > 0) {
                    genericPreferences.setValidationErors(ConstantsApp.MAX_ERRORS_KEY_WASABI - 1);

                    tokensAutomaticLogic.startProccesValidateTokens();
                } else {
                    Toast.makeText(InfoTokensActivity.this, getResources().getString(R.string.msg_no_tokens_pending), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadDataDb() {
        tokensMoviesCRUD = TokensMoviesCRUD.getInstance(this);
        allTokens = tokensMoviesCRUD.readALLMovies();


    }
    
    private void setadaptersElements() {


//        ArrayAdapter<TokensMovies> adapterTokensMov = new ArrayAdapter<TokensMovies>(this,android.R.layout.simple_spinner_item, allTokens);
        InfoTokensAdapter adapterTokensMov = new InfoTokensAdapter(this, allTokens);
        lstTokensInfTok.setAdapter(adapterTokensMov);
    }

    private void startElementsUI() {
        lstTokensInfTok = findViewById(R.id.lstTokensInfTok);
        lblTokTotInfoTok = findViewById(R.id.lblTokTotInfoTok);
        lblTokCorrectInfoTok = findViewById(R.id.lblTokCorrectInfoTok);
        lblTokNotRegInfoTok = findViewById(R.id.lblTokNotRegInfoTok);
        lblTokErrorInfoTok = findViewById(R.id.lblTokErrorInfoTok);
        lblStatusKeyInfoTok = findViewById(R.id.lblStatusKeyInfoTok);
        btnValidateAgain = findViewById(R.id.btnValidateAgain);

        genericPreferences = new PreferencesApp(this);
    }

    private void drawElementsUI() {
        total = allTokens.size();
        correct = tokensMoviesCRUD.getNumberOfRecordsByStatus(ConstantsApp.OPC_TOKEN_OK);
        error = tokensMoviesCRUD.getNumberOfRecordsByStatus(ConstantsApp.OPC_TOKEN_WITH_ERROR);
        notRegister = tokensMoviesCRUD.getNumberOfRecordsByStatus(ConstantsApp.OPC_TOKEN_NOT_REGISTERED);


        lblTokTotInfoTok.setText(total + "");
        lblTokCorrectInfoTok.setText(correct + "");
        lblTokNotRegInfoTok.setText(notRegister + "");
        lblTokErrorInfoTok.setText(error + "");


        int numberOErrors = genericPreferences.getValidationErors();


        if (!ContextApp.statusKey.equals("Ok")) {
            if (numberOErrors == 0) {

                File key = new File(ConfigMasterMGC.getConfigSingleton().getPathUserDRM());
                if (key.exists()) {
                    ContextApp.statusKey = getString(R.string.inf_tok_exist);
                } else {
                    ContextApp.statusKey = getString(R.string.inf_tok_no_exist);
                }
            } else if (numberOErrors < ConstantsApp.MAX_ERRORS_KEY_WASABI) {

                ContextApp.statusKey = getString(R.string.inf_tok_Error) + "  " + numberOErrors + "  " + getString(R.string.preposition_of) + "  " + ConstantsApp.MAX_ERRORS_KEY_WASABI;
            } else {
                ContextApp.statusKey = getString(R.string.inf_tok_error);
            }
        }


        lblStatusKeyInfoTok.setText(ContextApp.statusKey);
    }
}
