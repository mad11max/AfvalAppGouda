package nl.pee65;

import android.app.Application;
import android.content.Context;

/**
 * Created by madmax on 31-12-2015.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
