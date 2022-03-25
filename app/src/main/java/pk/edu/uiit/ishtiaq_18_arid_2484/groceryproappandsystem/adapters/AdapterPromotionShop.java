package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities.AddPromotionCodeActivity;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelPromotion;

public class AdapterPromotionShop extends RecyclerView.Adapter<AdapterPromotionShop.HolderPromotionShop> {
    Context context;
    ArrayList<ModelPromotion> promotionArrayList;

    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    public AdapterPromotionShop(Context context, ArrayList<ModelPromotion> promotionArrayList) {
        this.context = context;
        this.promotionArrayList = promotionArrayList;

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPromotionShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout row_promotion_shop.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_promotion_shop, parent, false);
        return new HolderPromotionShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPromotionShop holder, int position) {

        // Get Data
        ModelPromotion modelPromotion = promotionArrayList.get(position);
        String id = modelPromotion.getId();
        String timestamp = modelPromotion.getTimestamp();
        String description = modelPromotion.getDescription();
        String promoCode = modelPromotion.getPromoCode();
        String promoPrice = modelPromotion.getPromoPrice();
        String expireDate = modelPromotion.getExpireDate();
        String minimumOrderPrice = modelPromotion.getMinimumOrderPrice();

        // Set Data
        holder.descriptionTv.setText(description);
        holder.promoPriceTv.setText(promoPrice);
        holder.minimumOrderPriceTv.setText(minimumOrderPrice);
        holder.promoCodeTv.setText("Promo Code: "+promoCode);
        holder.expireDateTv.setText("Expire Date: "+expireDate);

        // Handle Click, Show Edit/Delete Dialog
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDeleteDialog(modelPromotion, holder);
            }
        });
    }

    private void editDeleteDialog(ModelPromotion modelPromotion, HolderPromotionShop holder) {
        // Options To Display In Dialog
        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Option")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle Clicks
                        if (which == 0){
                            // Edit Clicked
                            editPromoCode(modelPromotion);
                        }
                        else if (which == 1){
                            // Delete Clicked
                            deletePromoCode(modelPromotion);
                        }
                    }
                })
                .show();
    }

    private void editPromoCode(ModelPromotion modelPromotion) {
        // Start And Pass Data To AddPromotionCodeActivity To Edit
        Intent intent = new Intent(context, AddPromotionCodeActivity.class);
        intent.putExtra("promoId",modelPromotion.getId()); // Will Use id To Update Promo Code
        context.startActivity(intent);
    }

    private void deletePromoCode(ModelPromotion modelPromotion) {
        // Show Progress Bar
        progressDialog.setMessage("Deleting Promotion Code...");
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Promotions").child(modelPromotion.getId())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Deleted
                        progressDialog.dismiss();
                        Toast.makeText(context, "Promo Code Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed Deleting
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return promotionArrayList.size();
    }

    // View Holder Class
    class HolderPromotionShop extends RecyclerView.ViewHolder{

        // Views Of row_promotion_shop.xml
        ImageButton iconIv;
        TextView promoCodeTv, promoPriceTv, minimumOrderPriceTv, expireDateTv, descriptionTv;
        public HolderPromotionShop(@NonNull View itemView) {
            super(itemView);
            // Initialization Views Of row_promotion_shop.xml
            promoCodeTv = itemView.findViewById(R.id.promoCodeTv);
            promoPriceTv = itemView.findViewById(R.id.promoPriceTv);
            minimumOrderPriceTv = itemView.findViewById(R.id.minimumOrderPriceTv);
            expireDateTv = itemView.findViewById(R.id.expireDateTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
        }
    }
}
