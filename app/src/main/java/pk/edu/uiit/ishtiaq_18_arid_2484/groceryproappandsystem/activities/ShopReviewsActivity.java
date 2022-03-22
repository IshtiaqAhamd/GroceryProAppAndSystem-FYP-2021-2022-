package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterReview;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelReview;

public class ShopReviewsActivity extends AppCompatActivity {
    // Declaring Shop Reviews Activity UI Views
    ImageButton backBtn;
    ImageView profileIv;
    TextView shopNameTv, ratingTv;
    RatingBar ratingBar;
    RecyclerView reviewsRv;

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    ArrayList<ModelReview> reviewArrayList; // Will Contains List Of All Reviews
    AdapterReview adapterReview;

    String shopUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_reviews);

        // Get Shop Uid From Intent
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
        ratingTv = findViewById(R.id.ratingTv);
        ratingBar = findViewById(R.id.ratingBar);
        reviewsRv = findViewById(R.id.reviewsRv);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        loadShopDetails(); // For Shop Name, Image
        loadReviews(); // For Reviews List, Average Rating
    }

    float ratingSum = 0;
    private void loadReviews() {
        // Initialization Of List
        reviewArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear List Before Adding Data Into It
                        reviewArrayList.clear();
                        ratingSum = 0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue()); // e.g. 4.5
                            ratingSum = ratingSum + rating; // For Average, Add(Addition Of) All Ratings, Later Will Divide It By Number Of Reviews

                            ModelReview modelReview = ds.getValue(ModelReview.class);
                            reviewArrayList.add(modelReview);
                        }
                        // Setup Adapter
                        adapterReview = new AdapterReview(ShopReviewsActivity.this, reviewArrayList);
                        //Set To RecyclerView
                        reviewsRv.setAdapter(adapterReview);

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;
                        ratingTv.setText(String.format("%.2f",avgRating) + " [" +numberOfReviews+ "]"); // e.g. 4.5 [15]
                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopName = ""+snapshot.child("shopName").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        shopNameTv.setText(shopName);
                        try {
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(profileIv);
                    }
                        catch (Exception e){
                        // If Anything Goes Wrong Setting Image (Exception Occur), Set Default Image
                        profileIv.setImageResource(R.drawable.ic_store_gray);
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
                // Go Back To Previous Activity
                onBackPressed();
            }
        });
    }
}