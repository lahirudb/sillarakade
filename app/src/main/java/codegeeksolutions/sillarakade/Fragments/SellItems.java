package codegeeksolutions.sillarakade.Fragments;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import codegeeksolutions.sillarakade.Activity.MainActivity;
import codegeeksolutions.sillarakade.Helper.Constants;
import codegeeksolutions.sillarakade.Helper.DateConversion;
import codegeeksolutions.sillarakade.Helper.FireBaseManager;
import codegeeksolutions.sillarakade.Helper.Helper;
import codegeeksolutions.sillarakade.Helper.KeyboardHelper;
import codegeeksolutions.sillarakade.Helper.LocalStorageHelper;
import codegeeksolutions.sillarakade.Interfaces.HideKeyboard;
import codegeeksolutions.sillarakade.Models.ItemCatergory;
import codegeeksolutions.sillarakade.Models.ItemDesc;
import codegeeksolutions.sillarakade.Models.SellingData;
import codegeeksolutions.sillarakade.Models.StockData;
import codegeeksolutions.sillarakade.R;

public class SellItems extends Fragment {
    private String TAG = "SellItems : ";

    private View view;
    private Context mcontext;
    private Dialog progress;
    private Resources resources;
    private Gson gson;

    private List<ItemDesc> all_items = new ArrayList<>();
    private List<String> catergories = new ArrayList<>();
    private List<ItemCatergory> catergoriesWithItems = new ArrayList<>();
    private List<String> catCheck = new ArrayList<>();
    private List<StockData> stockData = new ArrayList<>();
    private List<SellingData> cart = new ArrayList<>();


    private EditText itemSearch;
    private RecyclerView itemRecyclerView;
    private Button btn;
    private RecyclerView catergory_list_recycle;
    private ItemAdapter itemAdapter;

    private int x=0;

    private CatergoryAdapter catergoryAdapter;
    private PopupWindow popupWindow;
    private Timer timer1;
    private Timer timer2;

    private float preValue = 0;

    private double availableItemQuantity = 0;
    private double stockSoldUnits = 0;
    private double stockPurchasedUnits = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sell_items, container, false);

        progress = Helper.showProgress(getActivity());

        mcontext = getActivity().getApplicationContext();

        gson = new Gson();

        resources = getActivity().getApplicationContext().getResources();

        MainActivity.getSingletonMainActivity().floatingVisibilityGone();

        initialize();

        getCatergoryData();
//        ItemGetAsync get = new ItemGetAsync();
//        get.execute();


        clickActions();

        return view;
    }

    private void clickActions() {

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore dbi = FirebaseFirestore.getInstance();
                CollectionReference colref = dbi.collection("Stock");

                colref
//                .whereEqualTo("item_name", itemDesc.getName())
//                                                .whereEqualTo("catergory", itemDesc.getCatergory())
                        .whereEqualTo("user", MainActivity.getSingletonMainActivity().userName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "query : " + task.getResult().getDocuments());
                                }
                            }
                        });
            }
        });


//        Query query = colref.whereEqualTo("item_name", itemDesc.getName());
//        Query query1 = colref.whereEqualTo("catergory", itemDesc.getCatergory());
//        Query query2 = colref.whereEqualTo("user", MainActivity.getSingletonMainActivity().userName);


    }

    private void initialize() {
        itemRecyclerView = view.findViewById(R.id.item_list_recycle);
        itemSearch = view.findViewById(R.id.itemSearch);
        btn = view.findViewById(R.id.add_item_btn);

        catergory_list_recycle = view.findViewById(R.id.catergory_list_recycle);
    }

    private void getCatergoryData() {


        try {
            LocalStorageHelper localStorageHelper = new LocalStorageHelper();
            String catergoryData = localStorageHelper.getDataFromLocalStorage(Constants.LOCALSTORAGE_CATERGORYDATA, getActivity());

            Log.d(TAG, "getCatergoryData: " + catergoryData);

            catergoriesWithItems = gson.fromJson(catergoryData, new TypeToken<List<ItemCatergory>>(){}.getType());

            if (catergoriesWithItems.size()>0) {
                for (x=0; x<catergoriesWithItems.size(); x++) {
                    catCheck.add("empty");
                    getItemData(catergoriesWithItems.get(x).getCatergoryName());
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getCatergoryData: ", e);
        }
    }

    private void getItemData(final String catName) {

//        for (x=0; x<catergoriesWithItems.size(); x++) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Items").document(MainActivity.getSingletonMainActivity().userName + "_" + catName).collection("items_sub").whereEqualTo("user", MainActivity.getSingletonMainActivity().userName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    try {
                        Log.d(TAG, "onSuccess item: collection group");
                        List<ItemDesc> itemList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                            try {
                                Log.d(TAG, "onSuccess item dataa : " + doc.getData());
                                ItemDesc item = new ItemDesc();
                                item.setAvailableUnits(doc.getData().get("availableQuantity").toString().equals("")?0:Double.parseDouble(doc.getData().get("availableQuantity").toString()));
                                item.setCatergory(doc.getData().get("catergory").toString());
                                item.setName(doc.getData().get("item_name").toString());
                                item.setUnit(doc.getData().get("unit").toString());

                                itemList.add(item);
                                all_items.add(item);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        for (int j=0; j<catergoriesWithItems.size(); j++) {
                            if (itemList.size()>0) {
                                if (itemList.get(0).getCatergory().equals(catergoriesWithItems.get(j).getCatergoryName())) {
                                    catergoriesWithItems.get(j).setItems(itemList);
                                    break;
                                }
                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    try {
                        Log.d(TAG, "onComplete: itemsssss " + task.getResult().getDocuments());
                        Log.d(TAG, "getItemData: cat name - " + catName);
                        if (task.getResult().getDocuments().size()>0) {
                            for (int i=0; i<catergoriesWithItems.size(); i++) {
                                Log.d(TAG, "on cat data : " + catergoriesWithItems.get(i).getCatergoryName() + " - " + task.getResult().getDocuments().size());
                                Log.d(TAG, "catergory item data : " + task.getResult().getDocuments().get(0).get("catergory").toString() + " - " + catergoriesWithItems.get(i));
                                    if (task.getResult().getDocuments().get(0).get("catergory").toString().equals(catergoriesWithItems.get(i).getCatergoryName())) {
                                        catCheck.set(i, "ok");
                                        break;
                                    }

                            }
                        } else {
                            for (int i=0; i<catergoriesWithItems.size(); i++) {
                                if (catergoriesWithItems.get(i).getCatergoryName().equals(catName)) {
                                    catCheck.set(i, "empty");
                                    break;
                                }
                            }
                        }

                        boolean canSetRecycle = true;
                        for (int y=0; y<catCheck.size(); y++) {
                            Log.d(TAG, "on cat check : " + catCheck.get(y));
                            if (catCheck.get(y) != null && (!catCheck.get(y).equals("ok") && !catCheck.get(y).equals("empty"))) {
                                canSetRecycle = false;
                            }
                        }

                        if (canSetRecycle) {
                            catergoriesWithItems.get(0).setItems(all_items);
                            if (catergoriesWithItems.size()>0) {
                                setRecycleData();
                            }
                            if (progress != null)
                                progress.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    }

    private void setRecycleData() {
        Log.d(TAG, "setRecycleData: ");
        LinearLayoutManager manager = new LinearLayoutManager(mcontext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);

        catergory_list_recycle.setLayoutManager(manager);

        catergoryAdapter = new CatergoryAdapter(catergoriesWithItems);

        catergory_list_recycle.setAdapter(catergoryAdapter);

        catergoryAdapter.notifyDataSetChanged();


    }

    private class CatergoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ItemCatergory> catergoryList;
        public CatergoryAdapter(List<ItemCatergory> catergoryList) {
            this.catergoryList = catergoryList;
        }

        private class CatergoryViewHolder extends RecyclerView.ViewHolder {

            private TextView catergory_name;
            private ImageView catergory_icon;
            private LinearLayout catergory_layout;

            public CatergoryViewHolder(@NonNull View itemView) {
                super(itemView);
                catergory_name = itemView.findViewById(R.id.catergory_name);
                catergory_icon = itemView.findViewById(R.id.catergory_icon);
                catergory_layout = itemView.findViewById(R.id.catergory_layout);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View catergoryview = LayoutInflater.from(mcontext).inflate(R.layout.adapter_sell_items_catergory, parent, false);
            return new CatergoryViewHolder(catergoryview);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            try {


                //Log.d(TAG, "onBindViewHolder catergory : " + catergoryList.get(position).getItems().size());
                final CatergoryViewHolder itemholder = (CatergoryViewHolder) holder;

                itemholder.catergory_name.setText(catergoryList.get(position).getCatergoryName());

                itemholder.catergory_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i=0; i<catergoriesWithItems.size(); i++) {
                            catergoriesWithItems.get(i).setCatergory_selected(false);
                        }
                        if (!catergoriesWithItems.get(position).isCatergory_selected()) {

                            catergoriesWithItems.get(position).setCatergory_selected(true);
                        } else {
                            catergoriesWithItems.get(position).setCatergory_selected(false);
                        }
                        catergoryAdapter.notifyDataSetChanged();
                        if (itemAdapter != null) {
                            itemAdapter.notifyDataSetChanged();
                        }
                    }
                });

                if (catergoriesWithItems.get(position).isCatergory_selected()) {
                    itemholder.catergory_layout.setBackgroundColor(resources.getColor(R.color.colorBlue));

                    GridLayoutManager grid = new GridLayoutManager(mcontext, 3);
                    itemRecyclerView.setLayoutManager(grid);

                    itemAdapter = new ItemAdapter(catergoriesWithItems.get(position).getItems(), position);

                    itemRecyclerView.setAdapter(itemAdapter);

                    itemAdapter.notifyDataSetChanged();
                } else {
                    itemholder.catergory_layout.setBackgroundColor(resources.getColor(R.color.colorWhite));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return catergoryList.size();
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ItemDesc> itemList = new ArrayList<>();
        int catergory_position;
        public ItemAdapter(List<ItemDesc> itemList, int catergory_position) {
            if (itemList != null) {
                this.itemList = itemList;
            }
            this.catergory_position = catergory_position;
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder {
            private TextView item_name, price;//, last_ordered_units, available_units, last_order_date;
            private ImageView item_icon;
            private LinearLayout item_layout;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                item_name = itemView.findViewById(R.id.item_name);
                item_icon = itemView.findViewById(R.id.item_icon);
                price = itemView.findViewById(R.id.price);
                item_layout = itemView.findViewById(R.id.item_layout);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.adapter_sell_items_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            try {
                final ItemViewHolder itemView = (ItemViewHolder) holder;

                itemView.item_name.setText(itemList.get(position).getName());
                Log.d(TAG, "onBindViewHolder item : " + itemList.get(position).getName());
                itemView.price.setText("Rs " + itemList.get(position).getUnitPrice());

                itemView.item_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openItemDetailPopup(itemList.get(position));
                    }
                });

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        LocalStorageHelper helper = new LocalStorageHelper();
                        String dataStr = helper.getDataFromLocalStorage(Constants.LOCALSTORAGE_STOCKDATA, mcontext);
                        List<StockData> stockList = gson.fromJson(dataStr, new TypeToken<List<StockData>>(){}.getType());
                        List<StockData> item_stockList = new ArrayList<>();
                        for (StockData sd: stockList) {
                            if (sd.getItem_name().equals(itemList.get(position).getName())){
                                if (sd.getPurchased_units()-sd.getSold_units() >0) {
                                    item_stockList.add(sd);
                                }

                            }
                        }
                        if (item_stockList.size()>0) {
                            itemView.item_layout.setBackgroundColor(resources.getColor(R.color.colorWhite));
                        } else {
                            itemView.item_layout.setBackgroundColor(resources.getColor(R.color.colorBlue));
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }

    private void openItemDetailPopup(final ItemDesc item) {
        try {
            preValue = 0;
            getStockData();

            popupWindow = new PopupWindow(getActivity());
            LinearLayout layout = new LinearLayout(getActivity());

            View view = LayoutInflater.from(mcontext).inflate(R.layout.sell_item_detail_view, null);
            popupWindow.setContentView(view);

            //layout components
            LinearLayout catergory_layout = view.findViewById(R.id.catergory_layout);
            RecyclerView stockListRecycle = view.findViewById(R.id.stock_data);
            TextView no_stock_data = view.findViewById(R.id.no_stock_data);
            LinearLayout stock_data_layout = view.findViewById(R.id.stock_data_layout);
            TextView item_name = view.findViewById(R.id.item_name);
            ImageView item_image = view.findViewById(R.id.item_image);

            final TextView selected_stock = view.findViewById(R.id.selected_stock);
            final EditText total_price = view.findViewById(R.id.total_price);
            final EditText selling_price_input = view.findViewById(R.id.selling_price_input);
            final EditText unit_count = view.findViewById(R.id.unit_count);

            Button sell = view.findViewById(R.id.sell);
            Button add_to_cart = view.findViewById(R.id.add_to_cart);

            popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);

            popupWindow.showAtLocation(layout, Gravity.CENTER, 400, 400);

            popupWindow.setOutsideTouchable(true);

            popupWindow.setFocusable(true);
            popupWindow.update();

            catergory_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int event = motionEvent.getAction();
                    Log.d(TAG, "onTouch outer : " +  motionEvent.getAction());
                    Log.d(TAG, "onTouch: historic size - " + motionEvent.getHistorySize());

                    for (int x=0; x<motionEvent.getHistorySize(); x++) {
                        Log.d(TAG, "onTouch event history : " + x + " - " + motionEvent.getHistoricalY(x));
                        if(preValue == 0) {
                            preValue = motionEvent.getHistoricalY(x);
                        }
                        if (preValue<motionEvent.getHistoricalY(x) && preValue>0) {
                            preValue = motionEvent.getHistoricalY(x);
                        }
                    }

                    switch (event) {
                        case MotionEvent.ACTION_UP:
                            Log.d(TAG, "onTouch: action up");
                            Log.d(TAG, "onTouch: up Y - " + motionEvent.getY());
                            Log.d(TAG, "onTouch pre value : " + preValue);
                            float lastValue = motionEvent.getY();
                            float distance = preValue-lastValue;
                            float compareViewHeight = view.getHeight()/distance;
                            int reslt = (int) compareViewHeight;
                            preValue = 0;
                            if (reslt<5) {
                                popupWindow.dismiss();
                            }
                            Log.d(TAG, "onTouch: result = " + reslt);
                            Log.d(TAG, "onTouch view height : " + view.getHeight());
                            break;

                    }
                    return true;
                }
            });

            //data binding
            item_name.setText(item.getName());

            LocalStorageHelper helper = new LocalStorageHelper();
            String dataStr = helper.getDataFromLocalStorage(Constants.LOCALSTORAGE_STOCKDATA, mcontext);
            List<StockData> stockList = gson.fromJson(dataStr, new TypeToken<List<StockData>>(){}.getType());
            final List<StockData> item_stockList = new ArrayList<>();
            for (StockData sd: stockList) {
                if (sd.getItem_name().equals(item.getName())){
                    if (sd.getPurchased_units()-sd.getSold_units() >0) {
                        item_stockList.add(sd);
                    }

                }
            }

            if (item_stockList.size()>0) {
                no_stock_data.setVisibility(View.GONE);
                stock_data_layout.setVisibility(View.VISIBLE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mcontext);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                stockListRecycle.setLayoutManager(linearLayoutManager);

                //set first item seleted
                item_stockList.get(0).setSelected(true);

                StockAdapter adapter = new StockAdapter(item_stockList, selected_stock, selling_price_input);
                stockListRecycle.setAdapter(adapter);

                adapter.notifyDataSetChanged();

                selling_price_input.setText(String.valueOf(item_stockList.get(0).getSale_price()));


            } else {
                no_stock_data.setVisibility(View.VISIBLE);
                stock_data_layout.setVisibility(View.GONE);
            }

            selling_price_input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString() != null && !editable.toString().equals("") && Double.parseDouble(editable.toString())>0 && !unit_count.getText().toString().equals("")) {
                        Log.d(TAG, "afterTextChanged: " + editable.toString());
                        Log.d(TAG, "afterTextChanged: " + unit_count.getText().toString());
                        String total =  String.valueOf(Double.parseDouble(editable.toString()) * Double.parseDouble(unit_count.getText().toString()));
                        total_price.setText(total);
                    }

//                    timer1 = new Timer();
//                    timer1.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
//                        }
//                    }, 5000);
                }
            });

            unit_count.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString() != null && !editable.toString().equals("") && Double.parseDouble(editable.toString())>0 && !selling_price_input.getText().toString().equals("")) {
                        String total =  String.valueOf(Double.parseDouble(editable.toString()) * Double.parseDouble(selling_price_input.getText().toString()));
                        total_price.setText(total);
                    }

//                    timer2 = new Timer();
//                    timer2.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            HideKeyboard keyboard = new KeyboardHelper(getView(), getActivity());
//                            keyboard.hideSofKeybiard();
//                        }
//                    }, 5000);
                }
            });

            sell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!unit_count.getText().toString().equals("")) {
                        progress = Helper.showProgress(getActivity());

                        double aq = 0;
                        if (availableItemQuantity == item.getAvailableUnits() ) {
                            aq = availableItemQuantity;
                        } else if (availableItemQuantity != 0 && availableItemQuantity<item.getAvailableUnits()) {
                            aq = availableItemQuantity;
                        }
                        aq = aq-Double.parseDouble(unit_count.getText().toString());

                        sellindividualItem(item.getName(), item.getCatergory(),
                               aq, selected_stock.getText().toString(), Double.parseDouble(unit_count.getText().toString()));
                    }
                }
            });

            add_to_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (unit_count.getText().toString().equals("")) {
                            Helper.showSnackBar(mcontext, resources.getString(R.string.units_empty_item_details));
                        } else {
                            SellingData sellingddata = new SellingData();
                            sellingddata.setItemName(item.getName());
                            StockData sddd = null;
                            for (StockData sd: item_stockList) {
                                if (sd.isSelected()) {
                                    sddd = sd;
                                    break;
                                }
                            }
                            String stockid = MainActivity.getSingletonMainActivity().userName + "_" + item.getName() + "_" + sddd.getDate();
                            sellingddata.setStockDocId(stockid);

                            sellingddata.setOriginal_price(sddd.getSale_price());
                            sellingddata.setSold_price(Double.parseDouble(selling_price_input.getText().toString()));
                            sellingddata.setUnits(Double.parseDouble(unit_count.getText().toString()));
                            sellingddata.setTotalprice(sellingddata.getUnits() * sellingddata.getSold_price());

                            cart.add(sellingddata);

                            Helper.showSnackBar(mcontext, resources.getString(R.string.added_to_cart_item_details));
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                        Helper.showSnackBar(mcontext, resources.getString(R.string.adding_to_cart_failed_item_details));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Helper.showSnackBar(mcontext, resources.getString(R.string.adding_to_cart_failed_item_details));
                    }

                }
            });
            firestoreListeners(item);

            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (timer1 != null) {
                        timer1.cancel();
                        timer1.purge();
                    }

                    if (timer2 != null) {
                        timer2.cancel();
                        timer2.purge();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void firestoreListeners(ItemDesc item) {
        //listener
        availableItemQuantity = 0;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String item_docname = MainActivity.getSingletonMainActivity().userName + "_" + item.getCatergory();

        String itemsub_docname = item.getCatergory() + "_" + item.getName();

        DocumentReference docResfItem = db.collection("Items").document(item_docname).collection("items_sub").document(itemsub_docname);
        docResfItem.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                try {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    String source = documentSnapshot != null && documentSnapshot.getMetadata().hasPendingWrites()
                            ? "Local" : "Server";

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Log.d(TAG, source + " data fgdfgf: " + documentSnapshot.getData());
                        Map<String, Object> listenData = documentSnapshot.getData();
                        Log.d(TAG, "onEvent: " + listenData.get("availableQuantity"));
                        availableItemQuantity = Double.parseDouble(listenData.get("availableQuantity").toString());
                    } else {
                        Log.d(TAG, source + " data: null");
                        availableItemQuantity = 0;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    availableItemQuantity = 0;
                }


            }
        });

    }

    private void stockListener(String itemname, String stockdate) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String stock_docname = MainActivity.getSingletonMainActivity().userName + "_" + itemname + "_" + stockdate;

        DocumentReference docResfItem = db.collection("Stock").document(stock_docname);
        docResfItem.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                String source = documentSnapshot != null && documentSnapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, source + " data fgdfgf: " + documentSnapshot.getData());
                    Map<String, Object> mapp = documentSnapshot.getData();
                    stockSoldUnits = Double.parseDouble(mapp.get("sold_units").toString());
                    stockPurchasedUnits = Double.parseDouble(mapp.get("purchased_units").toString());
                } else {
                    Log.d(TAG, source + " data: null");
                }

            }
        });
    }

    private void sellindividualItem(final String itemname, String catergoryname, double available_quantity, String stock_date, final double sellingUnits) {
        try {

            FirebaseFirestore db = FirebaseFirestore.getInstance();


            WriteBatch batch = db.batch();

            String item_docname = MainActivity.getSingletonMainActivity().userName + "_" + catergoryname;

            String itemsub_docname = catergoryname + "_" + itemname;

            DocumentReference docResfItem = db.collection("Items").document(item_docname).collection("items_sub").document(itemsub_docname);

            Log.d(TAG, "saveStockData: " + itemname + " - " + catergoryname);

            String stock_doc_name = MainActivity.getSingletonMainActivity().userName + "_" + itemname + "_" + stock_date;
            DocumentReference stockDocRef = db.collection("Stock").document(stock_doc_name);
            if (stockPurchasedUnits>= (sellingUnits + stockSoldUnits)) {
                batch.update(stockDocRef, "sold_units", sellingUnits + stockSoldUnits);

                batch.update(docResfItem, "availableQuantity", available_quantity);

                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        try {
                            if (task.isComplete()) {

                                Log.d(TAG, "onComplete: task complete - " + task.getResult());
                                Helper.showSnackBar(mcontext, resources.getString(R.string.individual_item_sell, String.valueOf(sellingUnits), itemname));
                                popupWindow.dismiss();
                            } else {
                                Log.d(TAG, "onComplete: task not complete");
                            }
                            if (progress != null) {
                                progress.cancel();
                            }
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                        if (progress != null) {
                            progress.cancel();
                        }
                    }
                });
            } else {
                String sell = String.valueOf(stockPurchasedUnits-stockSoldUnits);
                Helper.showSnackBar(mcontext, resources.getString(R.string.sell_limited_items_from_stock, sell));
                if (progress != null) {
                    progress.dismiss();
                }
            }


            //Log.d(TAG, "sellindividualItem: " + docResfItem.get().getResult().getDouble("availableQuantity").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class StockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<StockData> stocklist;
        TextView selectedStock;
        EditText price_per_unit;
        public StockAdapter(List<StockData> list, TextView selectedStock, EditText price_per_unit) {
            this.stocklist = list;
            this.selectedStock = selectedStock;
            this.price_per_unit = price_per_unit;
        }

        private class StockViewHolder extends RecyclerView.ViewHolder {
            TextView stock_date, selling_price, available_units, note;
            LinearLayout item_layout;
            public StockViewHolder(@NonNull View itemView) {
                super(itemView);
                stock_date = itemView.findViewById(R.id.stock_date);
                selling_price = itemView.findViewById(R.id.selling_price);
                available_units = itemView.findViewById(R.id.available_units);
                note = itemView.findViewById(R.id.note);

                item_layout = itemView.findViewById(R.id.item_layout);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.adapter_sell_items_stocklist_item, parent, false);
            return new StockViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            StockViewHolder viewHolder = (StockViewHolder) holder;

            try {
                viewHolder.stock_date.setText(stocklist.get(position).getDate());
                String availableUnits = String.valueOf(stocklist.get(position).getPurchased_units() - stocklist.get(position).getSold_units());
                viewHolder.available_units.setText(availableUnits);
                viewHolder.selling_price.setText(String.valueOf(stocklist.get(position).getSale_price()));
                viewHolder.note.setText("");

                if (stocklist.get(position).isSelected()) {
                    viewHolder.item_layout.setBackgroundColor(resources.getColor(R.color.colorGrey));

                    price_per_unit.setText(String.valueOf(stocklist.get(position).getSale_price()));
                    selectedStock.setText(stocklist.get(position).getDate());


                    stockListener(stocklist.get(position).getItem_name(), stocklist.get(position).getDate());
                } else {
                    viewHolder.item_layout.setBackgroundColor(resources.getColor(R.color.colorWhite));
                }

                viewHolder.item_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (int i=0; i<stocklist.size(); i++)  {

                            stocklist.get(i).setSelected(false);

                        }

                        if (stocklist.get(position).isSelected()) {
                            stocklist.get(position).setSelected(false);
                        } else {
                            stocklist.get(position).setSelected(true);
                        }


                        notifyDataSetChanged();
                    }
                });

                Log.d(TAG, "onBindViewHolder : " + position + " - " + stocklist.get(position).isSelected());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return stocklist.size();
        }
    }

    private void getStockData() {

        LocalStorageHelper localStorageHelper = new LocalStorageHelper();
        String stocks = localStorageHelper.getDataFromLocalStorage(Constants.LOCALSTORAGE_STOCKDATA, getActivity());

        Log.d(TAG, "getCatergoryData: " + stocks);

        stockData = gson.fromJson(stocks, new TypeToken<List<StockData>>(){}.getType());


    }

    private class ItemGetAsync extends AsyncTask<Void, Void, List<String>> {


        @Override
        protected List<String> doInBackground(Void... voids) {

            final FireBaseManager manager = new FireBaseManager();

            manager.getConnectToDocument("Catergories", MainActivity.getSingletonMainActivity().userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    List<String> data = new ArrayList<>();
                    if (task.getResult().exists()) {
                        try {
                            Log.d(TAG, "onComplete: " + task.getResult().getData().toString());
                            HashMap<String, Object> withCat = new Gson().fromJson(task.getResult().getData().toString(), HashMap.class);
//                    HashMap<String, Object> db = new Gson().fromJson(task.getResult().getData().toString(), HashMap.class);
//                    Log.d(TAG, "onComplete map with class : " + db.toString());
                            catergories = new ArrayList<>(withCat.keySet());
//                       data =  new ArrayList<>(withCat.keySet());

                            Log.d(TAG, "onComplete async : " + catergories.toString());

                            //getItemData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    //return data;
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: ");
                    if (progress != null)
                        progress.cancel();

                    //eturn null;
                }
            });
            Log.d(TAG, "doInBackground return : ");
            return catergories;

        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            Log.d(TAG, "onPostExecute: ");

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.getSingletonMainActivity().visibilityView();
    }
}


