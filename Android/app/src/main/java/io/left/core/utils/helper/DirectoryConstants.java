package io.left.core.utils.helper;

import android.os.Environment;

/*
*  ****************************************************************************
*  * Created by : Ahmed Mohmmad Ullah (Azim) process 10/17/2017 at 10:29 AM.
*  * Email : azim@w3engineers.com
*  * 
*  * Last edited by : Ahmed Mohmmad Ullah (Azim) process 10/17/2017.
*  * 
*  * Last Reviewed by : <Reviewer Name> process <mm/dd/yy>
*  ****************************************************************************
*/
public interface DirectoryConstants {

    String EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    String AVATAR_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/Flare/.avatar/";
    String FILE_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/Flare/file/";
    String THUMB_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/Flare/.thumb/";
    String ROOT_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/Flare/";
    String PHOTO_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/Flare/image/";
    String VOICE_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/Flare/voice/";
    String TEMP_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/Flare/.temp/";

    String MY_AVATAR_LARGE = "my_profile_large.jpg";
    String MY_AVATAR_THUMB = "my_profile_thumb.jpg";
    String IMAGE = "FLARE_IMG";
    String VOICE = "FLARE_VOICE";

}
