package codegeeksolutions.sillarakade.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.ArrayLinkedVariables;
import codegeeksolutions.sillarakade.Helper.Constants;
import codegeeksolutions.sillarakade.Helper.DateConversion;
import codegeeksolutions.sillarakade.Helper.Helper;
import codegeeksolutions.sillarakade.R;

public class RegisterActivity extends Activity {

    private Button register;
    private EditText username, password, confirm_password, shop_name, telephone_no, address_1, address_2, address_3;

    String usernameStr = "";
    String passwordStr = "";
    String shopName = "";
    String phone = "";
    String address = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeElements();

        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

            }
        });

    }

    private void registerUser() {

        usernameStr = "";
        passwordStr = "";
        shopName = "";
        phone = "";
        address = "";

        if (username.getText().toString().isEmpty()) {
            username.setError("Please Enter Username");
        } else {
            usernameStr = username.getText().toString();
        }

        if (password.getText().toString().length()>3 && password.getText().toString().length()<10) {
            if (password.getText().toString().equals(confirm_password.getText().toString())) {
                passwordStr = password.getText().toString();
            } else {
                confirm_password.setError("Password does not match");
            }
        } else {
            password.setError("Please check password length");
        }

        if (shop_name.getText().toString().isEmpty()) {
            shop_name.setError("Shop name can not be empty");
        } else {
            shopName = shop_name.getText().toString();
        }

        if (telephone_no.getText().toString().isEmpty()) {
            telephone_no.setError("Field is required");
        } else {
            if (telephone_no.getText().toString().length() == 10) {
                phone = telephone_no.getText().toString();
            } else {
                telephone_no.setError("Phone number is not valid");
            }
        }

        if (address_1.getText().toString().isEmpty() || address_2.getText().toString().isEmpty() || address_3.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Address Fields are requires", Toast.LENGTH_LONG).show();
        } else {
            address = address_1.getText().toString() + " , " + address_2.getText().toString() + " , " + address_3.getText().toString() + ".";
        }

        if (usernameStr != "" && passwordStr != "" && phone != "" && shopName != "" && address != "") {

            final Dialog progress = Helper.showProgress(this);
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference docref = db.collection("users")
                    .document(username.getText().toString().toLowerCase());
            docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                           Toast.makeText(getApplicationContext(), "Your username is already exist", Toast.LENGTH_LONG).show();
                            progress.cancel();

                        } else {
                            System.out.println("No such document");

                            FirebaseFirestore dbr = FirebaseFirestore.getInstance();

                            CollectionReference sillarakade = dbr.collection("users");

                            Map<String, Object> user = new HashMap<>();
                            user.put("address", address);
                            user.put("shopname", shopName);
                            user.put("username", usernameStr.toLowerCase());
                            user.put("phone_no", phone);
                            user.put("password", passwordStr);
                            user.put("created_at", DateConversion.getCurrentTimeStamp());
                            user.put("deleted_at", null);

                            sillarakade.document(usernameStr).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), Constants.MSG_REGISTER_SUCCESS, Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), Constants.MSG_REGISTER_FAIL, Toast.LENGTH_LONG).show();
                                    progress.cancel();
                                }
                            });

                        }

                    } else {
                        System.out.println("Error getting documents: " + task.getException());
                        task.getException().printStackTrace();

                        progress.cancel();
                    }
                }
            });







        }
    }

    private void registraion(String usernameStr, String passwordStr, String address, String phone, String shopName) {

    }

    private void initializeElements() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        shop_name = findViewById(R.id.shop_name);
        telephone_no = findViewById(R.id.telephone_no);
        address_1 = findViewById(R.id.address_1);
        address_2 = findViewById(R.id.address_2);
        address_3 = findViewById(R.id.address_3);
    }
}
