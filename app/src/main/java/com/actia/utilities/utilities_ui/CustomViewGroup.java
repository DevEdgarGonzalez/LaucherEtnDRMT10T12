package com.actia.utilities.utilities_ui;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class CustomViewGroup extends LinearLayout{

	  public CustomViewGroup(Context context) {
	   super(context);
	  }
	  
	  @Override
	  public boolean onInterceptTouchEvent(MotionEvent ev) {
	   return true;
	  }
}
