package codegeeksolutions.sillarakade.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.facebook.stetho.json.ObjectMapper;
import com.google.android.gms.common.data.ObjectDataBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ObjectArrays;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import codegeeksolutions.sillarakade.Activity.MainActivity;
import codegeeksolutions.sillarakade.BuildConfig;
import codegeeksolutions.sillarakade.Helper.DateConversion;
import codegeeksolutions.sillarakade.Helper.FireBaseManager;
import codegeeksolutions.sillarakade.Helper.Helper;
import codegeeksolutions.sillarakade.Models.DbItemData;
import codegeeksolutions.sillarakade.R;

public class CreateItems extends Fragment {
    private String TAG = "CreateItemsFragment : ";

    private View view;

    private EditText itemname, unit, price, insert_catergory, available_quantity, bought_price, selling_price, newstock_quantity;
    private TextView unit_a, unit_b;
    private SmartMaterialSpinner spinner1;
    private Button addItemBtn, add_catergory, show_catergory;
    
    private LinearLayout add_section;

    private Dialog progress;
    private Context mcontext;

    private List<String> selectedCatergoryData = new ArrayList<>();
    private HashMap <String, Object> mapFromString = new HashMap<>();
    private String selectedCatergory = "";


    Resources stringRes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_items, container, false);

        mcontext = getActivity().getApplicationContext();

        progress = Helper.showProgress(getActivity());

        initialize();

        clickActions();

        getData();
        stringRes = getActivity().getApplicationContext().getResources();

        return view;
    }

    private void initialize() {
        spinner1 = view.findViewById(R.id.spinner1);

        add_catergory = view.findViewById(R.id.add_catergory);
        show_catergory = view.findViewById(R.id.show_catergory);
        add_section = view.findViewById(R.id.add_section);
        add_section.setVisibility(View.GONE);
        insert_catergory = view.findViewById(R.id.insert_catergory);

        available_quantity = view.findViewById(R.id.available_quantity);
        bought_price = view.findViewById(R.id.bought_price);
        selling_price = view.findViewById(R.id.selling_price);
        newstock_quantity = view.findViewById(R.id.newstock_quantity);

        unit_a = view.findViewById(R.id.unit_a);
        unit_b = view.findViewById(R.id.unit_b);
        
        itemname = view.findViewById(R.id.itemname);
        unit = view.findViewById(R.id.unit);
        price = view.findViewById(R.id.price);
        addItemBtn = view.findViewById(R.id.add_item_btn);
    }

    private void clickActions() {
        
        show_catergory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_section.getVisibility() == View.GONE) {
                    Log.d(TAG, "onClick: visible");
                    add_section.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "onClick: gone");
                    add_section.setVisibility(View.GONE);
                }
            }
        });

        add_catergory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onComplete mapp : " + mapFromString.toString());
                Log.d(TAG, "onComplete spinner : " + spinner1.getSelectedItem());
                if (!Helper.isEditTextIsEmpty(insert_catergory)) {
                    progress = Helper.showProgress(getActivity());
                    try {

                        selectedCatergoryData.add(insert_catergory.getText().toString());
                        selectedCatergory = insert_catergory.getText().toString();
                        Log.d(TAG, "onClick: " + selectedCatergoryData.size());

                        mapFromString.put("catergory_name", selectedCatergory);
                        mapFromString.put("created_at", FieldValue.serverTimestamp());
                        mapFromString.put("deleted_at", "");
                        mapFromString.put("user", MainActivity.getSingletonMainActivity().userName);

                        FireBaseManager manager = new FireBaseManager();
                        manager.saveData("Items").document(MainActivity.getSingletonMainActivity().userName + "_" + selectedCatergory).set(mapFromString).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: saved");
                                Helper.showSnackBar(mcontext, stringRes.getString(R.string.catergory_added_successfully));

                                //if (selectedCatergoryData.size() == 1) {
                                spinner1.setItem(selectedCatergoryData);
                                //}
                                spinner1.setSelection(selectedCatergoryData.size()-1);

                                insert_catergory.setText("");
                                if (progress != null)
                                    progress.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: ");
                                Helper.showSnackBar(mcontext, stringRes.getString(R.string.catergory_adding_failed));
                                if (progress != null)
                                    progress.cancel();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (progress != null)
                            progress.cancel();
                    }
                }

                Log.d(TAG, "onClick add catergory: " + mapFromString.toString());
            }
        });

        itemname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (selectedCatergoryData.contains(itemname.getText().toString().toLowerCase())) {
                    itemname.setError(getActivity().getApplicationContext().getResources().getString(R.string.error_item_exist));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String data = editable.toString();
                data = data.trim();
                if (selectedCatergoryData.contains(data.toLowerCase())) {
                    itemname.setError(getActivity().getApplicationContext().getResources().getString(R.string.error_item_exist));
                }

            }
        });

        unit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!itemname.getText().toString().equals(""))
                    unit_a.setText(editable.toString().trim());
                    unit_b.setText(editable.toString().trim());
            }
        });

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                addItemFunc();
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Log.d(TAG, "onItemSelected: " + spinner1.getItemAtPosition(i));
                    selectedCatergory = spinner1.getItemAtPosition(i).toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCatergory = "";
            }
        });


    }

    private void addItemFunc() {
        Log.d(TAG, "addItemFunc: ");
        if (selectedCatergory.equals("") || itemname.getText().toString().equals("") || unit.getText().toString().equals("")) {

            Helper.showSnackBar(mcontext, stringRes.getString(R.string.fill_required_field));
        } else {

            progress = Helper.showProgress(getActivity());

            try {
                HashMap<String, String> hash = new HashMap<>();
                hash.put("unit", unit.getText().toString());

                HashMap<String, Object> neHash = new HashMap<>();
                neHash.put(itemname.getText().toString().toLowerCase(), hash);

                HashMap<String, Object> selectedCat = new HashMap<>();
                if (mapFromString.get(selectedCatergory.toLowerCase()) != null) {
                    selectedCat = new Gson().fromJson(mapFromString.get(selectedCatergory.toLowerCase()).toString(), HashMap.class);
                }

                selectedCat.put(itemname.getText().toString().toLowerCase(), hash);

                Log.d(TAG, "addItemFunc: " + neHash.toString());

                Log.d(TAG, "addItemFunc original map : " + mapFromString.toString());


                Log.d(TAG, "addItemFunc cat map : " + selectedCat.toString());

               //mapFromString.put(selectedCatergory.toLowerCase(), selectedCat);

               mapFromString.replace(selectedCatergory.toLowerCase(), selectedCat);



                Log.d(TAG, "updated original map : " + mapFromString.toString());
                addItemData();

            } catch (Exception e) {
                e.printStackTrace();
                progress.cancel();
            }
        }

    }

    private void getData() {
        Log.d(TAG, "getData: " + MainActivity.getSingletonMainActivity().userName);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Items").whereEqualTo("user", MainActivity.getSingletonMainActivity().userName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        Log.d(TAG, "onComplete dateeeeeeeee : " + task.getResult());
                        Log.d(TAG, "onComplete dateeeeeeeee : " + task.getResult().toString());
                        Log.d(TAG, "onComplete dateeeeeeeee : " + task.getResult().getDocuments());
                        Log.d(TAG, "onComplete dateeeeeeeee : " + task.getResult().getDocuments().toString());
                        //JSONArray jArr = new JSONArray(task.getResult().getDocuments().toString());
                        String dataStr = task.getResult().getDocuments().toString();
                        Log.d(TAG, "onComplete dataStr : " + dataStr);
                        if (dataStr.length()>2) {
                            selectedCatergoryData = new ArrayList<>();
                            for (QueryDocumentSnapshot doc :task.getResult()) {
                                Log.d(TAG, "onComplete get cattttt : " + doc.getData().get("catergory_name"));
    //                        HashMap<String, Object> datass = new Gson().toJson(task.getResult().toString(), HashMap.class);
                                selectedCatergoryData.add(doc.getData().get("catergory_name").toString());

                            }
                            spinner1.setItem(selectedCatergoryData);
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (progress != null)
                        progress.cancel();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progress != null)
                    progress.cancel();
            }
        });
    }

    private void addItemData() {
        try {
            FirebaseFirestore dbr = FirebaseFirestore.getInstance();
            //CollectionReference ref = dbr.collection("Items");

            HashMap<String, Object> items = new HashMap<>();
            items.put("item_name", itemname.getText().toString());
            items.put("catergory", selectedCatergory);
            items.put("availableQuantity", Double.parseDouble(available_quantity.getText().toString().equals("")?"0":available_quantity.getText().toString()));
            items.put("item_image", null);
            items.put("unit", unit.getText().toString());
            items.put("user", MainActivity.getSingletonMainActivity().userName);
            List<String> ss = new ArrayList<>();
            items.put("available_stocks", ss);

//            HashMap<String, Object> itemSet = new HashMap<>();
//            itemSet.put(itemname.getText().toString(), items);

            HashMap<String, Object> stockitemData = new HashMap<>();
            stockitemData.put("item_name", itemname.getText().toString());
            stockitemData.put("date_time", FieldValue.serverTimestamp());
            stockitemData.put("catergory", selectedCatergory);
            stockitemData.put("purchased_units", Double.parseDouble(newstock_quantity.getText().toString().equals("")?"0":newstock_quantity.getText().toString()));
            stockitemData.put("purchased_price", Double.parseDouble(bought_price.getText().toString().equals("")?"0":bought_price.getText().toString()));
            stockitemData.put("sale_price", Double.parseDouble(selling_price.getText().toString().equals("")?"0":selling_price.getText().toString()));
            stockitemData.put("date", DateConversion.getCurrentDate());
            stockitemData.put("user", MainActivity.getSingletonMainActivity().userName);

            String documentName = MainActivity.getSingletonMainActivity().userName + "_" + selectedCatergory;

            WriteBatch batch = dbr.batch();

            if (!available_quantity.getText().toString().equals("") && !newstock_quantity.getText().toString().equals("") && !bought_price.getText().toString().equals("") && !selling_price.getText().toString().equals("")) {
                String itemDocName = MainActivity.getSingletonMainActivity().userName + "_" + itemname.getText().toString() + "_" + DateConversion.getCurrentDate();
                DocumentReference stockRef = dbr.collection("Stock").document(itemDocName);
                batch.set(stockRef, stockitemData);

                //stock to item collection
                ss.add(itemDocName);
                items.put("available_stocks", ss);
            }

            DocumentReference itemRef = dbr.collection("Items").document(documentName).collection("items_sub").document(selectedCatergory + "_" + itemname.getText().toString());
            batch.set(itemRef, items);

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    available_quantity.setText("");
                    newstock_quantity.setText("");
                    bought_price.setText("");
                    selling_price.setText("");

                    spinner1.setSelected(false);
                    itemname.setText("");
                    unit.setText("");
                    price.setText("");

                    Helper.showSnackBar(mcontext, stringRes.getString(R.string.item_added_successfully));
                    progress.cancel();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Helper.showSnackBar(mcontext, stringRes.getString(R.string.item_adding_failed));
                    progress.cancel();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            progress.cancel();
        }
    }
}
