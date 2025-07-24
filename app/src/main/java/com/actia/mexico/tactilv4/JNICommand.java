package com.actia.mexico.tactilv4;

public class JNICommand {

    //Para que esta clase funcione se debe de localizar en:
    //main\java\com\actia\mexico\tactilv4
    public static native boolean runCommand(String cmd);
    
    static {
        System.loadLibrary("com_jni");
        System.loadLibrary("libWasabiJni");
    }
}