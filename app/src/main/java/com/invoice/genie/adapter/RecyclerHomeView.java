package com.invoice.genie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.invoice.genie.HomeInvoice;
import com.invoice.genie.R;
import com.invoice.genie.model.aisle.Inventory;
import com.invoice.genie.model.cart.Invoice;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerHomeView extends RecyclerView.Adapter<RecyclerHomeView.MyViewHolder> {

    List<Invoice> invoices;
    Context context;
    String fromWhichScreen;

    public RecyclerHomeView(Context context, List<Invoice> invoiceList, String fromWhichScreen) {
        this.context = context;
        this.invoices = invoiceList;
        this.fromWhichScreen = fromWhichScreen;
    }

    @NonNull
    @Override
    public RecyclerHomeView.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        Create a view with the layout which we created for the card view & return the view
        */
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_invoices_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHomeView.MyViewHolder holder, int position) {
        /*
          Here we will bind the data to each card view
          so we need to create instances for the order & date
          let us not use other items inside the class
          We will later on set this in MyViewHolder
          */
        String order_id = invoices.get(position).getCustomer_name() + " on " + invoices.get(position).getOrder_date() + " at " + invoices.get(position).getOrder_time() + " in " + invoices.get(position).getCompany_name();

        String order_date = invoices.get(position).getOrder_date();
        String customer_name = invoices.get(position).getCustomer_name();
        holder.assignValues(order_id,order_date,customer_name);

        holder.detailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String order_id = invoices.get(holder.getAdapterPosition()).getCustomer_name() + " on " + invoices.get(holder.getAdapterPosition()).getOrder_date() + " at " + invoices.get(holder.getAdapterPosition()).getOrder_time() + " in " + invoices.get(holder.getAdapterPosition()).getCompany_name();
                Invoice thisInvoice = invoices.get(holder.getAdapterPosition());
                Intent goToInvoice = new Intent(context, HomeInvoice.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                goToInvoice.putExtra("invoice",thisInvoice);
                goToInvoice.putExtra("order_id", order_id);
                context.startActivity(goToInvoice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public void filteredList(List<Invoice> searchList) {
        this.invoices = searchList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
         /**MyViewHolder
          * It is the one which we iterate for each item present in the list
          * First we need to get the id's & assign
          * */
        private final TextView orderID, orderDate, customerName;
        ConstraintLayout detailsLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            /*
            Now we will assign above one's with the id's which we given in layout for this itemView
             */
            orderID = itemView.findViewById(R.id.invoices_number_text);
            orderDate = itemView.findViewById(R.id.invoices_order_date_text);
            customerName = itemView.findViewById(R.id.invoices_customer_name_text);
            detailsLayout = itemView.findViewById(R.id.recycle_invoices_layout);
        }

        public void assignValues(String orderIDText, String orderedDate, String customer_name) {
            /*
            we are passing values from Main view
            orderIDText & orderedDate are set of each individual item (or) at item level (or) for each item, we can say
             */
            orderID.setText(orderIDText);
            String parkedD = "On : "+orderedDate;
            orderDate.setText(parkedD);
            String customer_Name = "By : "+customer_name;
            customerName.setText(customer_Name);
        }
    }
}
