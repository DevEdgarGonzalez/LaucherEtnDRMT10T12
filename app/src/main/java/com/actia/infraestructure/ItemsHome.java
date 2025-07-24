package com.actia.infraestructure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represent each item in the General Home section.
 */
public class ItemsHome implements Parcelable {

    private int id;
    private int browser;
    private int submenu;
    String title;
    public String title_en;
    public String pathImg;
    public String pathImg_en;
    private String intent;
    String url;
    private String packageName;
    private String className;
    private boolean isaplication;
    private boolean isAnimation;

    public ItemsHome(int id,
                     String title,
                     String title_en,
                     String packageName,
                     String className,
                     String intent,
                     String url,
                     String pathImg,
                     boolean isaplication,
                     boolean isAnimation,
                     int browser,
                     int submenu) {

        this.id = id;
        this.title = title;
        this.title_en = title_en;
        this.packageName = packageName;
        this.className = className;
        this.intent = intent;
        this.url = url;
        this.pathImg = pathImg;
        this.isaplication = isaplication;
        this.isAnimation = isAnimation;
        this.browser = browser;
        this.submenu = submenu;
    }

    public ItemsHome() {
    }

    /**
     * Get Id of the app configured in the json file.
     *
     * @return A id
     */
    public int getId() {
        return id;
    }

    /**
     * get a number that represents whether it is a link to display in a browser.
     *
     * @return 1 if is a link to open in a browser.0 otherwise
     */
    int getBrowser() {
        return browser;
    }

    public boolean isSubMenu() {
        return submenu == 1;
    }

    /**
     * get app's title
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getPathImg_en() {
        return pathImg_en;
    }

    /**
     * get image's path
     *
     * @return path
     */
    public String getPathImg() {
        return pathImg;
    }


    public void setPathImg(String pathImg) {
        this.pathImg = pathImg;
    }

    /**
     * get app's intent
     *
     * @return intent name
     */
    public String getIntent() {
        return intent;
    }

    /**
     *get app's url
     *@return url name
     */
//	public String getUrl() {
//		return url;
//	}

    /**
     * get app's packagename
     *
     * @return package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * get app's ClassName
     *
     * @return class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * define whether it is application or photo
     *
     * @return true if is application
     */
    public boolean isAplication() {
        return isaplication;
    }


    /**
     * define whether it is has animation
     *
     * @return true if it's has animation
     */
//	public boolean isAnimation() {
//		return isAnimation;
//	}
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.browser);
        dest.writeInt(this.submenu);
        dest.writeString(this.title);
        dest.writeString(this.title_en);
        dest.writeString(this.pathImg);
        dest.writeString(this.intent);
        dest.writeString(this.url);
        dest.writeString(this.packageName);
        dest.writeString(this.className);
        dest.writeByte(this.isaplication ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAnimation ? (byte) 1 : (byte) 0);
    }

    protected ItemsHome(Parcel in) {
        this.id = in.readInt();
        this.browser = in.readInt();
        this.submenu = in.readInt();
        this.title = in.readString();
        this.title_en = in.readString();
        this.pathImg = in.readString();
        this.intent = in.readString();
        this.url = in.readString();
        this.packageName = in.readString();
        this.className = in.readString();
        this.isaplication = in.readByte() != 0;
        this.isAnimation = in.readByte() != 0;
    }

    public static final Creator<ItemsHome> CREATOR = new Creator<ItemsHome>() {
        @Override
        public ItemsHome createFromParcel(Parcel source) {
            return new ItemsHome(source);
        }

        @Override
        public ItemsHome[] newArray(int size) {
            return new ItemsHome[size];
        }
    };
}
