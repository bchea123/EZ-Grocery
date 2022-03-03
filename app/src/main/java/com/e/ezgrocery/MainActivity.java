package com.e.ezgrocery;

import android.content.Intent;
import android.os.Bundle;

import com.e.ezgrocery.data.DatabaseHelper;
import com.e.ezgrocery.model.GroceryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText itemName;
    private EditText itemQuantity;
    private EditText itemDescription;
    private Button doneButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(MainActivity.this);

        bypassActivity();

        FloatingActionButton fab = findViewById(R.id.fabAddItem);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialog();
            }
        });
    }

    private void bypassActivity() {
        if(db.getCount() > 0) {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
            finish();
        }
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
                if (!itemName.getText().toString().isEmpty()){
                    saveGroceryItem(v);
                }
                else{
                    Snackbar.make(v, "Please enter an item name!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();


    }

    public void saveGroceryItem(View view){
        String newName = itemName.getText().toString().trim();
        String newQuantity = itemQuantity.getText().toString().trim();
        String newDescription = itemDescription.getText().toString().trim();

        GroceryItem item = new GroceryItem();
        item.setItemName(newName);
        item.setItemQuantity(newQuantity);
        item.setItemDescription(newDescription);

        db.createGroceryItem(item);
        db.close();

        Snackbar.make(view, "Item added!", Snackbar.LENGTH_SHORT).show();

        //Delay actions listed
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Close alert dialog
                dialog.dismiss();

                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}