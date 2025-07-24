package com.actia.infraestructure;

/**
 * Created by Edgar Gonzalez on 07/11/2017.
 * Actia de Mexico S.A. de C.V..
 */

public class ConstantsApp {

    public static final String IP_RASP = "192.168.3.135";
    public static final int PORT_RASP = 6789;


    public static final String PASSWORD_TACTIL = "482482";
    public static final String nameAppTesting = "mx.com.cesarcorona.actiabenchmarck";
    public static final String nameAppTestingFirsActivity = "mx.com.cesarcorona.actiabenchmarck.activities.SplashActivity";
    public static final String STATE_SD_CARD_MOUNTED = "mounted";
    public static final int TYPE_SCREEN_12PLG = 3;
    public static final int TYPE_SCREEN_10PLG = 2;
    public static final int TYPE_SCREEN_7PLG = 1;


    //Category
    public static final int CATEGORY_NO_DETECTED = 99;
    public static final String ARG_POSITION_CATEGORY = "positionCategory";
    public static final String ARG_MODULO = "MODULO";


    //Tokens
    public static final String DATE_FROM_SD = "File initial SD";
    public static boolean isRunningTskTokens = false;

    public static final int SCREEN_ADVERSITING = 1;
    public static final int SCREEN_INFO_TOKENS = 2;

    public static final int MAX_ERRORS_KEY_WASABI = 5;

    public static final long DURATION_SECONDS_DIALOG = 10000;
    public static final int SECONDS_DELAY_Validation_tokens = 40;

    public static final int OPC_TOKEN_OK = 1;
    public static final int OPC_TOKEN_NOT_REGISTERED = 2;
    public static final int OPC_TOKEN_WITH_ERROR = 3;
    public static final int OPC_TOKEN_UNKNOWN = 99;


    //Sort categories
    public static final boolean SORT_BY_ALPHABETICAL_IMG_ADVERTISING = true;

    public static final boolean SORT_BY_ALPHABETICAL_CATEGORIES = true;

    public static final boolean iSeatEnable = false;
    public static final boolean enableQuizzInVideoInit = false;
    public static final boolean enableQuizzInVideoInt =false;


    public static final int REQUEST_ENABLE_BT = 85;


    //BroadcastReceivers
    public static final String ARG_NEW_INSTANCE_APP = "newInstanceApp";

    //Args
    public static final String ARG_CATEGORY = "CATEGORY";

    public static final String ARG_ROOT_PATH_SUB_GENRE = "ArgRootPathSubMenu";

    public static final String ARG_IS_SUBMENU = "ArgIsSubmenu";


    public static final boolean addChildrenInMoviesGnres = true;
    public static final String nameCategoryChildren = "fantil";


    public static final String ARG_IMG_SUBGENRE = "ARG_IMG_SUB_MENU";
    public static final String ARG_TITLE_SUBGENRE = "ARG_TITLE_SUBmENU";
    public static final String ARG_SUBCAT = "ARG_SUBCAT";

    public static final boolean MULTILANGUAGE_ENABLE = true;
    public static final int LANGUAGE_NAMEDIR_POSITION_ES = 0;
    public static final int LANGUAGE_NAMEDIR_POSITION_EN = 1;

}
