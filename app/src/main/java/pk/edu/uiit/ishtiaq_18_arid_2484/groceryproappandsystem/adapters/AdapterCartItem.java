package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.DataBaseHelper;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities.ShopDetailsActivity;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelCartItem;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem> {

    Context context;
    ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate Layout row_cartitem.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_cartitem, parent, false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, @SuppressLint("RecyclerView") int position) {

        // Get Data
        ModelCartItem modelCartItem = cartItems.get(position);
        String id = modelCartItem.getId();
        String getpId = modelCartItem.getpId();
        String title = modelCartItem.getName();
        String cost = modelCartItem.getCost();
        String price = modelCartItem.getPrice();
        String quantity = modelCartItem.getQuantity();

        // Set Data
        holder.itemTitleTv.setText("" + title);
        holder.itemPriceTv.setText("" + cost);
        holder.itemQuantityTv.setText("[" + quantity+"]"); // For Example [8]
        holder.itemPriceEachTv.setText("" + price);

        // Handle Remove Click Listener, Delete Item From Cart
        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete Data In Database( SQLite )
                DataBaseHelper databaseHelper;
                databaseHelper = new DataBaseHelper(context);
                Boolean cartItemId = databaseHelper.deleteCartItem(getpId);
                if(cartItemId == false)
                {
                    Toast.makeText(context, "Error! Not Remove From Cart", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, "Removed Cart Item", Toast.LENGTH_LONG).show();
                }
                // Refresh List
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                // Adjust The  SubTotal After Product Remove
                double subTotalWithoutDiscount = ((ShopDetailsActivity)context).allTotalPrice;
                double subTotalAfterProductRemove = subTotalWithoutDiscount - Double.parseDouble(cost.replace("$",""));
                ((ShopDetailsActivity)context).allTotalPrice = subTotalAfterProductRemove;
                ((ShopDetailsActivity)context).sTotalTv.setText("$" + String.format("%.2f", ((ShopDetailsActivity)context).allTotalPrice));

                // Once Subtotal is Updated..... Check Minimum Order Price Of Promo Code
                double promoPrice = Double.parseDouble(((ShopDetailsActivity)context).promoPrice);
                double deliveryFee = Double.parseDouble(((ShopDetailsActivity)context).deliveryFee.replace("$",""));

                // Check If Promo Code Applied
                if (((ShopDetailsActivity)context).isPromoCodeApplied){
                    // Applied
                    if (subTotalAfterProductRemove < Double.parseDouble(((ShopDetailsActivity)context).promoMinimumOrderPrice)){
                        // Current Order Price Is Less Then Minimum required Price
                        Toast.makeText(context, "This Code Is Valid For Order With Minimum Amount $"+((ShopDetailsActivity)context).promoMinimumOrderPrice, Toast.LENGTH_SHORT).show();
                        ((ShopDetailsActivity)context).applyBtn.setVisibility(View.GONE);
                        ((ShopDetailsActivity)context).promoDescriptionTv.setVisibility(View.GONE);
                        ((ShopDetailsActivity)context).promoDescriptionTv.setText("");
                        ((ShopDetailsActivity)context).discountTv.setText("$0");
                        ((ShopDetailsActivity)context).isPromoCodeApplied = false;
                        // Show New Net Total After Delivery Fee
                        ((ShopDetailsActivity)context).allTotalPriceTv.setText("$" + String.format("%.2f", Double.parseDouble(String.format("%.2f", subTotalAfterProductRemove + deliveryFee))));
                    }
                    else {
                        ((ShopDetailsActivity)context).applyBtn.setVisibility(View.VISIBLE);
                        ((ShopDetailsActivity)context).promoDescriptionTv.setVisibility(View.VISIBLE);
                        ((ShopDetailsActivity)context).promoDescriptionTv.setText(((ShopDetailsActivity)context).promoDescription);
                        // Show New  Total Price After Adding Delivery Fee And Subtracting Promo Fee
                        ((ShopDetailsActivity)context).isPromoCodeApplied = true;
                        ((ShopDetailsActivity)context).allTotalPriceTv.setText("$" + String.format("%.2f",Double.parseDouble(String.format("%.2f", subTotalAfterProductRemove + deliveryFee - promoPrice))));

                    }
                }
                else {
                    // Not Applied
                    ((ShopDetailsActivity)context).allTotalPriceTv.setText("$" + String.format("%.2f", Double.parseDouble(String.format("%.2f", subTotalAfterProductRemove + deliveryFee))));
                }
                // After Removing Item From Cart, Update Cart Count
                ((ShopDetailsActivity)context).cartCount();

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size(); // Return Number Of Records
    }

    class HolderCartItem extends RecyclerView.ViewHolder {

        // View Holder Class
        TextView itemTitleTv, itemPriceTv, itemPriceEachTv, itemQuantityTv, itemRemoveTv;

        // UI Views Of row_cartitems
        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            // Initialization Of Views
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv = itemView.findViewById(R.id.itemRemoveTv);
        }
    }
}
