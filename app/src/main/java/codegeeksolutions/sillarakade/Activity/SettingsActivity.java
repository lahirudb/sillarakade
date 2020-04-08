package codegeeksolutions.sillarakade.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import codegeeksolutions.sillarakade.Helper.Constants;
import codegeeksolutions.sillarakade.Helper.LocalStorageHelper;
import codegeeksolutions.sillarakade.R;

public class SettingsActivity extends AppCompatActivity {

    private String TAG = "SettingsActivity";

    RadioGroup radioGroup;
    RadioButton theme1, theme2, theme3;

    LocalStorageHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        helper = new LocalStorageHelper();

        initializeElements();

        setSelectedTheme();
    }

    private void setSelectedTheme() {

        if (helper.getDataFromLocalStorage(Constants.LOCALSTORAGE_THEME_KEY, getApplicationContext()) == null || helper.getDataFromLocalStorage(Constants.LOCALSTORAGE_THEME_KEY, getApplicationContext()).equals("0")) {
            theme1.setChecked(true);
        } else if (helper.getDataFromLocalStorage(Constants.LOCALSTORAGE_THEME_KEY, getApplicationContext()).equals("1")) {
            theme2.setChecked(true);
        } else if (helper.getDataFromLocalStorage(Constants.LOCALSTORAGE_THEME_KEY, getApplicationContext()).equals("2")) {
            theme3.setChecked(true);
        } else {
            theme1.setChecked(true);
        }
    }

    private void initializeElements() {
        radioGroup = findViewById(R.id.theme_radio_group);

        theme1 = findViewById(R.id.theme1);
        theme2 = findViewById(R.id.theme2);
        theme3 = findViewById(R.id.theme3);

        theme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.saveDataInLocalStorage(Constants.LOCALSTORAGE_THEME_KEY, "0", getApplicationContext());
                Log.d(TAG, "onClick: theme1");
            }
        });
        
        theme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.saveDataInLocalStorage(Constants.LOCALSTORAGE_THEME_KEY, "1", getApplicationContext());
                Log.d(TAG, "onClick: theme2");
            }
        });
        
        theme3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheme(R.style.AcentTheme);

                helper.saveDataInLocalStorage(Constants.LOCALSTORAGE_THEME_KEY, "2", getApplicationContext());
            }
        });
    }
}
