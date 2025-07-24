package com.actia.infraestructure;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Edgar Gonzalez on 21/02/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class ContextApp {
    public static String idBuss ="";
    public static  String idSeat ="";

    public static int TYPE_SCREEN = ConstantsApp.TYPE_SCREEN_10PLG;
    public static HashMap<Integer, ItemsHome> categoriesApp = null;
    public static ArrayList<ItemsHome> subCategoriesApp = null;
    public static HashMap<Integer, ItemsHome> categoriesChildren = null;
    public static boolean deviceIsRoot = false;

    public static ItemsHome childrenCategory = null;

    public static boolean enableCustomization = false;

    public static String statusKey = "";
}
