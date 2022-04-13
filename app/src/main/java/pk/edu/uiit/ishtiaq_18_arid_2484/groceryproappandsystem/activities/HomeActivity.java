package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterProductSeller;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelProduct;

public class HomeActivity extends AppCompatActivity {
    ImageButton accountBtn, homeBtn;
    RecyclerView productsRv;
    // FirebaseAuth
    private FirebaseAuth firebaseAuth;
    // Products
    ArrayList<ModelProduct> productList;
    AdapterProductSeller adapterProductSeller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewsInitialization();
        ViewsPerformanceActions();

    }
    // UI Views Initialization
    public void ViewsInitialization() {
        // Initialization Of Views
        accountBtn = findViewById(R.id.accountBtn);
        homeBtn = findViewById(R.id.homeBtn);
        productsRv = findViewById(R.id.productsRv);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //loadAllProducts();
    }
    // UI Views Performance Actions
    public void ViewsPerformanceActions() {

        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            }
        });
    }
    private void loadAllProducts() {
        productList = new ArrayList<>();
        // Get All Products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Before Getting Reset List
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        // Setup Adapter
                        adapterProductSeller = new AdapterProductSeller(HomeActivity.this, productList);
                        // Set Adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}