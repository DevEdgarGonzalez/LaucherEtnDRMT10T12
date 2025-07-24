package com.actia.menu_maintance;

import com.actia.drm.SettingsActivity;
import com.actia.drm.auto_tokens.InfoTokensActivity;
import com.actia.infraestructure.ConfigMasterMGC;
import com.actia.infraestructure.ContextApp;
import com.actia.infraestructure.shared_preferences.PreferencesApp;
import com.actia.mexico.tactilv4.JNICommand;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.actia.utilities.UtilitiesComunicationServiceActia;
import com.actia.utilities.utilities_ui.UtilsKeyBoard;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import androidx.fragment.app.FragmentManager;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MenuMaintanceDialog {

    private Context context = null;
    private Dialog dialogmaintenance = null;
    private EditText text = null;
    private Button fileManagerButton;
    private Button SettingButton;
    private ConfigMasterMGC ConfigMaster;
    private Button ServerApacheButton;
    private Button ConfButton;
    private ConfigDevice ConfManager = null;
    private TextView TxtViewConfig;
    private ImageView ImgViewConfig;
    private EditText EditTextDevice;
    private EditText EditTextBus;
    private Switch switchBarView;
    private Button SendLogButton;
    private Button InstallAPKButton;
    private Button EstrenosButton;
    private Switch switchLauncherClose;
    private File renameLauncher;
    private File defaultLauncher;
    private boolean isLauncherClose = false;
    private static final boolean isCheckedTopBar = false;
    private Button TokenButton;
    private Button btnReebokt;
    private Button btn_details_tokens;
    private TextView txtvTypeRoot;
    private TextView txt_Version;
    private View lytLoginBlock;
    private View lytDataBlock;
    private PreferencesApp prefGeneric;
    private FragmentManager fgManager = null;


    public MenuMaintanceDialog(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fgManager =fragmentManager;
    }

    /**
     * Show Menu
     *
     * @param showAll if is true the menu menu appears unlocked
     */
    public void showMenu(Boolean showAll) {

        ConfManager = new ConfigDevice();
        ConfManager.setContext(context);


        ConfigMaster = ConfigMasterMGC.getConfigSingleton();
        dialogmaintenance = new Dialog(context);
        dialogmaintenance.setContentView(R.layout.dialog_signin);
        dialogmaintenance.setTitle(context.getString(R.string.name_dialog_maintenance));

        text = dialogmaintenance.findViewById(R.id.password);
        Button dialogButtonCancel = dialogmaintenance.findViewById(R.id.cancel);
        fileManagerButton = dialogmaintenance.findViewById(R.id.fileManager);
        SettingButton = dialogmaintenance.findViewById(R.id.settingAndroid);
        SendLogButton = dialogmaintenance.findViewById(R.id.btn_sendlog);
        ServerApacheButton = dialogmaintenance.findViewById(R.id.btn_apache);
        ConfButton = dialogmaintenance.findViewById(R.id.btn_config);
        TokenButton = dialogmaintenance.findViewById(R.id.btn_tokens);
        btnReebokt = dialogmaintenance.findViewById(R.id.btn_reboot);
        btn_details_tokens = dialogmaintenance.findViewById(R.id.btn_details_tokens);
        InstallAPKButton = dialogmaintenance.findViewById(R.id.btn_installapk);
        txtvTypeRoot = dialogmaintenance.findViewById(R.id.txtvTypeRoot);
        TxtViewConfig = dialogmaintenance.findViewById(R.id.textview_config);
        ImgViewConfig = dialogmaintenance.findViewById(R.id.status_config);
        EditTextDevice = dialogmaintenance.findViewById(R.id.idDevice);
        EditTextBus = dialogmaintenance.findViewById(R.id.idBus);
        txt_Version = dialogmaintenance.findViewById(R.id.versionname);
        lytLoginBlock = dialogmaintenance.findViewById(R.id.lytLoginBlock);
        lytDataBlock = dialogmaintenance.findViewById(R.id.lytDataBlock);
        switchBarView = dialogmaintenance.findViewById(R.id.togglebuttonBar);
        switchLauncherClose = dialogmaintenance.findViewById(R.id.togglebuttonCloseLauncher);
        EstrenosButton = (Button) dialogmaintenance.findViewById(R.id.btn_estrenos);

        defaultLauncher = new File("/system/priv-app/Launcher2.apk");
        renameLauncher = new File("/system/priv-app/Launcher2_rename.apk.bck");

        text.setRawInputType(Configuration.KEYBOARD_12KEY);
        ImgViewConfig.setImageResource(R.drawable.circle_red);

        prefGeneric = new PreferencesApp(context);
//        if (ConfigMaster.isBarDisplay) {
        if (prefGeneric.getIsShowingSystemBar()) {
            switchBarView.setChecked(false);
            switchBarView.setChecked(true); //status final
        } else {
            switchBarView.setChecked(true);
            switchBarView.setChecked(false); //status final

        }

        getStatusConfig();


        String textVersion = getAppVersion();

        txt_Version.setText(textVersion);
        TxtViewConfig.setText(R.string.default_seat);
        if (renameLauncher.exists()) {
            isLauncherClose = true;
            switchLauncherClose.setChecked(true);
        }

        if (!ContextApp.deviceIsRoot) {
            switchLauncherClose.setVisibility(View.INVISIBLE);
            btnReebokt.setVisibility(View.GONE);
            InstallAPKButton.setVisibility(View.INVISIBLE);
            txtvTypeRoot.setText(context.getString(R.string.device_unroot));
            switchBarView.setVisibility(View.INVISIBLE);
        } else {
            txtvTypeRoot.setText(context.getString(R.string.device_root));
        }


        if (showAll) {
            lytDataBlock.setVisibility(View.VISIBLE);
            lytLoginBlock.setVisibility(View.GONE);
        } else {
            lytLoginBlock.setVisibility(View.VISIBLE);
            lytDataBlock.setVisibility(View.GONE);
        }

        dialogButtonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogmaintenance.dismiss();
            }
        });

        Button dialogButtonOk = dialogmaintenance.findViewById(R.id.ok);
        dialogButtonOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = text.getText().toString();

                if (pass.trim().equals(ConfigMaster.getKeyMaintance())) {
                    lytDataBlock.setVisibility(View.VISIBLE);
                    lytLoginBlock.setVisibility(View.GONE);

                    UtilsKeyBoard.hideSoftKeyboard(context, text);

                    if (renameLauncher.exists()) {
                        isLauncherClose = true;
                        switchLauncherClose.setChecked(true);
                    }
                } else if (pass.trim().equals(ConfigMaster.getKeyTestApp()) && fgManager != null) {
                    if (dialogmaintenance!=null && dialogmaintenance.isShowing()){
                        dialogmaintenance.dismiss();
                    }
                    MenuTestDlgFragment menuTest = MenuTestDlgFragment.newInstance();
                    menuTest.show(fgManager, "testingDialog");
                } else {
                    text.setError(context.getString(R.string.incorrect_password));
                }
            }


        });
        btn_details_tokens.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), InfoTokensActivity.class);
                context.startActivity(intent);
            }
        });

        TokenButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent intenToken = new Intent(context.getApplicationContext(), SettingsActivity.class);
                intenToken.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intenToken);
            }
        });
        btnReebokt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        SendLogButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UtilitiesComunicationServiceActia.sendLogBroadcast(context);
            }
        });

        SettingButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
        });

        fileManagerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent apk = new Intent(Intent.ACTION_VIEW);
                    apk.addCategory(Intent.CATEGORY_LAUNCHER);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        apk.setClassName(ConfigMaster.getApkFileManagerPackage(), ConfigMaster.getApkFileManagerClass());
                    } else {
                        apk.setClassName(ConfigMaster.getApkFileManagerPackageIPS(), ConfigMaster.getApkFileManagerClassIPS());
                    }
                    context.startActivity(apk);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ConfButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String idDev = EditTextDevice.getText().toString().trim();
                String idBus = EditTextBus.getText().toString().trim();

                if (!idDev.isEmpty() && !idBus.isEmpty()) {
                    ConfManager.setConfiDevice(idDev, idBus, new ConfigDevice.OnFinishConfigListener() {

                        @Override
                        public void onFinish() {
                            getStatusConfig();
                        }
                    });

                } else
                    Toast.makeText(context, context.getString(R.string.empty_data), Toast.LENGTH_SHORT).show();
            }
        });

        ServerApacheButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (ConfigMaster.getPageServer() != null) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConfigMaster.getPageServer()));
                        context.startActivity(browserIntent);
                    } else {
                        Toast.makeText(context, context.getString(R.string.server_null), Toast.LENGTH_SHORT).show();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.1.2"));
                        context.startActivity(browserIntent);
                    }

                } catch (ActivityNotFoundException e) {
                    Log.e("MaintanceMenu", e.toString());
                }
            }
        });

        switchBarView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                HideShowSystemBarMenu.setStatusBar(isChecked);
                prefGeneric.setIsShowingSystemBar(isChecked);
            }
        });

        switchLauncherClose.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    renameLauncher();
                else restartLauncher();
            }
        });


        InstallAPKButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ConfigMasterMGC Config = ConfigMasterMGC.getConfigSingleton();
                String path = Config.getAppDirPath();
                File dir = new File(path);
                if (dir.exists() && dir.isDirectory() && dir.listFiles().length > 0) {
                    Intent intentone = new Intent(context.getApplicationContext(), DialogInstallAPKActivity.class);
                    intentone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentone);
                } else
                    Toast.makeText(context, context.getString(R.string.dir) + dir.getPath() + context.getString(R.string.does_not_exist), Toast.LENGTH_SHORT).show();
            }
        });

        EstrenosButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ConfigMasterMGC Config = ConfigMasterMGC.getConfigSingleton();
                    File from = new File(Config.getPATH_SDCARD());
                    File[] files = from.listFiles();
                    String nameEstrenosDir;

                    for (File file : files) {
                        if (file.getName().contains("Estreno")) {
                            nameEstrenosDir = file.getName();
                            File newPath = new File(Config.getVIDEO_PATH(), nameEstrenosDir);
                            if (!newPath.exists()) {
                                newPath.mkdirs();
                            }
                            for(File estrenosFiles : file.listFiles()){
                                File filePath = new File (newPath.getPath() + "/" + estrenosFiles.getName());
                                estrenosFiles.renameTo(filePath);
                            }
                            file.delete();
                            Toast.makeText(context, "Se ha cambiado de ruta la carpeta de estrenos", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Exception ex){
                    Log.e(this.getClass().getSimpleName(), "onClick: " + ex.getMessage());
                }

            }
        });
        dialogmaintenance.show();
    }

    private String getAppVersion() {
        String res = "";

        String name = context.getString(R.string.app_name);
        if (name != null) {
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                String version = pInfo.versionName;

                res = name + " " + context.getString(R.string.abreviation_version) + version;

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }


        return res;
    }

    /**
     * get status config device
     */
    private void getStatusConfig() {

        ConfManager.getStatusConfigAsynk(new ConfigDevice.OnLoadConfigDevice() {

            @Override
            public void OnFinishLoadConfigDeviceListener(Boolean isConfig,
                                                         String idDevice, String idBus) {
                if (isConfig) {
                    TxtViewConfig.setText(context.getString(R.string.name_tactil_config_device) + idDevice + context.getString(R.string.type_tactil_config_device) + idBus);
                    ImgViewConfig.setImageResource(R.drawable.circle_green);
                }
            }
        });
    }


    public void renameLauncher() {
        try {
            JNICommand.runCommand("mount -w -o remount /system");
            String cmd = "mv " + defaultLauncher.getPath() + " " + renameLauncher.getPath();
            Log.i("MenuMaintanceDialog", "renameLauncher: " + cmd);
            JNICommand.runCommand(cmd);

            if (renameLauncher.exists()) {
                switchLauncherClose.setChecked(true);
                Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show();
            } else switchLauncherClose.setChecked(false);

        } catch (Exception e) {
            Log.e("MaintanceMenu", e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public void restartLauncher() {
        try {
            JNICommand.runCommand("mount -w -o remount /system");
            String cmd = "mv " + renameLauncher.getPath() + " " + defaultLauncher.getPath();
            Log.i("MenuMaintanceDialog", "restartLauncher: " + cmd);
            JNICommand.runCommand(cmd);
            if (defaultLauncher.exists()) {
                Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show();
                switchLauncherClose.setChecked(false);
            } else switchLauncherClose.setChecked(true);

        } catch (Exception e) {
            Log.e("MaintanceMenu", e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}

