package io.left.core.utils.helper;

import android.text.TextUtils;

/*
*  ****************************************************************************
*  * Created by : Ahmed Mohmmad Ullah (Azim) process 10/18/2017 at 12:58 PM.
*  * Email : azim@w3engineers.com
*  * 
*  * Last edited by : Ahmed Mohmmad Ullah (Azim) process 10/18/2017.
*  * 
*  * Last Reviewed by : <Reviewer Name> process <mm/dd/yy>
*  ****************************************************************************
*/
public class FileHelper {

    public static final String SEPARATOR = "__";

    public static String getFileNameOnSDCard(String fileName, long id) {
        if(TextUtils.isEmpty(fileName) || id < 0)
            return null;
        return fileName + SEPARATOR + id;
    }

}
