package com.actia.utilities.utilities_dialog_brightness;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;

import com.actia.mexico.launcher_t12_generico_2018.R;


public class DialogBrightnessFragment extends DialogFragment {
	
	public static DialogBrightnessFragment newInstance(){
		
		DialogBrightnessFragment f = new DialogBrightnessFragment();
		
		return f;
	}
	
	SeekBar backControlLight;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int style = DialogFragment.STYLE_NORMAL, theme = 0;

        theme = R.style.Base_Theme_MaterialComponents_Light_Dialog;

        setStyle(style, theme);
        final float BackLightValue;
        View rootView=inflater.inflate(R.layout.brightnessbar, container, false);
        int brightness = 0;
        if(cResolver == null)
            cResolver = getActivity().getApplicationContext().getContentResolver();

        try {
            brightness = Settings.System.getInt(cResolver,
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        backControlLight = rootView.findViewById(R.id.backlightcontrol);
        backControlLight.setMax(255);
        backControlLight.setProgress(brightness);
        getDialog().setTitle(R.string.title_brightness_dialog);

        backControlLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int level = progress;
                ScreenBrightness(progress, getActivity().getApplicationContext());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        backControlLight = null;
    }

    private int brightness;
  //Content resolver used as a handle to the system's settings
  private ContentResolver cResolver;
  //Window object, that will store a reference to the current window
  private Window window;

  boolean ScreenBrightness(int level, Context context) {

      try {

          cResolver = context.getContentResolver();
          brightness = Settings.System.getInt(cResolver,
                  Settings.System.SCREEN_BRIGHTNESS);

//          if(brightness == 255)
//              level = 2;

          Settings.System.putInt(
                  context.getContentResolver(),
                  Settings.System.SCREEN_BRIGHTNESS, level);

          Settings.System.putInt(context.getContentResolver(),
                  Settings.System.SCREEN_BRIGHTNESS_MODE,
                  Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

          Settings.System.putInt(
                  context.getContentResolver(),
                  Settings.System.SCREEN_BRIGHTNESS,
                  level);

          return true;
      }

      catch (Exception e) {
          if(e != null)
          Log.e("Screen Brightness", e.toString());
          return false;
      }
  }

}
