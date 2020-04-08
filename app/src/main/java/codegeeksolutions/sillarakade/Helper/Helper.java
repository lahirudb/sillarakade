package codegeeksolutions.sillarakade.Helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import codegeeksolutions.sillarakade.Fragments.CreateItems;
import codegeeksolutions.sillarakade.R;

public class Helper {

    int a= 10;
    public static void openFragment(Context activity, Fragment fragment) {
        int a = 12;

        a= 5;
    }

    public static Dialog showProgress(Activity context) {
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_layout);

        if(progressDialog.getWindow() != null)
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        progressDialog.setCancelable(true);
        progressDialog.show();

        return progressDialog;
    }

    public static void showSnackBar(Context mcontext, String msg) {
        Toast.makeText(mcontext, msg, Toast.LENGTH_LONG).show();
    }

    public static boolean isEditTextIsEmpty(EditText editText) {
        if (editText != null) {
            if (editText.getText().toString().equals("")) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static void loadFragment(Activity activity, Fragment fragment) {

        FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_container, fragment, "fragmentLoader");
        transaction.addToBackStack(null);

        transaction.commit();
    }

}
