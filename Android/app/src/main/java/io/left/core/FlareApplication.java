/*
*  ****************************************************************************
*  * Created by : Azizul Islam on 13-Oct-17 at 4:02 PM.
*  * Email : azizul@w3engineers.com
*  *
*  * Last edited by : Azizul Islam on 13-Oct-17.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/

package io.left.core;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;

import io.left.core.flare.BuildConfig;
import io.left.core.utils.helper.SharedPref;
import io.left.core.utils.lib.Glider;
import io.left.core.utils.helper.Toaster;

public class FlareApplication extends MultiDexApplication {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        releaseLoader(sContext);
        debugLoader(sContext);
    }

    private void debugLoader(Context context) {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(context);
        }
    }

    private void releaseLoader(Context context) {
        Glider.init(context);
        Toaster.init(context);
        SharedPref.init(context);
    }

    public static Context getContext() {
        return sContext;
    }
}
