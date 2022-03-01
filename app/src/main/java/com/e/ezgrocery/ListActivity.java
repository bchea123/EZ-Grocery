package com.e.ezgrocery;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.e.ezgrocery.data.DatabaseHelper;
import com.e.ezgrocery.model.GroceryItem;
import com.e.ezgrocery.ui.RecyclerViewAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<GroceryItem> groceryItemList;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton addGroceryButton;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText itemName;
    private EditText itemQuantity;
    private EditText itemDescription;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView;

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        recyclerView = findViewById(R.id.recyclerView);
        addGroceryButton = findViewById(R.id.fabAddItem);

        databaseHelper = new DatabaseHelper(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groceryItemList = new ArrayList<>();

        groceryItemList = databaseHelper.getAllGroceries();

        recyclerViewAdapter = new RecyclerViewAdapter(this, groceryItemList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        addGroceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopupDialog();
            }
        });
    }

    private void createPopupDialog() {
        //Create view object and fill with inflated dialog layout
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_popup, null);

        itemName = view.findViewById(R.id.editName);
        itemQuantity = view.findViewById(R.id.editQuantity);
        itemDescription = view.findViewById(R.id.editDescription);
        doneButton = view.findViewById(R.id.btnDone);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemName.getText().toString().isEmpty()
                        && !itemQuantity.getText().toString().isEmpty()){
                    saveGroceryItem(v);
                }
                else{
                    Snackbar.make(v, "Item field incomplete!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    public void saveGroceryItem(View view){
        String newName = itemName.getText().toString().trim();
        int newQuantity = Integer.parseInt(itemQuantity.getText().toString().trim());
        String newDescription = itemDescription.getText().toString().trim();

        GroceryItem item = new GroceryItem();
        item.setItemName(newName);
        item.setItemQuantity(newQuantity);
        item.setItemDescription(newDescription);

        databaseHelper.createGroceryItem(item);
        databaseHelper.close();

        Snackbar.make(view, "Item added!", Snackbar.LENGTH_SHORT).show();

        startActivity(new Intent(ListActivity.this, ListActivity.class));
        finish();

        //Delay actions listed
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Close alert dialog
                dialog.dismiss();
            }
        }, 500);
    }
}