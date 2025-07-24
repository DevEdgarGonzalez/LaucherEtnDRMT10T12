package com.actia.utilities.utilities_ui.utilitiesOrderItems;

import android.app.Activity;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.Gravity;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Edgar Gonzalez on 06/03/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class OrderElementsFourThree {
    int sizeArray;
    Activity activity;
    ArrayList<Integer> alistNeedCustom;
//    ArrayList<Integer> alistRigth;
//    ArrayList<Integer> alistLeft;

    public final int ARG_PADDING_LEFT = 0;
    public final int ARG_PADDING_RIGTH = 1;
    public final int ARG_PADDING_CUSTOM = 2;

    public OrderElementsFourThree(Activity activity, int sizeArray) {
        this.sizeArray = sizeArray;
        this.activity = activity;
    }

    /*public GridLayoutManager getGridManager(){
        addElementsCustomToAlist();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 12);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (alistNeedCustom.contains(position)){
                    return 4;
                }
                return 3;
            }
        });
        return  gridLayoutManager;
    }*/


    /**
     * metodo que configura el gridLayout manager, este metodo solo se debe de agregar en donde se inicie el recycler view
     *
     * @return gridLayoutManager customizado
     */
    public GridLayoutManager getGridManagerVtwo() {
        addElementsCustomToAlistTwo();

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 8);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//
//                if (alistNeedCustom.contains(position)) {
//                    return 4;
//                } else {
//                    return 2;
//                }
//            }
//        });
//        return gridLayoutManager;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 8);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (alistNeedCustom.contains(position)) {
                    return 4;
                } else {
                    return 2;
                }
            }
        });
        return gridLayoutManager;
    }

    /**
     * Agrega a un arreglo cada septimo elemento (7-1) que son los que van a tener un span de 4
     */
    private void addElementsCustomToAlistTwo() {
        alistNeedCustom = new ArrayList<>();

        int numberAdd = 6;


        while (numberAdd < sizeArray) {

            alistNeedCustom.add(numberAdd);
            numberAdd = numberAdd + 7;

        }

    }


    /**
     * Divide los elementos en bloques de 7 para saber que tipo de padding se le va a dar
     * padding izquierdo elementos:1, 2, 3 y 4
     * padding derecho 5 y 6
     * padding custom elemento 7
     *
     * @param position posicion del elemento en el adaptador
     * @return regresa el tipo de padding que le correponde
     */
    public int getTypePaddingForElement(int position) {

        //si todavia no se cargan los elementos numero 7
        if (alistNeedCustom == null) {
            addElementsCustomToAlistTwo();

        }

        int positionElement = position + 1;
        int resultDivision = positionElement / 7;

        if ((resultDivision * 7) > sizeArray) {
            resultDivision--;
        }

        int numberElement = positionElement - (resultDivision * 7);

        int abc = 99;

        if (numberElement > 0 && numberElement <= 4) {
            abc = ARG_PADDING_LEFT;
        } else if (numberElement > 4 && numberElement < 7) {
            abc = ARG_PADDING_RIGTH;
        } else if (numberElement == 7) {
            abc = ARG_PADDING_CUSTOM;
        }

        return abc;
    }


    /**
     * metodo que obtiene el tipo de padding dependiendo de la posicion
     *
     * @param lyt
     * @param position
     */
    public void configureElementInLayout(LinearLayout lyt, int position) {


        int typePadding = getTypePaddingForElement(position);
        if (typePadding == ARG_PADDING_LEFT) {
            lyt.setGravity(Gravity.LEFT);
        } else if (typePadding == ARG_PADDING_RIGTH) {
            lyt.setGravity(Gravity.END);
        } else {
            lyt.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 125, 0);
            lyt.setLayoutParams(params);
        }
    }


    /*private void addElementsCustomToAlist() {
        alistNeedCustom = new ArrayList<>();
        int counFlag = 0;
        int numberAdd = 4;


        while (numberAdd < sizeArray) {

            if (counFlag < 3) {
                alistNeedCustom.add(numberAdd);
                counFlag++;
                numberAdd++;
            } else {
                counFlag = 0;
                numberAdd = numberAdd+4;

            }

        }

    }
*/
  /*  private void startElementswhitPadding(){

        int flag=0;
        int positionArray=0;

        while (positionArray<alistNeedCustom.size()){

            if (flag==0){
                alistRigth.add(alistNeedCustom.get(positionArray));
            }else if (flag==2){
                alistLeft.add(alistNeedCustom.get(positionArray));
                positionArray=0;
            }

            flag++;
            positionArray++;
            if (flag>=2){
                flag=0;
            }


        }

    }*/


}
