package com.hergomsoft.easyorienteering.ui;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class VisualUtils {
    /**
     * Hides the software keyboard in the specified activity.
     * Credits: Accepted answer in https://stackoverflow.com/questions/1109022/close-hide-android-soft-keyboard
     * @param activity Activity which in the keyboard is displayed
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
