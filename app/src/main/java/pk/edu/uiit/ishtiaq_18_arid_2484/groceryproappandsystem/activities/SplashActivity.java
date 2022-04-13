package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;

public class SplashActivity extends AppCompatActivity {
    // Declaring Splash Activity UI Views
    ImageView splashImage;

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make Full Screen Before Setting The Layout In Activity (setContentView)
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        // Start Login Activity After 4 Seconds
        splashImage = findViewById(R.id.splashIcon);
        splashImage.animate().scaleX(1.5f).scaleY(1.5f).setDuration(3000);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user==null)
                {
                    // User Not Logged In Start Login Activity
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
                else
                {
                    // User Is Logger in, Check User Type
                    checkUserType();
                }
            }
        },4000);
    }
    private void checkUserType() {
        //If User Is Seller, Start Main Seller Activity
        //If User Is Buyer, start Main User Activity
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String accountType = ""+snapshot.child("accountType").getValue();
                        if (accountType.equals("Seller"))
                        {
                            // User Is Seller
                            startActivity(new Intent(SplashActivity.this, MainSellerActivity.class));
                            finish();
                        }
                        else
                        {
                            // User Is Buyer
                            startActivity(new Intent(SplashActivity.this, MainUserActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}