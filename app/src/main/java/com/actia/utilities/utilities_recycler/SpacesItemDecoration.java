package com.actia.utilities.utilities_recycler;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
//        outRect.left = space;
        outRect.right = space;
//        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.left = 0;
        } else {
            outRect.left = space;
        }

        if (parent.getChildLayoutPosition(view)== parent.getChildCount()-1){
            outRect.right = 0;
        }else{
            outRect.right = space;
        }
    }

}
