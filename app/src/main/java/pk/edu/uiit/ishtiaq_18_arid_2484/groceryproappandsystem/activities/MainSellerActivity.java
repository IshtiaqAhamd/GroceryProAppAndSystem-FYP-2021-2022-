package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterOrderShop;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterProductSeller;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.Constants;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelOrderShop;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelProduct;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;

public class MainSellerActivity extends AppCompatActivity {
    // Declaring Main Seller Activity UI Views
    TextView nameTv, shopNameTv, emailTv, tabProductsTv, tabOrdersTv, filteredProductsTv, filteredOrdersTv;
    EditText searchProductEt;
    ImageButton logoutBtn, editProfileBtn, addProductBtn, filterProductBtn, filterOrderBtn, moreBtn;
    ImageView profileIv;
    RelativeLayout productsRl, ordersRl;
    RecyclerView productsRv, ordersRv;

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    // Progress Dialog
    private ProgressDialog progressDialog;

    // Products
    ArrayList<ModelProduct> productList;
    AdapterProductSeller adapterProductSeller;

    // Orders
    ArrayList<ModelOrderShop> orderShopArrayList;
    AdapterOrderShop adapterOrderShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        ViewsInitialization();
        ViewsPerformanceActions();
    }

    // UI Views Initialization
    public void ViewsInitialization() {
        // Initialization Of Views
        nameTv = findViewById(R.id.nameTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        emailTv = findViewById(R.id.emailTv);
        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        filteredOrdersTv = findViewById(R.id.filteredOrdersTv);
        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        productsRl = findViewById(R.id.productsRl);
        ordersRl = findViewById(R.id.ordersRl);
        addProductBtn = findViewById(R.id.addProductBtn);
        logoutBtn = findViewById(R.id.cartBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        filterOrderBtn = findViewById(R.id.filterOrderBtn);
        moreBtn = findViewById(R.id.moreBtn);
        profileIv = findViewById(R.id.profileIv);
        productsRv = findViewById(R.id.productsRv);
        ordersRv = findViewById(R.id.ordersRv);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadAllProducts();
        loadAllOrders();
        showProductsUI();

        // Initialization Of Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void loadAllOrders() {
        // Initialization Of List
        orderShopArrayList = new ArrayList<>();

        // Load Orders Of Shop
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear List Before Adding New Data In It
                        orderShopArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderShop modelOrderShop = ds.getValue(ModelOrderShop.class);
                            // Add To List
                            orderShopArrayList.add(modelOrderShop);
                        }
                        // Setup Adapter
                        adapterOrderShop = new AdapterOrderShop(MainSellerActivity.this, orderShopArrayList);
                        // Set Adapter To Recyclerview
                        ordersRv.setAdapter(adapterOrderShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        // Set Adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    // UI Views Performance Actions
    public void ViewsPerformanceActions() {
        // Search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductSeller.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make Offline
                // Sign Out
                // Go To Login Activity
                makeMeOffline();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Profile Edit Activity For Seller
                startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));
            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Add Product Activity For Seller
                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));
            }
        });
        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load Products
                showProductsUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load Orders
                showOrdersUI();
            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get Selected Item
                                String selected = Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if (selected.equals("All")){
                                    // Load All
                                    loadAllProducts();
                                }
                                else {
                                    //Load Filtered
                                    loadFilteredProducts(selected);
                                }
                            }
                        })
                .show();
            }
        });
        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Options To Display In Dialog
                String[] options = {"All", "In Progress", "Completed", "Cancelled"};

                // Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Filter Orders")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Handel Item clicks
                                if (which==0){
                                    // All Clicked
                                    filteredOrdersTv.setText("Showing All Orders");
                                    adapterOrderShop.getFilter().filter(""); // Show All Orders
                                }
                                else {
                                    String optionClicked = options[which];
                                    filteredOrdersTv.setText("Showing " +optionClicked+ " Orders"); // e.g. Showing Completed Orders
                                    adapterOrderShop.getFilter().filter(optionClicked);
                                }
                            }
                        })
                        .show();
            }
        });

        // popup menu
        PopupMenu popupMenu = new PopupMenu(MainSellerActivity.this, moreBtn);
        // Add Menu Items To Our Menu
        popupMenu.getMenu().add("Settings");
        popupMenu.getMenu().add("Reviews");
        popupMenu.getMenu().add("Promotion Codes");
        // Handle Menu Item Click
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Settings"){

                    startActivity(new Intent(MainSellerActivity.this, SettingsActivity.class));
                }
                else if (item.getTitle() == "Reviews"){

                    // Open Same Reviews Activity As Used In User Main Activity
                    Intent intent = new Intent(MainSellerActivity.this, ShopReviewsActivity.class);
                    intent.putExtra("shopUid", ""+firebaseAuth.getUid());
                    startActivity(intent);
                }
                else if (item.getTitle() == "Promotion Codes"){
                    startActivity(new Intent(MainSellerActivity.this, PromotionCodesActivity.class));
                }
                return true;
            }
        });
        // Show More Options: Settings, Reviews, Promotion Codes
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Popup Menu
                popupMenu.show();
            }
        });
    }

    private void loadFilteredProducts(String selected) {
        productList = new ArrayList<>();
        // Get All Products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Before Getting Reset List
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String productCategory = ""+ds.child("productCategory").getValue();
                            // If selected category matches product category then add in list
                            if (selected.equals(productCategory)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }

                        }
                        // Setup Adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        // Set Adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void showProductsUI() {
        // Show Products UI and hide Orders UI
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabProductsTv.setBackgroundColor(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }
    private void showOrdersUI() {
        // Show Orders UI and hide Products UI
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabOrdersTv.setBackgroundColor(R.drawable.shape_rect04);

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user==null)
        {
            startActivity(new Intent(MainSellerActivity.this,LoginActivity.class));
            finish();
        }
        else
        {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            // Get Data From Database (Firebase)
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            String shopName = ""+ds.child("shopName").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String accountType = ""+ds.child("accountType").getValue();

                            // Set Data To Main Seller Activity Views
                            nameTv.setText(name+"("+accountType+")");
                            emailTv.setText(email);
                            shopNameTv.setText(shopName);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(profileIv);
                            }
                            catch (Exception exception){
                                profileIv.setImageResource(R.drawable.ic_store_gray);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void makeMeOffline() {
        //After Logout, make user offline
        progressDialog.setMessage("Logging out....");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //Update value to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Update successfully
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Failed Updating
                        progressDialog.dismiss();
                        Toast.makeText(MainSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}