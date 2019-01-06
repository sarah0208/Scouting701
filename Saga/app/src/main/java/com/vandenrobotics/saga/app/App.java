package com.vandenrobotics.saga.app;

import android.app.Application;
import android.content.Context;

import com.vandenrobotics.saga.data.DataBaseHelper;
import com.vandenrobotics.saga.data.DatabaseManager;

/**
 * Created by Programming701-A on 12/15/2017.
 */

public class App extends Application {
    private static Context context;
    private static DataBaseHelper dataBaseHelper;

    @Override
    public void onCreate(){
        super.onCreate();
        context = this.getApplicationContext();
        dataBaseHelper = new DataBaseHelper();
        DatabaseManager.initializedInstance(dataBaseHelper);
    }

    public static Context getContext(){
        return context;
    }
}
