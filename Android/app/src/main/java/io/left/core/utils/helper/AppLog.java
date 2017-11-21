package io.left.core.utils.helper;

import android.util.Log;

/*
*  ****************************************************************************
*  * Created by : Azizul Islam process 19-Oct-17 at 4:02 PM.
*  * Email : azizul@w3engineers.com
*  *
*  * Last edited by : Sudipta K Paik Islam process 24-Oct-17.
*  *
*  * Last Reviewed by : <Reviewer Name> process <mm/dd/yy>
*  ****************************************************************************
*/
public final class AppLog {

    private AppLog() {
    }

    private static final String TAG = AppLog.class.getName();

    public static void v(String log) {
        v(null, log);
    }

    public static void e(String log) {
        e(null, log);
    }

    public static void v(String tag, String log) {
        Log.v(tag == null ? TAG : tag, log);
    }

    public static void e(String tag, String log) {
        Log.e(tag == null ? TAG : tag, log);
    }
}
