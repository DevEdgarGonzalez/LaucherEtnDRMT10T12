package com.actia.utilities.utilities_ui;

import androidx.recyclerview.widget.GridLayoutManager;

/**
 * Created by Edgar Gonzalez on 22/02/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class UtilitiesRecyclerView {


    public static GridLayoutManager.SpanSizeLookup getSpanMusicGenres() {
        return new GridLayoutManager.SpanSizeLookup() {


            @Override
            public int getSpanSize(int positionGrid) {

                if (positionGrid == 0) {

                } else if (positionGrid == 1) {

                }
//                int position = positionGrid+1;
//
//                if (position>=4){
//                    return 1;
//                }else if (position==5){
//                    span = 2;
//                }else if (position==)


                return 1;

            }
        };

    }


    public static GridLayoutManager.SpanSizeLookup getSpanSizeTresDos(final int numberAllElements) {
        if (numberAllElements == 0) {
            return genericSpanZise();
        }
        final int hastaQueelementoPintaNormal;
        final int elementosCustom;

        hastaQueelementoPintaNormal = (numberAllElements / 10) * 10;


        if (hastaQueelementoPintaNormal > 0) {
            elementosCustom = numberAllElements % 10;
        } else {
            elementosCustom = numberAllElements;
        }

        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int numberElement= position+1;
                if(hastaQueelementoPintaNormal>0){
                    if (numberElement<=hastaQueelementoPintaNormal){
                        return 1;
                    }else {
                        return  spanCustomElements(position-hastaQueelementoPintaNormal,elementosCustom);
                    }
                }else{
                    return spanCustomElements(position-hastaQueelementoPintaNormal,elementosCustom);
                }


            }

            private int spanCustomElements(int noElementCustom, int allElementsCustoms) {
                //si son mas de 5 todos los elementos (se maeja la validacion para pintar especial del elemento 6-10)
                if (allElementsCustoms>5){

                    //el elemento actual es menos de 5 (se pinta con span 1)
                    if (noElementCustom<=5){
                        return 1;

                    }else{
                        //el elemento se pinta especial

                        return spanLessFive(noElementCustom);
                    }
                }else{

                    //Si son 4 elementos o menos se pinta especial
                    return spanLessFive(noElementCustom);
                }


            }

            private int spanLessFive(int noElementCustom) {
                if (noElementCustom==1){
                    return 1;
                }else if (noElementCustom==2){
                    return 1;
                }else if (noElementCustom==3){
                    return 1;
                }else if (noElementCustom==4){
                    return 0;
                }else {
                    return 0;
                }
            }
        };

    }

    private static GridLayoutManager.SpanSizeLookup genericSpanZise() {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        };
    }

}
