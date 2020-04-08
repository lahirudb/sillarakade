package codegeeksolutions.sillarakade.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class LocalStorageHelper {

    public String getDataFromLocalStorage(String key, Context context) {
        String value = "";

        try {

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LOCALSTORAGE_KEY, MODE_PRIVATE);
            value = sharedPreferences.getString(key, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public void saveDataInLocalStorage(String key, String dataStr, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LOCALSTORAGE_KEY, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, dataStr);
        editor.commit();
    }

}
