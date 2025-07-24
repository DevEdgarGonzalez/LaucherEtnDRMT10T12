package com.actia.encuesta;

public class InfoRespuestas {
    private String mName;
    private String mSelect;
    private String mComment;
    private int mRating;

    public InfoRespuestas(String name, String select, String comment, int rating){
        this.mName = name;
        this.mSelect = select;
        this.mComment = comment;
        this.mRating = rating;

    }

    public String getName() {
        return mName;
    }

    public String getSelect() {
        return mSelect;
    }

    public String getComment() {
        return mComment;
    }

    public int getRating(){
        return mRating;
    }

}
