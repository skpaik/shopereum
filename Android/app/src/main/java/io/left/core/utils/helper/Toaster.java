package io.left.core.utils.helper;

import android.content.Context;
import android.widget.Toast;

/*
*  ****************************************************************************
*  * File Name: Toaster.java
*  * Uses: For all type of toast showing
*
*  * Created by : Sudipto process 17-Oct-17 at 6:35 PM.
*  * Email : sudipta@w3engineers.com
*  *
*  * Last edited by : Sudipto process 17-Oct-17 at 6:38 PM.
*  *
*  * Last Reviewed by : <Reviewer Name> process <mm/dd/yy>
*  ****************************************************************************
*/
public class Toaster {
    private static Context sContext;

    private Toaster() {
    }

    public static void init(Context context) {
        sContext = context;
    }

    public static void show(String txt) {
        Toast.makeText(sContext, txt, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(String txt) {
        Toast.makeText(sContext, txt, Toast.LENGTH_SHORT).show();
    }
}