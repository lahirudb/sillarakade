package codegeeksolutions.sillarakade.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import codegeeksolutions.sillarakade.Helper.Constants;
import codegeeksolutions.sillarakade.Helper.Helper;
import codegeeksolutions.sillarakade.R;

public class LoginActivity extends Activity {

    String TAG = "LoginActivity";

    TextView register_btn;
    private EditText username, password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeElements();

        clickedActions();
    }


    private void initializeElements() {
        register_btn = findViewById(R.id.register_btn);
        login = findViewById(R.id.login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }

    private void clickedActions() {

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAction();

            }
        });
    }

    private void loginAction() {


        if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            if (username.getText().toString().isEmpty()) {
                username.setError("User name is required");
            }

            if (password.getText().toString().isEmpty()) {
                password.setError("Passord is required");
            }
        } else {
            final Dialog progress = Helper.showProgress(this);
            Log.d(TAG, "loginAction: ");
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference docref = db.collection("users")
                    .document(username.getText().toString());
            docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "onComplete: firebasae");
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            try {
                                JSONObject jo = new JSONObject(document.getData());

                                String pwd = jo.getString("password");

                                if (pwd.equals(password.getText().toString())) {

                                    Toast.makeText(getApplicationContext(), Constants.MSG_LOGIN_SUCCESS, Toast.LENGTH_LONG).show();

                                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                    intent.putExtra("userObject", jo.toString());
                                    startActivity(intent);
                                    progress.cancel();
                                    finish();
                                } else {
                                    progress.cancel();
                                    Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
                                    username.setText("");
                                    password.setText("");
                                }
                                System.out.println("DocumentSnapshot data: " + document.getData());


                            } catch (Exception e) {
                                e.printStackTrace();
                                progress.cancel();
                            }

                        } else {
                            System.out.println("No such document");
                            Toast.makeText(getApplicationContext(), Constants.MSG_LOGIN_FAIL, Toast.LENGTH_LONG).show();
                            progress.cancel();
                        }

                    } else {
                        Log.d(TAG, "username password not match: ");
                        System.out.println("Error getting documents: " + task.getException());
                        progress.cancel();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: ");
                    progress.cancel();
                }
            });
        }

    }
}
