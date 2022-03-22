package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;

public class WriteReviewActivity extends AppCompatActivity {
    // Declaring Write Review Activity UI Views
    ImageButton backBtn;
    ImageView profileIv;
    TextView shopNameTv;
    RatingBar ratingBar;
    EditText reviewEt;
    FloatingActionButton submitBtn;

    String shopUid;

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // Get Shop UID From Intent
        shopUid = getIntent().getStringExtra("shopUid");

        ViewsInitialization();
        ViewsPerformanceActions();
    }

    // UI Views Initialization
    public void ViewsInitialization() {
        // Initialization Of Views
        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        ratingBar = findViewById(R.id.ratingBar);
        reviewEt = findViewById(R.id.reviewEt);
        submitBtn = findViewById(R.id.submitBtn);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Load Shop Info: Shop Name, Shop Image
        loadShopInfo();
        // If User Has Write Review To This Shop, Load It
        loadMyReview();

    }

    private void loadShopInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get Shop Info
                String shopName = ""+snapshot.child("shopName").getValue();
                String shopImage = ""+snapshot.child("profileImage").getValue();

                // Set Shop Info
                shopNameTv.setText(shopName);
                try {
                    Picasso.get().load(shopImage).placeholder(R.drawable.ic_store_gray).into(profileIv);
                }
                catch (Exception e){
                    profileIv.setImageResource(R.drawable.ic_store_gray);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMyReview() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            // My Review Is Available In This Shop

                            // Get Review Details
                            String uid = ""+snapshot.child("uid").getValue();
                            String ratings = ""+snapshot.child("ratings").getValue();
                            String review = ""+snapshot.child("review").getValue();
                            String timestamp = ""+snapshot.child("timestamp").getValue();

                            // Set Review Details To Our UI
                            float myRating = Float.parseFloat(ratings);
                            ratingBar.setRating(myRating);
                            reviewEt.setText(review);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    // UI Views Performance Actions
    public void ViewsPerformanceActions() {
        // Go Back To Previous Activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Input Data
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void inputData() {
        String ratings = ""+ratingBar.getRating();
        String review = reviewEt.getText().toString().trim();

        // For Time Of Review
        String timestamp = ""+System.currentTimeMillis();

        // Setup Data In Hashmap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+ firebaseAuth.getUid());
        hashMap.put("ratings", ""+ ratings); // e.g. 4.5
        hashMap.put("review", ""+ review); // e.g Good Service
        hashMap.put("timestamp", ""+ timestamp);

        // Put To Database: DB > Users > ShopUid > Rating
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Ratings").child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Review Added To Database
                        Toast.makeText(WriteReviewActivity.this, "Review Published Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed Adding Review To Database
                        Toast.makeText(WriteReviewActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}