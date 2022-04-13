package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.Constants;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterOrderedItem;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelOrderedItem;

public class OrderDetailsSellerActivity extends AppCompatActivity {
    // Declaring Add Product Activity UI Views
    ImageButton backBtn, editBtn, mapBtn;
    TextView orderIdTv, dateTv, orderStatusTv, emailTv, phoneTv, totalItemsTv, amountTv, addressTv;
    RecyclerView itemsRv;

    // For Intent Data
    String orderId, orderBy;

    // To Open Destination in Map
    String sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude;

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    // Ordered Items
    ArrayList<ModelOrderedItem> orderedItemArrayList;
    AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);

        // Get Data From Intent
        orderId = getIntent().getStringExtra("orderId");
        orderBy = getIntent().getStringExtra("orderBy");

        ViewsInitialization();
        ViewsPerformanceActions();
    }
    // UI Views Initialization
    public void ViewsInitialization() {
        // Initialization Of Views
        backBtn = findViewById(R.id.backBtn);
        editBtn = findViewById(R.id.editBtn);
        mapBtn = findViewById(R.id.mapBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        addressTv = findViewById(R.id.addressTv);
        itemsRv = findViewById(R.id.itemsRv);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        loadMyInfo();
        loadBuyerInfo();
        loadOrderDetails();
        loadOrderItems();

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

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit Order Status: In Progress, Completed or Cancelled
                editOrderStatusDialog();
            }
        });
    }

    private void loadMyInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sourceLatitude = ""+snapshot.child("latitude").getValue();
                        sourceLongitude = ""+snapshot.child("longitude").getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadBuyerInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Get Buyer Info
                        destinationLatitude = ""+snapshot.child("latitude").getValue();
                        destinationLongitude = ""+snapshot.child("longitude").getValue();
                        String email = ""+snapshot.child("email").getValue();
                        String phone = ""+snapshot.child("phone").getValue();

                        // Set Info
                        emailTv.setText(email);
                        phoneTv.setText(phone);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void openMap() {

        // saddr means Source Address
        // daddr means Destination Address
        String address = "https://maps.google.com/maps?saddr=" +sourceLatitude+ "," +sourceLongitude+ "&daddr=" +destinationLatitude+ "," +destinationLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);

    }

    private void loadOrderDetails() {
        // Load Detailed Info Of This Order, Based On Order Id
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        // Not Yet Sure Address (Testing Reaming In This Portion)
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String address = ""+snapshot.child("address").getValue();
                        addressTv.setText(address);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // Get Order Info
                        String orderBy = ""+snapshot.child("orderBy").getValue();
                        String orderCost = ""+snapshot.child("orderCost").getValue();
                        String orderId = ""+snapshot.child("orderId").getValue();
                        String orderStatus = ""+snapshot.child("orderStatus").getValue();
                        String orderTime = ""+snapshot.child("orderTime").getValue();
                        String orderTo = ""+snapshot.child("orderTo").getValue();
                        String deliveryFee = ""+snapshot.child("deliveryFee").getValue();
                        String latitude = ""+snapshot.child("latitude").getValue();
                        String longitude = ""+snapshot.child("longitude").getValue();
                        String discount = ""+snapshot.child("discount").getValue(); // In Previous Orders That Will Be null

                        if (discount.equals("null") || discount.equals("0")){
                            // Value Is Either null Or "0"
                            discount = "& Discount $0";
                        }
                        else {
                            discount = "& Discount $"+discount;
                        }

                        // Convert Time Stamp To Proper Format
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString(); // e.g. 20/05/2022 1:23 PM

                        // Order Status
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
                        amountTv.setText("$" + orderCost + "[Incuding Delivery Fee $" + deliveryFee +" " +discount+ "]");
                        dateTv.setText(formatedDate);

                        // findAddress(latitude, longitude); // To Find Delivery Address
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
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadOrderItems() {
        // Initialization List
        orderedItemArrayList = new ArrayList<>();
        // Load The Products/Items Of Order
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId).child("Items")
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
                        adapterOrderedItem = new AdapterOrderedItem(OrderDetailsSellerActivity.this, orderedItemArrayList);

                        // Set Adapter
                        itemsRv.setAdapter(adapterOrderedItem);

                        // Set Total Number Of Items/Products In Order
                        totalItemsTv.setText("" + snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void editOrderStatusDialog() {
        // Options To Display In Dialog
        String[] options = {"In Progress", "Completed", "Cancelled"};
        // Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Order Status")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handel Item Clicks
                        String selectedOption = options[which];
                        editOrderStatus(selectedOption);
                    }
                })
                .show();

    }

    private void editOrderStatus(String selectedOption) {
        // Setup Data To Put In Firebase DB
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", ""+selectedOption);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String message = "Order Is Now "+selectedOption;
                        // Status Updated
                        Toast.makeText(OrderDetailsSellerActivity.this, message, Toast.LENGTH_SHORT).show();

                        preparedNotificationMessage(orderId, message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed Updating Status, Show Reason
                        Toast.makeText(OrderDetailsSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Prepared Notification Message
    private void preparedNotificationMessage(String orderId, String message){
        // When User seller Change The Order Status In Progress/Completed/Cancelled, Sand Notification To Buyer

        // Prepare Data For Notification
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC; // Must Be Same Subscribed By User
        String NOTIFICATION_TITLE = "Your Order " + orderId;
        String NOTIFICATION_MESSAGE = ""+message;
        String NOTIFICATION_TYPE = "OrderStatusChanged";

        // Prepare JSON (What To Send And Where To Sand)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {

            // What To Sand
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", orderBy);
            // Since We Are Logged In As Seller To Change Order Status So Current User uid Is Seller uid
            notificationBodyJo.put("sellerUid", firebaseAuth.getUid());
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
        sendFcmNotification(notificationJo);
    }

    private void sendFcmNotification(JSONObject notificationJo) {
        // Send Volley Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Notification Sent

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Notification Failed
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                // Put Required Headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key" + Constants.FCM_TOPIC);
                return headers;
            }
        };

        // Enque The Volley Request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}