package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities.OrderDetailsUsersActivity;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelOrderUser;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser> {
    Context context;
    ArrayList<ModelOrderUser> orderUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> orderUserList) {
        this.context = context;
        this.orderUserList = orderUserList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout ( row_order_user )
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_user, parent, false);
        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {

        // Get Data
        ModelOrderUser modelOrderUser = orderUserList.get(position);
        String orderId = modelOrderUser.getOrderId();
        String orderBy = modelOrderUser.getOrderBy();
        String orderCost = modelOrderUser.getOrderCost();
        String orderStatus = modelOrderUser.getOrderStatus();
        String orderTime = modelOrderUser.getOrderTime();
        String orderTo = modelOrderUser.getOrderTo();

        // Get Shop Info
        loadShopInfo(modelOrderUser, holder);

        // Set Data
        holder.amountTv.setText("Amount: $" + orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("order ID:" + orderId);

        // Change Order Status Text Color
        if (orderStatus.equals("In Progress")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else if (orderStatus.equals("Completed")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
        else if (orderStatus.equals("Cancelled")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        // Convert timestamp To Proper Format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy", calendar).toString(); // e.g. 14/03/2022
        holder.dateTv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Order Details, We Need To Keys there, orderId, orderTo
                Intent intent = new Intent(context, OrderDetailsUsersActivity.class);
                intent.putExtra("orderTo", orderTo);
                intent.putExtra("orderId", orderId);
                // Now Get These Values Through Intent On OrderDetailsUsersActivity;
                context.startActivity(intent);
            }
        });
    }

    private void loadShopInfo(ModelOrderUser modelOrderUser, HolderOrderUser holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(modelOrderUser.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopName = ""+snapshot.child("shopName").getValue();
                        holder.shopNameTv.setText(shopName);
                     }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderUserList.size();
    }

    // View Holder Class
    class HolderOrderUser extends RecyclerView.ViewHolder{

        // Views Of Layout ( row_order_user )
        TextView orderIdTv, dateTv, shopNameTv, amountTv, statusTv;

        public HolderOrderUser(@NonNull View itemView) {
            super(itemView);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
        }
    }
}
