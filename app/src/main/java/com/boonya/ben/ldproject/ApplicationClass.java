package com.boonya.ben.ldproject;

import android.app.Application;
import android.util.Log;

/**
 * Created by User on 12/3/2015.
 */
public class ApplicationClass extends Application {
    private static final String TAG = ApplicationClass.class.getSimpleName();
    public static MySQLiteHelper database;
//    public static TextToSpeech tts;

    private static ApplicationClass applicationClass;

    public static ApplicationClass getInstance() {
        if (applicationClass == null) {
            applicationClass = new ApplicationClass();
        }
        return applicationClass;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Spell check application", " spell check oncreate");
        database = new MySQLiteHelper(this);
    }
}
