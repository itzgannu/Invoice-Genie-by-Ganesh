package com.invoice.genie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.invoice.genie.R;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.cart.Items;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerCartView extends RecyclerView.Adapter<RecyclerCartView.MyViewHolder> {

    /**
     * Variables
     */
    List<Items> items = new ArrayList<>();
    Context context;
    RecursiveMethods helper = new RecursiveMethods();
    private final String TAG = "RecyclerCartView";

    /**
     * Constructor to initialize
     */
    public RecyclerCartView(Context context, List<Items> items) {
        helper.logD(TAG, "constructor is called successfully");
        this.context = context;
        this.items = items;
    }

    /**
     * Classes onCreateViewHolder, onBindViewHolder, getItemCount
     * are created automatically for extending with RecyclerView.Adapter
     */
    @NonNull
    @Override
    public RecyclerCartView.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        Create a view with the layout which we created for the card view & return the view
        */
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_cart_products, parent, false);
        helper.logD(TAG, "recycle_cart_layout is inflated into view & returned now - onCreateViewHolder");
        return new RecyclerCartView.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerCartView.MyViewHolder holder, int position) {
        /*
          Here we will bind the data to each card view
          so we need to create instances for the image, name, price, unit type(package) of inventory
          let us not use other items inside Products class
          We will later on set this in MyViewHolder
          */
        /*
        Get values of items from recyclerInventories & position
         */
        String product_name, unit_type, url, product_quantity, product_price, product_total_price;

        product_name = items.get(position).getProduct_name();
        unit_type = items.get(position).getUnit_type();
        url = items.get(position).getUrl();
        product_quantity = String.valueOf(items.get(position).getProduct_quantity());
        product_price = String.valueOf(items.get(position).getProduct_price());
        product_total_price = "CAD "+String.valueOf(items.get(position).getProduct_total_price());

        holder.assignValues(product_quantity,product_name,product_price,unit_type,product_total_price,url);
    }

    @Override
    public int getItemCount() {
         /*
        This will return number of items or card view we need to display on screen
        */
        int size = items.size();
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
        TextView quantity, name, info, total;
        ImageView image;
        ConstraintLayout cartProductsLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            /*
            Now we will assign above one's with the id's which we given in layout for this itemView
             */
            quantity = itemView.findViewById(R.id.cart_card_product_quantity);
            name = itemView.findViewById(R.id.cart_card_product_name);
            info = itemView.findViewById(R.id.cart_card_product_info);
            total = itemView.findViewById(R.id.cart_card_product_total_price);
            image = itemView.findViewById(R.id.cart_card_product_image);
            helper.logD(TAG, "Elements assigned now - MyViewHolder");
        }

        /*
        We are passing values from onBindViewHolder to assign to the elements
         */
        public void assignValues(String product_quantity, String product_name, String product_price, String product_unit_type, String product_total, String url){
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.not_found)
                    .into(image);
            name.setText(product_name);
            quantity.setText(product_quantity);
            String information = "CAD "+product_price + " - " + product_unit_type;
            info.setText(information);
            total.setText(product_total);
            helper.logD(TAG, "Values are assigned to each element - assignValues");
        }
    }
}
