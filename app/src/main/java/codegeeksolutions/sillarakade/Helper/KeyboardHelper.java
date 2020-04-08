package codegeeksolutions.sillarakade.Helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import codegeeksolutions.sillarakade.Activity.MainActivity;
import codegeeksolutions.sillarakade.Interfaces.HideKeyboard;

public class KeyboardHelper implements HideKeyboard {
    View view;
    Activity activity;
    public KeyboardHelper(View view, Activity activity) {
        this.view = view;
        this.activity = activity;
    }
    @Override
    public void hideSofKeybiard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
