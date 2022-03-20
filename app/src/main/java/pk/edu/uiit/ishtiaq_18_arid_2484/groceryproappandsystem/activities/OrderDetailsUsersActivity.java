package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterOrderedItem;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelOrderedItem;

public class OrderDetailsUsersActivity extends AppCompatActivity {
    String orderTo, orderId;

    // Declaring Order Details Users Activity UI Views
    ImageButton backBtn, writeReviewBtn;
    TextView orderIdTv, dateTv, orderStatusTv, shopNameTv, totalItemsTv, amountTv, addressTv;
    RecyclerView itemsRv;

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    ArrayList<ModelOrderedItem> orderedItemArrayList;
    AdapterOrderedItem  adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_users);

        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo"); // orderTo Contains uid Of The Shop Where We Placed Order
        orderId = intent.getStringExtra("orderId");

        ViewsInitialization();
        ViewsPerformanceActions();
    }

    // UI Views Initialization
    public void ViewsInitialization() {
        // Initialization Of Views
        backBtn = findViewById(R.id.backBtn);
        writeReviewBtn = findViewById(R.id.writeReviewBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        addressTv = findViewById(R.id.addressTv);
        itemsRv = findViewById(R.id.itemsRv);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        loadShopInfo();
        loadOrderDetails();
        loadOrderItems();
    }

    private void loadOrderItems() {
        // Initialization List

        orderedItemArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderedItemArrayList.clear(); // Before Loading Items Clear List
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);
                            //Add To List
                            orderedItemArrayList.add(modelOrderedItem);
                        }

                        // All Items Added To List
                        // SetUp Adapter
                        adapterOrderedItem = new AdapterOrderedItem(OrderDetailsUsersActivity.this, orderedItemArrayList);
                        // Set Adapter
                        itemsRv.setAdapter(adapterOrderedItem);

                        // Set Items Count
                        totalItemsTv.setText("" + snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    // UI Views Performance Actions
    public void ViewsPerformanceActions() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Handle writeReviewBtn Click, Start Write Review Activity
        writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailsUsersActivity.this, WriteReviewActivity.class);
                intent.putExtra("shopUid", orderTo); // To Write Review To a Shop We Must Have UID Of Shop
                startActivity(intent);
            }
        });
    }

    private void loadOrderDetails() {
        // Load Order Details
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // Get Data
                        String orderBy = ""+snapshot.child("orderBy").getValue();
                        String orderCost = ""+snapshot.child("orderCost").getValue();
                        String orderStatus = ""+snapshot.child("orderStatus").getValue();
                        String orderTime = ""+snapshot.child("orderTime").getValue();
                        String orderTo = ""+snapshot.child("orderTo").getValue();
                        String deliveryFee = ""+snapshot.child("deliveryFee").getValue();
                        String latitude = ""+snapshot.child("latitude").getValue();
                        String longitude = ""+snapshot.child("longitude").getValue();
                        String address = ""+snapshot.child("address").getValue();

                        // Convert Time Stamp To Proper Format
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString(); // e.g. 20/05/2022 1:23 PM
                        if (orderStatus.equals("In Progress")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                        else if (orderStatus.equals("Completed")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorGreen));
                        }
                        else  if (orderStatus.equals("Cancelled")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorRed));
                        }

                        //Set Data
                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText("$" + orderCost + "[Incuding Delivery Fee $" + deliveryFee + "]");
                        dateTv.setText(formatedDate);

                        findAddress(latitude, longitude);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopInfo() {
        // Get Shop Info
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopName = ""+snapshot.child("shopName").getValue();
                        shopNameTv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void findAddress(String latitude, String longitude) {
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        // Find Address, Country, State and City
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            String address = addresses.get(0).getAddressLine(0); // Complete Address
            addressTv.setText(address);
        }
        catch (Exception e){

        }
    }
}