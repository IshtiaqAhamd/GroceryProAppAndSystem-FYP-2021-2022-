package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;

public class PromotionCodesActivity extends AppCompatActivity {
    // Declaring Add Product Activity UI Views
    ImageButton backBtn, addPromoBtn;
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
    }
}