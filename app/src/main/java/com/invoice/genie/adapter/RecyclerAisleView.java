package com.invoice.genie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.invoice.genie.Cart_Individual;
import com.invoice.genie.R;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.aisle.Inventory;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAisleView extends RecyclerView.Adapter<RecyclerAisleView.MyViewHolder> {
    /**
     * Variables
     */
    List<Inventory> recyclerInventories = new ArrayList<>();
    Context context;
    RecursiveMethods helper = new RecursiveMethods();
    private final String TAG = "RecyclerAisleView";

    /**
     * Constructor to initialize
     */
    public RecyclerAisleView(Context context, List<Inventory> inventoryList) {
        helper.logD(TAG, "constructor is called successfully");
        this.context = context;
        this.recyclerInventories = inventoryList;
    }

    /**
     * Classes onCreateViewHolder, onBindViewHolder, getItemCount
     * are created automatically for extending with RecyclerView.Adapter
     */

    @NonNull
    @Override
    public RecyclerAisleView.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        Create a view with the layout which we created for the card view & return the view
        */
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_products_layout, parent, false);
        helper.logD(TAG, "recycle_products_layout is inflated into view & returned now - onCreateViewHolder");
        return new RecyclerAisleView.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAisleView.MyViewHolder holder, int position) {
        /*
        Here we will bind the data to each card view
        so we need to create instances for the image, name, price, unit type(package) of inventory
        let us not use other items inside Products class
        We will later on set this in MyViewHolder
         */
        /*
        Get values of image url, inventory name, price, package (unit type) from recyclerInventories & position
         */
        String single_inventory_image_URL, single_inventory_name, single_inventory_price, single_inventory_unit_type;
        single_inventory_image_URL = recyclerInventories.get(position).getUrl();
        single_inventory_name = recyclerInventories.get(position).getName();
        single_inventory_price = recyclerInventories.get(position).getPrice().toString();
        single_inventory_unit_type = recyclerInventories.get(position).getPack();

        holder.assignValues(single_inventory_image_URL, single_inventory_name, single_inventory_price, single_inventory_unit_type);

        holder.productsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Go to detail screen & assign Parking obj to that screen
                 */
                helper.logD(TAG, single_inventory_name + " is clicked & now going to Cart_Individual Screen - onBindViewHolder");
                Intent goToDetailScreen = new Intent(context, Cart_Individual.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                goToDetailScreen.putExtra("inventory", recyclerInventories.get(holder.getAdapterPosition()));
                context.startActivity(goToDetailScreen);
            }
        });
    }

    @Override
    public int getItemCount() {
        /*
        This will return number of items or card view we need to display on screen
        */
        int size = recyclerInventories.size();
        helper.logD(TAG, "Returning size of List - " + size);
        return size;
    }

    /**
     * MyViewHolder
     * Class MyViewHolder created automatically when extending with RecyclerProductView.MyViewHolder
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        /*
        Created automatically when extending with RecyclerView.ViewHolder
         */
        /*
        MyViewHolder is the one which we iterate for each item present in the list
         */
        /*
        First we need to get the id's & assign
         */
        ImageView inventory_image;
        TextView inventory_name, inventory_price, inventory_unit_type;
        CardView productsLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            /*
            Now we will assign above one's with the id's which we given in layout for this itemView
             */
            inventory_image = itemView.findViewById(R.id.product_card_image);
            inventory_name = itemView.findViewById(R.id.product_card_name);
            inventory_price = itemView.findViewById(R.id.product_card_price);
            inventory_unit_type = itemView.findViewById(R.id.product_card_unit_type);
            productsLayout = itemView.findViewById(R.id.recycle_products_layout);
            helper.logD(TAG, "Elements assigned now - MyViewHolder");
        }

        /*
        We are passing values from onBindViewHolder to assign to the elements
         */
        public void assignValues(String single_inventory_image_URL, String single_inventory_name, String single_inventory_price, String single_inventory_unit_type) {
            Glide.with(context)
                    .load(single_inventory_image_URL)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.not_found)
                    .into(inventory_image);
            inventory_name.setText(single_inventory_name);
            inventory_price.setText(single_inventory_price);
            inventory_unit_type.setText(single_inventory_unit_type);
            helper.logD(TAG, "Values are assigned to each element - assignValues");
        }
    }

    public void filteredList(List<Inventory> searchList) {
        this.recyclerInventories = searchList;
        notifyDataSetChanged();
    }

}
