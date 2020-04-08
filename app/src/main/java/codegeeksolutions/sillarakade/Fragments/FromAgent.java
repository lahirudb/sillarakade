package codegeeksolutions.sillarakade.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import codegeeksolutions.sillarakade.Activity.MainActivity;
import codegeeksolutions.sillarakade.Helper.Constants;
import codegeeksolutions.sillarakade.Helper.DateConversion;
import codegeeksolutions.sillarakade.Helper.FireBaseManager;
import codegeeksolutions.sillarakade.Helper.Helper;
import codegeeksolutions.sillarakade.Helper.LocalStorageHelper;
import codegeeksolutions.sillarakade.Models.DbItems;
import codegeeksolutions.sillarakade.Models.ItemCatergory;
import codegeeksolutions.sillarakade.Models.ItemDesc;
import codegeeksolutions.sillarakade.Models.StockData;
import codegeeksolutions.sillarakade.R;

public class FromAgent extends Fragment {
    private String TAG = "FromAgent : ";

    private View view;
    private Context mcontext;
    private Dialog progress;
    private Gson gson;

    private List<ItemDesc> itemDescList = new ArrayList<>();
    private List<String> catergories = new ArrayList<>();
    private List<ItemCatergory> catergoriesWithItems = new ArrayList<>();
    private List<ItemCatergory> catergoriesWithItemsSearch = new ArrayList<>();
    private List<String> catCheck = new ArrayList<>();

    private EditText itemSearch;
    private RecyclerView itemRecyclerView;
    private Button btn;
    private RecyclerView catergory_list_recycle;
    private ItemAdapter itemAdapter;
    CatergoryAdapter catergoryAdapter;

    private int x=0;
    private Resources resources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_agent_items, container, false);

        progress = Helper.showProgress(getActivity());

        mcontext = getActivity().getApplicationContext();

        resources = getActivity().getApplicationContext().getResources();

        gson = new Gson();

        MainActivity.getSingletonMainActivity().floatingVisibilityGone();

        initialize();

        getCatergoryData();
//        ItemGetAsync get = new ItemGetAsync();
//        get.execute();


        clickActions();

        return view;
    }

    private void clickActions() {

        itemSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>0) {
                    catergoriesWithItemsSearch = new ArrayList<>();
                    for (int i=0; i<catergoriesWithItems.size(); i++) {
                        if (catergoriesWithItems.get(i).getCatergoryName().toLowerCase().contains(editable.toString().toLowerCase())) {
                            ItemCatergory cat = null;
                            try {
                                cat = (ItemCatergory) catergoriesWithItems.get(i).clone();
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                            if (cat != null) {
                                cat.setItemlist_visible(true);
                                catergoriesWithItemsSearch.add(cat);
                            }
                        }
                    }

                    LinearLayoutManager manager = new LinearLayoutManager(mcontext);
                    manager.setOrientation(LinearLayoutManager.VERTICAL);

                    catergory_list_recycle.setLayoutManager(manager);

                    catergoryAdapter = new CatergoryAdapter(catergoriesWithItemsSearch);

                    catergory_list_recycle.setAdapter(catergoryAdapter);

                    catergoryAdapter.notifyDataSetChanged();

                } else {
                    LinearLayoutManager manager = new LinearLayoutManager(mcontext);
                    manager.setOrientation(LinearLayoutManager.VERTICAL);

                    catergory_list_recycle.setLayoutManager(manager);

                    catergoryAdapter = new CatergoryAdapter(catergoriesWithItems);

                    catergory_list_recycle.setAdapter(catergoryAdapter);

                    catergoryAdapter.notifyDataSetChanged();
                }




            }
        });




    }

    private void initialize() {
        itemRecyclerView = view.findViewById(R.id.item_list_recycle);
        itemSearch = view.findViewById(R.id.itemSearch);
        btn = view.findViewById(R.id.add_item_btn);

        catergory_list_recycle = view.findViewById(R.id.catergory_list_recycle);
    }

    private void getCatergoryData() {

        LocalStorageHelper localStorageHelper = new LocalStorageHelper();
        String catergoryData = localStorageHelper.getDataFromLocalStorage(Constants.LOCALSTORAGE_CATERGORYDATA, getActivity());

        Log.d(TAG, "getCatergoryData: " + catergoryData);
        if (catergoryData != null && !catergoryData.equals("[]")) {
            catergoriesWithItems = gson.fromJson(catergoryData, new TypeToken<List<ItemCatergory>>(){}.getType());
            catergoriesWithItems.remove(0);
            for (x=0; x<catergoriesWithItems.size(); x++) {
                catCheck.add("");
                getItemData(catergoriesWithItems.get(x).getCatergoryName(), "");
            }
        }

    }

    private void getItemData(final String catName, String docName) {

//        for (x=0; x<catergoriesWithItems.size(); x++) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Items").document(MainActivity.getSingletonMainActivity().userName + "_" + catName).collection("items_sub").whereEqualTo("user", MainActivity.getSingletonMainActivity().userName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
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


                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                        if (catergoriesWithItems.size()>0) {
                            setRecycleData();
                        }
                        if (progress != null)
                            progress.cancel();
                    }
                }
            });

    }

    private void setRecycleData() {
        Log.d(TAG, "setRecycleData: ");
        LinearLayoutManager manager = new LinearLayoutManager(mcontext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

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

            private RecyclerView item_list_recycle;
            private TextView catergory_title;
            private LinearLayout catergory_layout;
            private ImageView arrow;

            public CatergoryViewHolder(@NonNull View itemView) {
                super(itemView);
                item_list_recycle = itemView.findViewById(R.id.item_list_recycle);
                catergory_title = itemView.findViewById(R.id.catergory_title);
                catergory_layout = itemView.findViewById(R.id.catergory_layout);
                arrow = itemView.findViewById(R.id.arrow);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View catergoryview = LayoutInflater.from(mcontext).inflate(R.layout.adapter_from_agent_catergory, parent, false);
            return new CatergoryViewHolder(catergoryview);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            try {


                //Log.d(TAG, "onBindViewHolder catergory : " + catergoryList.get(position).getItems().size());
                final CatergoryViewHolder itemholder = (CatergoryViewHolder) holder;

                itemholder.catergory_title.setText(catergoryList.get(position).getCatergoryName());

                //if (catergoryList.get(position).getItems() != null && catergoryList.get(position).getItems().size()>0) {

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mcontext);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                itemholder.item_list_recycle.setLayoutManager(linearLayoutManager);
                ItemAdapter itemAdapter = new ItemAdapter(catergoryList.get(position).getItems(), position);

                itemholder.item_list_recycle.setAdapter(itemAdapter);

                itemAdapter.notifyDataSetChanged();
//                } else {
//                    itemholder.item_list_recycle.setVisibility(View.GONE);
//                    itemholder.arrow.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.down_arrow));
//                }


                Log.d(TAG, "onBindViewHolder item sizes : " + catergoryList.get(position).getItems().size() + " - " +  catergoryList.get(position).getCatergoryName());

                if (catergoryList.get(position).isItemlist_visible()) {
                    itemholder.item_list_recycle.setVisibility(View.VISIBLE);

                    itemholder.arrow.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.up_arrow));

                } else {
                    itemholder.item_list_recycle.setVisibility(View.GONE);
                    itemholder.arrow.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.down_arrow));
                }



                itemholder.catergory_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (catergoryList.get(position).isItemlist_visible()) {
                            itemholder.item_list_recycle.setVisibility(View.GONE);
                            catergoriesWithItems.get(position).setItemlist_visible(false);
                            catergoryList.get(position).setItemlist_visible(false);
                        } else {
                            itemholder.item_list_recycle.setVisibility(View.VISIBLE);
                            catergoriesWithItems.get(position).setItemlist_visible(true);
                            catergoryList.get(position).setItemlist_visible(true);

                        }
                        notifyItemChanged(position);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return catergoryList.size();
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView item_title, last_ordered_units, available_units, last_order_date;
        private ImageView item_image;
        private LinearLayout item_desc, item_layout;
        private EditText new_order_unit_price, new_order_quantity, salePrice;
        private Button buy;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.item_title);
            item_image = itemView.findViewById(R.id.item_image);
            item_desc = itemView.findViewById(R.id.item_desc);
            item_layout = itemView.findViewById(R.id.item_layout);

            new_order_unit_price = itemView.findViewById(R.id.new_order_unit_price);
            new_order_quantity = itemView.findViewById(R.id.new_order_quantity);

            salePrice = itemView.findViewById(R.id.salePrice);
            last_ordered_units = itemView.findViewById(R.id.last_ordered_units);
            available_units = itemView.findViewById(R.id.available_units);
            last_order_date = itemView.findViewById(R.id.last_order_date);

            buy = itemView.findViewById(R.id.buy);
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

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.adapter_from_agent_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            try {
                final ItemViewHolder itemView = (ItemViewHolder) holder;

                itemView.item_title.setText(itemList.get(position).getName());
                Log.d(TAG, "onBindViewHolder item : " + itemList.get(position).getName());

                itemView.available_units.setText(String.valueOf(itemList.get(position).getAvailableUnits()));

                itemView.item_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemList.get(position).isDesc_visible()) {
                            itemView.item_desc.setVisibility(View.GONE);
                            itemList.get(position).setDesc_visible(false);
                            catergoriesWithItems.get(catergory_position).getItems().get(position).setDesc_visible(false);
                        } else {

                            itemView.item_desc.setVisibility(View.VISIBLE);
                            itemList.get(position).setDesc_visible(true);
                            catergoriesWithItems.get(catergory_position).getItems().get(position).setDesc_visible(true);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Stock").whereEqualTo("user", MainActivity.getSingletonMainActivity().userName).whereEqualTo("item_name", itemList.get(position).getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    try {
                                        Log.d(TAG, "onComplete stock data : " + task.getResult().getDocuments().size());
                                        List<DocumentSnapshot> dataList = task.getResult().getDocuments();
                                        List<StockData> dataclass = new ArrayList<>();
                                        if (dataList != null && dataList.size()>0) {

                                            int latest_index = dataList.size()-1;
                                            StockData stockData = new StockData();
                                            //(dataList.get(latest_index).get("catergory").toString());
                                            itemView.last_order_date.setText(dataList.get(latest_index).get("date").toString());
                                            itemView.last_ordered_units.setText(String.valueOf(dataList.get(latest_index).getDouble("purchased_units")));
                                            //itemView.available_units.setText(dataList.get(latest_index).getString("sale_price"));
                                            //s



                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });

                if (itemList.get(position).isDesc_visible()) {
                    itemView.item_desc.setVisibility(View.VISIBLE);
                } else {
                    itemView.item_desc.setVisibility(View.GONE);
                }

                itemView.buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!itemView.new_order_quantity.getText().toString().equals("") && !itemView.new_order_unit_price.getText().toString().equals("") && !itemView.salePrice.getText().toString().equals("")) {
                            progress = Helper.showProgress(getActivity());
                            checkAvailableCount(itemView.new_order_quantity.getText().toString(), itemView.new_order_unit_price.getText().toString(), itemList.get(position).getName(), itemList.get(position).getCatergory(), itemView.salePrice.getText().toString(), catergory_position, position, itemView);
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

    private void checkAvailableCount(final String orderQuantity, final String orderUnitPrice, final String itemName, final String catergoryName, final String salePrice, final int catergory_position, final int item_position, final ItemViewHolder itemViewHolder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference docRf = db.collection("Items").document(MainActivity.getSingletonMainActivity().userName + "_" + catergoryName).collection("items_sub").document(catergoryName + "_" + itemName);
        docRf.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isComplete()) {
                double newAvailableCount = 0;
                if (task.getResult().getDouble("availableQuantity") != null && task.getResult().getDouble("availableQuantity")>=0) {
                    newAvailableCount = task.getResult().getDouble("availableQuantity") + Double.parseDouble(orderQuantity);
                }
                Log.d(TAG, "updated available count : " + newAvailableCount);
                saveStockData(orderQuantity, orderUnitPrice, itemName, catergoryName, salePrice, newAvailableCount, docRf, catergory_position, item_position, itemViewHolder);
            }
            }
        });

    }

    private void saveStockData(final String orderQuantity, String orderUnitPrice, String itemName, String catergoryName, String salePrice, final double availableCount, DocumentReference docRf, final int cat_position, final int item_position, final ItemViewHolder viewHolder) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(TAG, "saveStockData: " + itemName);

        WriteBatch batch = db.batch();

        String document_name = MainActivity.getSingletonMainActivity().userName +  "_" + itemName + "_" + DateConversion.getCurrentDate();

        HashMap<String, Object> stockitemData = new HashMap<>();
        stockitemData.put("item_name", itemName);
        stockitemData.put("date_time", FieldValue.serverTimestamp());
        stockitemData.put("catergory", catergoryName);
        stockitemData.put("purchased_units", Double.parseDouble(orderQuantity));
        stockitemData.put("purchased_price", Double.parseDouble(orderUnitPrice));
        stockitemData.put("sale_price", Double.parseDouble(salePrice));
        stockitemData.put("date", DateConversion.getCurrentDate());
        stockitemData.put("user", MainActivity.getSingletonMainActivity().userName);
        stockitemData.put("sold_units", 0);

        DocumentReference stockRef = db.collection("Stock").document(document_name);
        batch.set(stockRef, stockitemData);

        batch.update(docRf,"availableQuantity", availableCount);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    if (!itemSearch.getText().toString().equals("")) {
                        catergoriesWithItemsSearch.get(cat_position).getItems().get(item_position).setAvailableUnits(availableCount);

                        for (int i=0; i<catergoriesWithItems.size(); i++) {
                            if (catergoriesWithItems.get(i).getCatergoryName().equals(catergoriesWithItemsSearch.get(cat_position).getCatergoryName())) {
                                catergoriesWithItems.get(i).getItems().get(item_position).setAvailableUnits(availableCount);
                                break;
                            }
                        }

                    } else {
                        catergoriesWithItems.get(cat_position).getItems().get(item_position).setAvailableUnits(availableCount);
                    }

                    viewHolder.last_order_date.setText(DateConversion.getCurrentDate());
                    viewHolder.last_ordered_units.setText(orderQuantity);
                    viewHolder.new_order_unit_price.setText("");
                    viewHolder.salePrice.setText("");
                    viewHolder.new_order_quantity.setText("");

                    if (progress != null) {
                        progress.cancel();
                    }

                    Helper.showSnackBar(mcontext, resources.getString(R.string.stock_adding_success));

                }

            }
        });

    }

    private void getStockData() {

//                    for (int i=0; i<dataList.size(); i++) {
//                        StockData stockData = new StockData();
//                        stockData.setCatergory(dataList.get(i).get("catergory").toString());
//                        stockData.setDate(dataList.get(i).get("date").toString());
//                        stockData.setDate_time(dataList.get(i).get("date_time").toString());
//                        stockData.setPurchased_price(dataList.get(i).getDouble("purchased_price"));
//                        stockData.setPurchased_units(dataList.get(i).getDouble("purchased_units"));
//                        stockData.setSale_price(dataList.get(i).getDouble("sale_price"));
//                        stockData.setUser(dataList.get(i).getString("user"));
//
//                        Log.d(TAG, "before sort : " + stockData.getDate());
//
//                        dataclass.add(stockData);
//                    }
//
//                    Collections.sort(dataclass, new Comparator<StockData>() {
//                        @Override
//                        public int compare(StockData stockData, StockData t1) {
//                            Date date1 = null;
//                            Date date2 = null;
//                            try {
//                                 date1 = new SimpleDateFormat("yyyy-MM-dd").parse(stockData.getDate());
//                                date2 = new SimpleDateFormat("yyyy-MM-dd").parse(t1.getDate());
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            return date1.compareTo(date2);
//                        }
//                    });

//                                        for (int a=0; a<dataclass.size(); a++) {
//                                            Log.d(TAG, "after sort : " + dataclass.get(a).getDate());
//                                        }
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


