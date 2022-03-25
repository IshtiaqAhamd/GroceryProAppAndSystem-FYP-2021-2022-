package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterPromotionShop;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelPromotion;

public class PromotionCodesActivity extends AppCompatActivity {

    // Declaring Add Product Activity UI Views
    ImageButton backBtn, addPromoBtn, filterBtn;
    TextView filteredTv;
    RecyclerView promoRv;

    // FirebaseAuth

    FirebaseAuth firebaseAuth;

    // Promotion Code Model And Adapter
    ArrayList<ModelPromotion> promotionArrayList;
    AdapterPromotionShop adapterPromotionShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_codes);

        ViewsInitialization();
        ViewsPerformanceActions();
    }

    // UI Views Initialization
    public void ViewsInitialization() {
        // Initialization Of Views
        backBtn = findViewById(R.id.backBtn);
        addPromoBtn = findViewById(R.id.addPromoBtn);
        filterBtn = findViewById(R.id.filterBtn);
        filteredTv = findViewById(R.id.filteredTv);
        promoRv = findViewById(R.id.promoRv);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        loadAllPromoCodes();
    }

    // UI Views Performance Actions
    public void ViewsPerformanceActions() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go Back To The Previous Activity
                onBackPressed();
            }
        });

        // Handle Click, Open Add Promo Code Activity
        addPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PromotionCodesActivity.this, AddPromotionCodeActivity.class));
            }
        });

        // Handle Filter Button Click, Show Filter Dialog
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });
    }

    private void filterDialog() {

        // Option To Display In dialog
        String[] options = {"All", "Expired", "Not Expired"};

        // Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Promotion Codes")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handel Clicks
                        if (which==0){
                            // All Clicked
                            filteredTv.setText("All Promotion Codes");
                            loadAllPromoCodes();
                        }
                        else if (which==1){
                            // All Clicked
                            filteredTv.setText("Expired Promotion Codes");
                            loadExpiredPromoCodes();
                        }
                        else if (which==2){
                            // All Clicked
                            filteredTv.setText("Expired Promotion Codes");
                            loadNotExpiredPromoCodes();
                        }
                    }
                })
                .show();
    }

    private void loadAllPromoCodes() {
        // Initialization List
        promotionArrayList = new ArrayList<>();

        // DB Reference Users -> Current User -> Promotions -> Codes Data
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear List Before Adding Data
                        promotionArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);
                            //Add To List
                            promotionArrayList.add(modelPromotion);
                        }
                        // Setup Adapter, Add List To Adapter
                        adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, promotionArrayList);
                        // Set Adapter To Recyclerview
                        promoRv.setAdapter(adapterPromotionShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadExpiredPromoCodes(){
        // Get Current Date
        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String todayDate = day +"/"+ month +"/"+ year; // e.g. 26/03/2022
        // Initialization List
        promotionArrayList = new ArrayList<>();

        // DB Reference Users -> Current User -> Promotions -> Codes Data
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear List Before Adding Data
                        promotionArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);

                            String expDate = modelPromotion.getExpireDate();

                            /*---------- Check Expire Date ----------*/
                            try {
                                SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                                Date currentDate = sdformat.parse(todayDate);
                                Date expireDate = sdformat.parse(expDate);
                                if (expireDate.compareTo(currentDate) > 0){
                                    // Date 1 Occurs  After Date 2
                                }
                                else if (expireDate.compareTo(currentDate) < 0){
                                    // Date 1 Occurs  Before Date 2 (i.e. Expired)
                                    // Add List
                                    promotionArrayList.add(modelPromotion);
                                }
                                else if (expireDate.compareTo(currentDate) < 0){
                                    // Both Date Equals
                                }
                            }
                            catch (Exception e){

                            }
                        }
                        // Setup Adapter, Add List To Adapter
                        adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, promotionArrayList);
                        // Set Adapter To Recyclerview
                        promoRv.setAdapter(adapterPromotionShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadNotExpiredPromoCodes(){
        // Get Current Date
        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String todayDate = day +"/"+ month +"/"+ year; // e.g. 26/03/2022
        // Initialization List
        promotionArrayList = new ArrayList<>();

        // DB Reference Users -> Current User -> Promotions -> Codes Data
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear List Before Adding Data
                        promotionArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);

                            String expDate = modelPromotion.getExpireDate();

                            /*---------- Check Expire Date ----------*/
                            try {
                                SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                                Date currentDate = sdformat.parse(todayDate);
                                Date expireDate = sdformat.parse(expDate);
                                if (expireDate.compareTo(currentDate) > 0){
                                    // Date 1 Occurs  After Date 2
                                    // Add List
                                    promotionArrayList.add(modelPromotion);
                                }
                                else if (expireDate.compareTo(currentDate) < 0){
                                    // Date 1 Occurs  Before Date 2 (i.e. Expired)

                                }
                                else if (expireDate.compareTo(currentDate) < 0){
                                    // Both Date Equals
                                    // Add List
                                    promotionArrayList.add(modelPromotion);
                                }
                            }
                            catch (Exception e){

                            }
                        }
                        // Setup Adapter, Add List To Adapter
                        adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, promotionArrayList);
                        // Set Adapter To Recyclerview
                        promoRv.setAdapter(adapterPromotionShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}