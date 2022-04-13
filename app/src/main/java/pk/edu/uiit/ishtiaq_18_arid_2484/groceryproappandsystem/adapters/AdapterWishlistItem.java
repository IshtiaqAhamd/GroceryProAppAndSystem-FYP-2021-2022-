package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelWishlistItem;

public class AdapterWishlistItem extends RecyclerView.Adapter<AdapterWishlistItem.HolderWishlistItem>{
    Context context;
    ArrayList<ModelWishlistItem> wishlistItems;

    public AdapterWishlistItem(Context context, ArrayList<ModelWishlistItem> cartItems) {
        this.context = context;
        this.wishlistItems = cartItems;
    }

    @NonNull
    @Override
    public HolderWishlistItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate Layout row_cartitem.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_cartitem, parent, false);
        return new HolderWishlistItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderWishlistItem holder, int position) {
// Get Data
        ModelWishlistItem modelCartItem = wishlistItems.get(position);
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

    }

    @Override
    public int getItemCount() {
        return wishlistItems.size(); // Return Number Of Records
    }

    class HolderWishlistItem extends RecyclerView.ViewHolder {

        // View Holder Class
        TextView itemTitleTv, itemPriceTv, itemPriceEachTv, itemQuantityTv, itemRemoveTv;

        // UI Views Of row_cartitems
        public HolderWishlistItem(@NonNull View itemView) {
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
