package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.Constants;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.DataBaseHelper;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterCartItem;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterProductUser;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterWishlistItem;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelCartItem;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelProduct;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelWishlistItem;

public class ShopDetailsActivity extends AppCompatActivity {
    // Declaring  Shop Details Activity  UI Views
    ImageView shopIv;
    TextView shopNameTv, phoneTv, emailTv, openCloseTv, deliveryFeeTv, addressTv, filteredProductsTv, cartCountTv;
    ImageButton callBtn, mapBtn, cartBtn, backBtn, filterProductBtn, reviewsBtn, wishlistBtn, chatBtn;
    EditText searchProductEt;
    RecyclerView productsRv;
    RatingBar ratingBar;

    String shopUid;
    String myLatitude, myLongitude, myPhone;
    String shopName, shopEmail, shopPhone, shopAddress, shopLatitude, shopLongitude;
    public String deliveryFee;

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    // Progress Dialog
    private ProgressDialog progressDialog;

    // Products
    ArrayList<ModelProduct> productsList;
    AdapterProductUser adapterProductUser;

    // Cart
    ArrayList<ModelCartItem> cartItemList;
    AdapterCartItem adapterCartItem;
    // Wishlist
    ArrayList<ModelWishlistItem> wishlistItems;
    AdapterWishlistItem adapterWishlistItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        ViewsInitialization();
        ViewsPerformanceActions();
        cartCount();
    }

    // UI Views Initialization
    public void ViewsInitialization() {
        // Initialization Of Views
        shopIv = findViewById(R.id.shopIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        addressTv = findViewById(R.id.addressTv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        cartBtn = findViewById(R.id.cartBtn);
        backBtn = findViewById(R.id.backBtn);
        reviewsBtn = findViewById(R.id.reviewsBtn);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        wishlistBtn = findViewById(R.id.wishlistBtn);
        chatBtn = findViewById(R.id.chatBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        productsRv = findViewById(R.id.productsRv);
        cartCountTv = findViewById(R.id.cartCountTv);
        ratingBar = findViewById(R.id.ratingBar);

        // Get Uid Of The Shop From Intent
        shopUid = getIntent().getStringExtra("shopUid");

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialization Of Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        loadReviews(); // Average Rating: Set On ratingbar
        // Each Shop Have Its Own Products and Orders, So If User Add Items To Cart And Go Back
        // And Open Cart Different Shop Then Cart Should Bbe Different
        // So Delete Cart Data Whenever User Open This Activity
        deleteCartData();

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
                    adapterProductUser.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go Previous Activity
                onBackPressed();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Cart Dialog
                showCartDialog();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });


        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetailsActivity.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get Selected Item
                                String selected = Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if (selected.equals("All")){
                                    // Load All
                                    loadShopProducts();
                                }
                                else {
                                    //Load Filtered
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        // Handle reviewsBtn Click, Open Reviews Activity
        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass Shop uid To Show Its Activity
                Intent intent = new Intent(ShopDetailsActivity.this, ShopReviewsActivity.class);
                intent.putExtra("shopUid", shopUid);
                startActivity(intent);
            }
        });
        wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Cart Dialog
                showWishlistDialog();
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopDetailsActivity.this, ChatHomeActivity.class));
            }
        });
    }

    float ratingSum = 0;

    private void loadReviews() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear List Before Adding Data Into It
                        ratingSum = 0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue()); // e.g. 4.5
                            ratingSum = ratingSum + rating; // For Average, Add(Addition Of) All Ratings, Later Will Divide It By Number Of Reviews
                        }

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;
                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteCartData() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    }

    public void cartCount(){
        // Keep It Public So We Can Access In Adapter
        // Get Cart Count
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        int count = dataBaseHelper.cartCount().getCount();
        if (count<=0){
            // No Item In Cart, Hide Cart Count TextView
            cartCountTv.setVisibility(View.GONE);
        }
        else {
            // Have Items In Cart, Hide Cart Count TextView and Set Count
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText("" + count); // Concatenate With String, Because We can't Set Integer In TextView
        }

    }

   public double allTotalPrice = 0.00;

    // Need To Access Theses Views In Adapter So Making Public
    public TextView sTotalTv, dFeeTv, allTotalPriceTv, promoDescriptionTv, discountTv;
    public EditText promoCodeEt;
    public Button applyBtn;

    private void showCartDialog() {
        // Initialization List
        cartItemList = new ArrayList<>();

        // Inflate Cart Layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);

        // Initialization Views

        TextView shopNameTv = view.findViewById(R.id.shopNameTv);
        promoCodeEt = view.findViewById(R.id.promoCodeEt);
        promoDescriptionTv = view.findViewById(R.id.promoDescriptionTv);
        discountTv = view.findViewById(R.id.discountTv);
        FloatingActionButton validateBtn = view.findViewById(R.id.validateBtn);
        applyBtn = view.findViewById(R.id.applyBtn);

        RecyclerView cartItemsRv = view.findViewById(R.id.cartItemsRv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        dFeeTv = view.findViewById(R.id.dFeeTv);
        allTotalPriceTv = view.findViewById(R.id.totalTv);
        Button checkoutBtn = view.findViewById(R.id.checkoutBtn);

        // Whenever Cart Dialog Shows, Check If Promo Code Is Applied Or Not
        if (isPromoCodeApplied){
            // Applied
            promoDescriptionTv.setVisibility(View.VISIBLE);
            applyBtn.setVisibility(View.VISIBLE);
            applyBtn.setText("Appled");
            promoCodeEt.setText(promoCode);
            promoDescriptionTv.setText(promoDescription);
        }
        else{
            // Apply
            promoDescriptionTv.setVisibility(View.GONE);
            applyBtn.setVisibility(View.GONE);
            applyBtn.setText("Apply");
        }

        // Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set View To Dialog
        builder.setView(view);

        shopNameTv.setText(shopName);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        // Get All Data From Database (Sqlite)
        Cursor res = dataBaseHelper.getCartData();
        if (res.getCount() == 0){
            Toast.makeText(this, "Cart Is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        while(res.moveToNext()){

            String id = res.getString(0);
            String pId = res.getString(1);
            String name = res.getString(2);
            String price = res.getString(3);
            String cost = res.getString(4);
            String quantity = res.getString(5);

           allTotalPrice = allTotalPrice + Double.parseDouble(cost);
            ModelCartItem modelCartItem = new ModelCartItem(
                    "" + id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );
            cartItemList.add(modelCartItem);


        }
        // Setup Adapter
        adapterCartItem = new AdapterCartItem(this,cartItemList);
        // Set To Recyclerview
        cartItemsRv.setAdapter(adapterCartItem);

        if (isPromoCodeApplied){
            priceWithDiscount();
        }
        else {
            priceWithoutDiscount();
        }

        // Show Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        // Reset Total Price On Dialog Dismiss
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.00;
            }
        });

        // Place Order
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First Validate Delivery Address
                if(myLatitude.equals("") || myLatitude.equals("null") || myLongitude.equals("") || myLongitude.equals("null")){
                    // User Didn't Enter Address In Profile
                    Toast.makeText(ShopDetailsActivity.this, "Please Enter Your Address In Your Profile Before Placing Order", Toast.LENGTH_SHORT).show();
                    return; // Don't Proceed Further
                }
                if(myPhone.equals("") || myPhone.equals("null")){
                    // User Didn't Enter Phone Number In Profile
                    Toast.makeText(ShopDetailsActivity.this, "Please Enter Your Phone Number In Your Profile Before Placing Order", Toast.LENGTH_SHORT).show();
                    return; // Don't Proceed Further
                }
                if (cartItemList.size() == 0){
                    // Cart List IS Empty
                    Toast.makeText(ShopDetailsActivity.this, "No Item In Cart", Toast.LENGTH_SHORT).show();
                    return; // Don't Proceed Further
                }
                submitOrder();
            }
        });

        // Start Validating Promo Code When Validate Button Is Applied
        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*   (***** Checking Validity Button *****)
                * 1) Get Code From EditText
                     If Not Empty: Promotion May Be Applied, Otherwise No Promotion
                * 2) Check If Code Is Valid i.e Available id Seller's Promotion DB
                     If Available: Promotion May Be Applied, Otherwise No Promotion
                * 3) Check If Expired Or Not
                     If Not Expired: Promotion May Be Applied, Otherwise No Promotion
                * 4) Check If Minimum Order Price
                     If minimumOrderPrice is >= SubTotal Price:  Promotion Available, Otherwise No Promotion */

                String promotionCode = promoCodeEt.getText().toString().trim();
                if (TextUtils.isEmpty(promotionCode)){
                    Toast.makeText(ShopDetailsActivity.this, "Please Enter Promo Code!!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    checkCodeAvailability(promotionCode);
                }
            }
        });

        // Applied Code If Valid, No Need To Check If Valid Or Not, Because This Button Will Be Visible Only If Code Is Valid
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPromoCodeApplied = true;
                applyBtn.setText("Applied");

                priceWithDiscount();
            }
        });
    }

    private void priceWithDiscount() {
        discountTv.setText("$" + promoPrice);
        dFeeTv.setText("$" + deliveryFee);
        sTotalTv.setText("$" + String.format("%.2f", allTotalPrice));
        allTotalPriceTv.setText("$" + (allTotalPrice + Double.parseDouble(deliveryFee.replace("$","")) - Double.parseDouble(promoPrice)));
    }

    private void priceWithoutDiscount() {
        discountTv.setText("$0");
        dFeeTv.setText("$" + deliveryFee);
        sTotalTv.setText("$" + String.format("%.2f", allTotalPrice));
        allTotalPriceTv.setText("$" + (allTotalPrice + Double.parseDouble(deliveryFee.replace("$",""))));
    }

    public boolean isPromoCodeApplied = false;
    public String promoId, promoTimestamp, promoCode, promoDescription, promoExpDate, promoMinimumOrderPrice, promoPrice;
    private void checkCodeAvailability(String promotionCode){
        // promotionCode Is Promo Entered By Buyer/User
        // Progress Bar
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Checking Promo Code...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Promo IS Not Applied Yed

        isPromoCodeApplied = false;
        applyBtn.setText("Apply");
        priceWithoutDiscount();

        // Check Promo Code Availability
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Promotions").orderByChild("promoCode").equalTo(promotionCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Check If Promo Code Exist
                        if (snapshot.exists()){
                            // Promo Code Exist
                            progressDialog.dismiss();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                promoId = ""+ds.child("id").getValue();
                                promoTimestamp = ""+ds.child("timestamp").getValue();
                                promoCode = ""+ds.child("promoCode").getValue();
                                promoCode = ""+ds.child("promoCode").getValue();
                                promoDescription = ""+ds.child("description").getValue();
                                promoExpDate = ""+ds.child("expireDate").getValue();
                                promoMinimumOrderPrice = ""+ds.child("minimumOrderPrice").getValue();
                                promoPrice = ""+ds.child("promoPrice").getValue();

                                // Now Check If Code Is Expired Or Not
                                checkCodeExpiredDate();
                            }
                        }
                        else {
                            // Entered Promo Code Doesn't Exist
                            progressDialog.dismiss();
                            Toast.makeText(ShopDetailsActivity.this, "Invalid Promo Code!!!", Toast.LENGTH_SHORT).show();
                            applyBtn.setVisibility(View.GONE);
                            promoDescriptionTv.setVisibility(View.GONE);
                            promoDescriptionTv.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void checkCodeExpiredDate() {
        // Get Current Date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // It Start From 0 Instead Of 1 that's Why Did +1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Concatenate Date
        String todayDate = day + "/" + month + "/" + year; // e.g. 26/03/2022

        /*---------- Check For Expiry ----------*/
        try {
            SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = sdformat.parse(todayDate);
            Date expireDate = sdformat.parse(promoExpDate);
            // Compare Dates
            if (expireDate.compareTo(currentDate) > 0){
                // Date 1 occur After Date 2 (i.e. Not Expire Date)
                checkMinimumOrderPrice();
            }
            else if (expireDate.compareTo(currentDate) < 0){
                // Date 1 occur Before Date 2 (i.e. Expire Date)
                Toast.makeText(this, "The Promotion Code Is Expired On "+promoExpDate, Toast.LENGTH_SHORT).show();
                applyBtn.setVisibility(View.GONE);
                promoDescriptionTv.setVisibility(View.GONE);
                promoDescriptionTv.setText("");

            }
            else if (expireDate.compareTo(currentDate) == 0){
                // Both Date Are Equal (i.e. Not Expire Date)
                checkMinimumOrderPrice();
            }
        }
        catch (Exception e){
            // If Anything Goes Wrong Causing Exception While Comparing Current Date And Expiry Date
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            applyBtn.setVisibility(View.GONE);
            promoDescriptionTv.setVisibility(View.GONE);
            promoDescriptionTv.setText("");
        }
    }

    private void checkMinimumOrderPrice() {
        // Each Promo Code  Have Minimum Order Price Requirement, If Order Is Less Tje Required then Don't Allow To Apply Code
       if (Double.parseDouble(String.format("%.2f", allTotalPrice)) < Double.parseDouble(promoMinimumOrderPrice)){
           // Current Order Price Is Less Then Minimum Order Price Required By Promo Code, So Don't Allow To Apply
           Toast.makeText(this, "This Code Is Valid For Order With Minimum Amount: $"+promoMinimumOrderPrice, Toast.LENGTH_SHORT).show();
           applyBtn.setVisibility(View.GONE);
           promoDescriptionTv.setVisibility(View.GONE);
           promoDescriptionTv.setText("");
       }
       else{
           // Current Order Price Is Equal Or Greater Then Minimum Order Price Required By Promo Code (Allow To Apply)
           applyBtn.setVisibility(View.VISIBLE);
           promoDescriptionTv.setVisibility(View.VISIBLE);
           promoDescriptionTv.setText(promoDescription);
       }

    }

    private void submitOrder() {
        // Show Progress Dialog
        progressDialog.setMessage("Placing Order...");
        progressDialog.show();

        //For Order ID And Order Time
        final String timestamp = ""+System.currentTimeMillis();

        String cost = allTotalPriceTv.getText().toString().trim().replace("$", ""); // Remove $ If Contains
        // Add Latitude, Longitude Of User To Each Other | Delete Previous Orders From Firebase or Add Manually To Them

        // Setup Order Data
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", "" + timestamp);
        hashMap.put("orderTime", "" + timestamp);
        hashMap.put("orderStatus", "In Progress"); // In Progress/Completed/Cancelled
        hashMap.put("orderCost", ""+cost);
        hashMap.put("orderBy", ""+firebaseAuth.getUid());
        hashMap.put("orderTo", ""+shopUid);
        hashMap.put("Latitude", ""+myLatitude);
        hashMap.put("Longitude", ""+myLongitude);
        hashMap.put("deliveryFee", ""+ deliveryFee); // Include Delivery Fee In Each Order
        if (isPromoCodeApplied){
            // Promo Applied
            hashMap.put("discount", ""+ promoPrice); // Include Promo Price
        }
        else {
            // Promo Not Applied, Include Price 0
            hashMap.put("discount", "0"); // Include Promo Price
        }

        // Add To Database (Firebase)
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        reference.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Order INFO Added Now And Order Items
                        for (int i=0; i<cartItemList.size(); i++){
                            String pId = cartItemList.get(i).getpId();
                            String id = cartItemList.get(i).getId();
                            String cost = cartItemList.get(i).getCost();
                            String name = cartItemList.get(i).getName();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", name);
                            hashMap1.put("cost", cost);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", quantity);

                            reference.child(timestamp).child("Items").child(pId).setValue(hashMap1);
                        }

                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailsActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();

                        preparedNotificationMessage(timestamp);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed Placing Order
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openMap() {

        // saddr means Source Address
        // daddr means Destination Address
        String address = "https://maps.google.com/maps?saddr=" +myLatitude+ "," +myLongitude+ "&daddr=" +shopLatitude+ "," +shopLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);

    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(shopPhone))));
        Toast.makeText(this, ""+shopPhone, Toast.LENGTH_SHORT).show();
    }

    private void loadMyInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){

                            // Set User Data
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            myPhone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String city = ""+ds.child("city").getValue();
                            myLatitude = ""+ds.child("latitude").getValue();
                            myLongitude = ""+ds.child("longitude").getValue();

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get Shop Data
                String name = ""+snapshot.child("name").getValue();
                shopName = ""+snapshot.child("shopName").getValue();
                shopEmail = ""+snapshot.child("email").getValue();
                shopPhone = ""+snapshot.child("phone").getValue();
                shopLatitude = ""+snapshot.child("latitude").getValue();
                shopLongitude = ""+snapshot.child("longitude").getValue();
                shopAddress = ""+snapshot.child("address").getValue();
                deliveryFee = ""+snapshot.child("deliveryFee").getValue();
                String profileImage = ""+snapshot.child("profileImage").getValue();
                String shopOpen = ""+snapshot.child("shopOpen").getValue();
                // Set Shop Data
                shopNameTv.setText(shopName);
                emailTv.setText(shopEmail);
                deliveryFeeTv.setText("Delivery Fee: $" + deliveryFee);
                addressTv.setText(shopAddress);
                phoneTv.setText(shopPhone);

                if (shopOpen.equals("true")){
                    openCloseTv.setText("Open");
                }

                else {
                    openCloseTv.setText("Closed");
                }

                try {
                    Picasso.get().load(profileImage).into(shopIv);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShopProducts() {
        // Initialization List
        productsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear List Before Adding Items
                        productsList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }

                        // Setup Adapter
                        adapterProductUser = new AdapterProductUser(ShopDetailsActivity.this, productsList);
                        // Set Adapter
                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    // Prepared Notification Message
    private void preparedNotificationMessage(String orderId){
       // When User Places Order, Sand Notification To Seller

        // Prepare Data For Notification
        String NOTIFICATION_TOPIC = "/topics/" +Constants.FCM_TOPIC; // Must Be Same Subscribed By User
        String NOTIFICATION_TITLE = "New Order " + orderId;
        String NOTIFICATION_MESSAGE = "Congratulation...! You Have New Order.";
        String NOTIFICATION_TYPE = "NewOrder";

        // Prepare JSON (What To Send And Where To Sand)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {

            // What To Sand
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            // Since We Are Logged In As Buyer To Place Order So Current User uid Is Buyer uid
            notificationBodyJo.put("buyerUid", firebaseAuth.getUid());
            notificationBodyJo.put("sellerUid", shopUid);
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);

            // Where To Sand
            notificationJo.put("to", NOTIFICATION_TOPIC); // To All who Subscribed To This Topic
            notificationJo.put("data", notificationBodyJo);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        sendFcmNotification(notificationJo, orderId);
    }

    private void sendFcmNotification(JSONObject notificationJo, String orderId) {
        // Send Volley Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // After Sending FCM Start Order Details Activity
                // Open Order Details, We Need To Keys there, orderId, orderTo
                Intent intent = new Intent(ShopDetailsActivity.this, OrderDetailsUsersActivity.class);
                intent.putExtra("orderTo", shopUid);
                intent.putExtra("orderId", orderId);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // If Failed Sending FCM, Still Start Order Details Activity
                Intent intent = new Intent(ShopDetailsActivity.this, OrderDetailsUsersActivity.class);
                intent.putExtra("orderTo", shopUid);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                // Put Required Headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key=" + Constants.FCM_KEY);
                return headers;
            }
        };

        // Enqueue The Volley Request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showWishlistDialog(){
        // Initialization List
        wishlistItems = new ArrayList<>();

        // Inflate Cart Layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_wishlist, null);

        // Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set View To Dialog
        builder.setView(view);

        // Setup Adapter
        adapterWishlistItem = new AdapterWishlistItem(this,wishlistItems);
        // Set To Recyclerview
        // Show Dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}