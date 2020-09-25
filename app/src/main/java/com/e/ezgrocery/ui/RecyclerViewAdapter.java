package com.e.ezgrocery.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.ezgrocery.R;
import com.e.ezgrocery.data.DatabaseHelper;
import com.e.ezgrocery.model.GroceryItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<GroceryItem> groceryItemList;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<GroceryItem> groceryItemList) {
        this.context = context;
        this.groceryItemList = groceryItemList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_layout, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        GroceryItem groceryItem = groceryItemList.get(position);

        holder.itemName.setText(groceryItem.getItemName());
        holder.itemQuantity.setText(String.format("Quantity: %s", String.valueOf(groceryItem.getItemQuantity())));
        if (!groceryItem.getItemDescription().isEmpty())
            holder.itemDescription.setText(String.format("Description: %s", groceryItem.getItemDescription()));
        else
            holder.itemDescription.setText(null);
    }

    @Override
    public int getItemCount() {
        return groceryItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public int id;
        public TextView itemName;
        public TextView itemQuantity;
        public TextView itemDescription;
        public Button editButton;
        public Button deleteButton;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.txtItemName);
            itemQuantity = itemView.findViewById(R.id.txtQuantity);
            itemDescription = itemView.findViewById(R.id.txtDescription);
            editButton = itemView.findViewById(R.id.btnEdit);
            deleteButton = itemView.findViewById(R.id.btnDelete);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            DatabaseHelper db = new DatabaseHelper(context);
            int position = getAdapterPosition();
            GroceryItem groceryItem = groceryItemList.get(position);
            switch(v.getId()){
                case R.id.btnEdit:
                    //Edit grocery item
                    editItem(groceryItem);
                    break;
                case R.id.btnDelete:
                    //Delete grocery item
                    deleteItem(groceryItem.getId());
                    break;

            }
        }

        private void editItem(final GroceryItem newGroceryItem) {
            builder = new AlertDialog.Builder(context);
            inflater = inflater.from(context);

            View view = inflater.inflate(R.layout.dialog_popup, null);

            final EditText itemNameEdit;
            final EditText itemQuantityEdit;
            final EditText itemDescriptionEdit;
            Button doneButton;
            TextView title;

            itemNameEdit = view.findViewById(R.id.editName);
            itemQuantityEdit = view.findViewById(R.id.editQuantity);
            itemDescriptionEdit = view.findViewById(R.id.editDescription);
            doneButton = view.findViewById(R.id.btnDone);
            title = view.findViewById(R.id.txtTitle);

            title.setText("Edit Item");
            itemNameEdit.setText(newGroceryItem.getItemName());
            itemQuantityEdit.setText(String.valueOf(newGroceryItem.getItemQuantity()));
            itemDescriptionEdit.setText(newGroceryItem.getItemDescription());
            doneButton.setText("Update");

            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHelper db = new DatabaseHelper(context);

                    newGroceryItem.setItemName(itemNameEdit.getText().toString());
                    newGroceryItem.setItemQuantity(Integer.parseInt(itemQuantityEdit.getText().toString()));
                    newGroceryItem.setItemDescription(itemDescriptionEdit.getText().toString());

                    if (!itemName.getText().toString().isEmpty()
                            && !itemQuantity.getText().toString().isEmpty()){
                        db.updateGroceryItem(newGroceryItem);
                        notifyItemChanged(getAdapterPosition(), newGroceryItem);
                    }
                    else{
                        Snackbar.make(v, "Item field incomplete!", Snackbar.LENGTH_SHORT).show();
                    }

                    alertDialog.dismiss();
                }
            });
        }

        private void deleteItem(final int id) {
            builder = new AlertDialog.Builder(context);
            inflater = inflater.from(context);

            View view = inflater.inflate(R.layout.delete_confirmation_popup, null);

            Button confirmYesButton = view.findViewById(R.id.btnConfirmYes);
            Button confirmNoButton = view.findViewById(R.id.btnConfirmNo);

            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();

            confirmYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHelper db = new DatabaseHelper(context);
                    db.deleteGroceryItem(id);
                    groceryItemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    alertDialog.dismiss();
                }
            });
            confirmNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
    }
}
