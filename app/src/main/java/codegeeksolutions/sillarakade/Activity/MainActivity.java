package codegeeksolutions.sillarakade.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import codegeeksolutions.sillarakade.Fragments.CreateItems;
import codegeeksolutions.sillarakade.Fragments.FromAgent;
import codegeeksolutions.sillarakade.Fragments.SellItems;
import codegeeksolutions.sillarakade.Helper.Constants;
import codegeeksolutions.sillarakade.Helper.DateConversion;
import codegeeksolutions.sillarakade.Helper.Helper;
import codegeeksolutions.sillarakade.Helper.LocalStorageHelper;
import codegeeksolutions.sillarakade.Models.ItemCatergory;
import codegeeksolutions.sillarakade.Models.StockData;
import codegeeksolutions.sillarakade.R;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    public FloatingActionButton add_orders, sales, floating_action;
    ImageView menuDrawer;
    DrawerLayout drawerLayout;
    NavigationView nav_view;
    FrameLayout fragment_container;
    Toolbar toolbar;
    AppBarLayout app_bar;

    private static boolean floating_clicked = false;
    private static MainActivity mainActivity;

    private String userObject = "";
    public String userName = "";

    private List<StockData> listData = new ArrayList<>();
    private List<ItemCatergory> catergoriesWithItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkAndSetTheme();

        checkUSerObj();

        setContentView(R.layout.activity_main);
        menuDrawerIntialize();

        initializeElements();

        clickedActions();

        mainActivity = this;
    }

    @SuppressLint("RestrictedApi")
    public void floatingVisibilityGone() {
        floating_action.setVisibility(View.GONE);
    }

    @SuppressLint("RestrictedApi")
    public void visibilityView() {
        floating_action.setVisibility(View.VISIBLE);
    }

    public static MainActivity getSingletonMainActivity() {
        return mainActivity;
    }

    private void checkAndSetTheme() {

        app_bar = findViewById(R.id.app_bar);

       // SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOCALSTORAGE_KEY, MODE_PRIVATE);
        String theme = new LocalStorageHelper().getDataFromLocalStorage(Constants.LOCALSTORAGE_THEME_KEY, this);//sharedPreferences.getString(Constants.LOCALSTORAGE_THEME_KEY, "");

        if (theme != null && !theme.equals("")) {
            int theme_code = Integer.parseInt(theme);

            if (theme_code == 0) {
                setTheme(R.style.blueTheme);
            } else if (theme_code == 1) {
                setTheme(R.style.AppTheme);
            } else if (theme_code == 2) {
                setTheme(R.style.AcentTheme);
            }
        } else {
            setTheme(R.style.blueTheme);
            //toolbar.set
        }

    }

    private void checkUSerObj() {

        try {
            Intent intent = getIntent();

            if (intent.getStringExtra("userObject") != null && !intent.getStringExtra("userObject").equals("")) {

                LocalStorageHelper helper = new LocalStorageHelper();
                helper.saveDataInLocalStorage(Constants.LOCALSTORAGE_UEROBJECT, intent.getStringExtra("userObject").toString(), this);

                JSONObject json = new JSONObject(intent.getStringExtra("userObject").toString());

                userName = json.getString("username");

            } else {
                //SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOCALSTORAGE_KEY, MODE_PRIVATE);
                String userobj = new LocalStorageHelper().getDataFromLocalStorage(Constants.LOCALSTORAGE_UEROBJECT, this);//sharedPreferences.getString(Constants.LOCALSTORAGE_UEROBJECT, "");

                if (userobj != null && !userobj.equals("")) {

                    JSONObject json = new JSONObject(userobj);
                    Log.d(TAG, "checkUSerObj: " + json.toString());

                    Log.d(TAG, "checkUSername: " + json.getString("username"));

                    userName = json.getString("username").toString();

                    Log.d(TAG, "checkUSerObjMainActivity: " + MainActivity.getSingletonMainActivity().userName);

                } else {
                    Intent intent_login = new Intent(MainActivity.this,LoginActivity.class);

                    startActivity(intent_login);

                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void menuDrawerIntialize() {

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);


        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_view);

        View view = getSupportActionBar().getCustomView();

        menuDrawer = view.findViewById(R.id.menu_drawer);


        //navigation menu view
        nav_view = findViewById(R.id.nav_view);

        MenuItem additem = nav_view.getMenu().findItem(R.id.add_item);

        additem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                System.out.println("item clicked");

                Helper.loadFragment(mainActivity, new CreateItems());

                getSupportActionBar().setTitle("Fragment");

                drawerLayout.closeDrawer(Gravity.LEFT);
                return false;
            }
        });

        MenuItem getFromAgent = nav_view.getMenu().findItem(R.id.get_from_agent);

        getFromAgent.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.d(TAG, "onMenuItemClick: get from agent");
                Helper.loadFragment(mainActivity, new FromAgent());
                drawerLayout.closeDrawer(Gravity.LEFT);
                return false;
            }
        });

        nav_view.getMenu().findItem(R.id.sell_items).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Helper.loadFragment(mainActivity, new SellItems());
                drawerLayout.closeDrawer(Gravity.LEFT);
                return false;
            }
        });

        nav_view.getMenu().findItem(R.id.settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intent, 100);

                return true;
            }
        });

        nav_view.getMenu().findItem(R.id.logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                logoutAction();
                return false;
            }
        });
    }

    private void logoutAction() {
        try {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sillarakade", MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();


            Intent intent = new Intent(this, SplashScreenActivity.class);
            startActivity(intent);



            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("RestrictedApi")
    private void initializeElements() {
        floating_action = findViewById(R.id.floatinc_action_btn);

        sales = findViewById(R.id.sales);
        this.sales.setVisibility(View.INVISIBLE);
        add_orders = findViewById(R.id.add_orders);
        add_orders.setVisibility(View.INVISIBLE);

        fragment_container = findViewById(R.id.fragment_container);
    }

    private void clickedActions() {

        floating_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//                Map<String, Object> user = new HashMap<>();
//                user.put("address", "Ada");
//                user.put("shopname", "Lovelace");
//                user.put("username", "aa");
//
//                db.collection("users")
//                        .add(user)
//                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                System.out.println("docc added");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                System.out.println("docc failed");
//                            }
//                        });
                //PreferenceManager fm = getSupportFragmentManager().beginTransaction();
                floating_action();
            }
        });



        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("time stamp - " + DateConversion.getCurrentTimeStamp());
            }
        });

        menuDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(Gravity.LEFT);

            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void floating_action() {
        if (floating_clicked) {
            floating_clicked = false;
            add_orders.setVisibility(View.INVISIBLE);
            sales.setVisibility(View.INVISIBLE);
        } else {
            floating_clicked = true;
            add_orders.setVisibility(View.VISIBLE);
            sales.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        getCatergoryData();
        get_available_stocks();
    }

    private void stockListener() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Stock").document();
    }

    private void  get_available_stocks() {
        listData = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Stock").whereEqualTo("user", userName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size()>0) {
                    try {
                        Log.d(TAG, "onComplete stock : "+ queryDocumentSnapshots.getDocuments().toString());
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        if (list != null && list.size()>0) {
                            for (DocumentSnapshot doc: list) {
                                try {
                                    StockData stockData = new StockData();
                                    stockData.setCatergory(doc.getString("catergory"));
                                    stockData.setDate(doc.getString("date"));
                                    stockData.setItem_name(doc.getString("item_name"));
                                    stockData.setDate_time(doc.getTimestamp("date_time").toString());
                                    stockData.setUser(doc.getString("user"));
                                    stockData.setSale_price(doc.getDouble("sale_price"));
                                    stockData.setPurchased_units(doc.getDouble("purchased_units"));
                                    stockData.setPurchased_price(doc.getDouble("purchased_price"));

                                    listData.add(stockData);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (listData.size()>0) {

                                Collections.sort(listData, new Comparator<StockData>() {
                                    @Override
                                    public int compare(StockData stockData, StockData t1) {
                                        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                                        Date d1 = null;
                                        Date d2 = null;
                                        try {
                                            d1 = sd.parse(stockData.getDate());
                                            d2 = sd.parse(t1.getDate());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        return d1.compareTo(d2);
                                    }
                                });

                                save_stockdata_in_local();
                            }
                            Log.d(TAG, "onComplete list data size : " + listData.size());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void save_stockdata_in_local() {
        Log.d(TAG, "save_stockdata_in_local: ");
        LocalStorageHelper helper = new LocalStorageHelper();
        helper.saveDataInLocalStorage(Constants.LOCALSTORAGE_STOCKDATA, new Gson().toJson(listData).toString(), this);
    }

    private void getCatergoryData() {

        catergoriesWithItems = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Items").whereEqualTo("user", MainActivity.getSingletonMainActivity().userName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess: collection group");



                // getItemData();
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isComplete()) {

                    Log.d(TAG, "onComplete data cat: "+ task.getResult().getDocuments().toString());
                    if (task.getResult().getDocuments() != null && task.getResult().getDocuments().size()>0) {
                        ItemCatergory cat_initial = new ItemCatergory();
                        cat_initial.setCatergoryName("All");
                        cat_initial.setCatergory_selected(true);
                        catergoriesWithItems.add(cat_initial);

                        for (DocumentSnapshot doc: task.getResult().getDocuments()) {

                            ItemCatergory cat = new ItemCatergory();
                            cat.setCatergoryName(doc.get("catergory_name").toString());
                            catergoriesWithItems.add(cat);

                        }
                        Log.d(TAG, "onComplete cat to str : " + catergoriesWithItems.toString());
                        saveCatergoryInLocal(catergoriesWithItems);
                    }

                }

            }
        });
    }

    private void saveCatergoryInLocal(List<ItemCatergory> list) {
        LocalStorageHelper localStorageHelper = new LocalStorageHelper();
        localStorageHelper.saveDataInLocalStorage(Constants.LOCALSTORAGE_CATERGORYDATA, new Gson().toJson(list), this);
    }
}
