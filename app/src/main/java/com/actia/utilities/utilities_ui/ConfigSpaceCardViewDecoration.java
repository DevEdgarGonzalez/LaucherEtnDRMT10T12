package com.actia.utilities.utilities_ui;

import android.graphics.Rect;
import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by Edgar Gonzalez on 29/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class ConfigSpaceCardViewDecoration {

    public static RecyclerView.ItemDecoration setSpaceAllSides(int spaceLeft, int spaceRigth, int spaceTop, int spaceBottom){
        return new SpaceItemsCardViewDecoration(spaceLeft,spaceRigth, spaceTop,spaceBottom);
    }





    private static class SpaceItemsCardViewDecoration extends RecyclerView.ItemDecoration {
        private final int spaceLeft;
        private final int spaceRigth;
        private final int spaceTop;
        private final int spaceBottom;


        public SpaceItemsCardViewDecoration(int spaceLeft, int spaceRigth, int spaceTop, int spaceBottom) {
            this.spaceLeft = spaceLeft;
            this.spaceRigth = spaceRigth;
            this.spaceTop = spaceTop;
            this.spaceBottom = spaceBottom;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = spaceTop;
            outRect.bottom = spaceBottom;
            outRect.left = spaceLeft;
            outRect.right = spaceRigth;
//        parent.setBackgroundResource(R.drawable.img_recamara_infantil_fondo);

        }


    }

    private class BackgroundItemDecoration extends RecyclerView.ItemDecoration {

        private final int mOddBackground;
        private final int mEvenBackground;

        public BackgroundItemDecoration(@DrawableRes int oddBackground, @DrawableRes int evenBackground) {
            mOddBackground = oddBackground;
            mEvenBackground = evenBackground;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            view.setBackgroundResource(position % 2 == 0 ? mEvenBackground : mOddBackground);
        }
    }
}

