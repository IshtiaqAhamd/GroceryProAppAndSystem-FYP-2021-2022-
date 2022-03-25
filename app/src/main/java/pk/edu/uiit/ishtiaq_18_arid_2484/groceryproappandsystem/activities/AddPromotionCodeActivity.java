package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;

public class AddPromotionCodeActivity extends AppCompatActivity {

    // Declaring  Add Promotion Code Activity  UI Views
    ImageButton backBtn;
    EditText promoCodeEt, promoDescriptionEt, promoPriceEt, minimumOrderPriceEt;
    TextView expireDateTv;
    Button addBtn;

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    // Progress Dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion_code);

        ViewsInitialization();
        ViewsPerformanceActions();

    }

    // UI Views Initialization
    public void ViewsInitialization() {

        // Initialization Of Views
        backBtn = findViewById(R.id.backBtn);
        promoCodeEt = findViewById(R.id.promoCodeEt);
        promoDescriptionEt = findViewById(R.id.promoDescriptionEt);
        promoPriceEt = findViewById(R.id.promoPriceEt);
        minimumOrderPriceEt = findViewById(R.id.minimumOrderPriceEt);
        expireDateTv = findViewById(R.id.expireDateTv);
        addBtn = findViewById(R.id.addBtn);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialization Of Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    // UI Views Performance Actions
    public void ViewsPerformanceActions() {

        //Handle Click, Go Back To The Previous Activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Handle Click, Pick Date
        expireDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicKDialog();
            }
        });
        // Handle Click, Add Promotion Code To Firebase DB
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void datePicKDialog() {
        // Get Current Date To Set On Calender
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Date Pick Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DecimalFormat mFormat = new DecimalFormat("00");
                String pDay = mFormat.format(dayOfMonth);
                String pMonth = mFormat.format(monthOfYear);
                String pYear = ""+year;
                String pDate = pDay +"/"+ pMonth +"/"+ pYear; // e.g. 25/03/2022
                expireDateTv.setText(pDate);
            }
        },mYear, mMonth, mDay);

        // Show Dialog
        datePickerDialog.show();
        //Disable Past Dates Selection On Calender
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

   String description, promoCode, promoPrice, minimumOrderPrice, expireDate;
    private void inputData(){

        // Input Data
        promoCode =promoCodeEt.getText().toString().trim();
        description =promoDescriptionEt.getText().toString().trim();
        promoPrice =promoPriceEt.getText().toString().trim();
        minimumOrderPrice =minimumOrderPriceEt.getText().toString().trim();
        expireDate =expireDateTv.getText().toString().trim();

        // Validate From Data
        if (TextUtils.isEmpty(promoCode)){
            Toast.makeText(this, "Enter Discount Code", Toast.LENGTH_SHORT).show();
            return; //Don't Proceed Further
        }
        if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show();
            return; //Don't Proceed Further
        }
        if (TextUtils.isEmpty(promoPrice)){
            Toast.makeText(this, "Enter Promotion Price", Toast.LENGTH_SHORT).show();
            return; //Don't Proceed Further
        }
        if (TextUtils.isEmpty(minimumOrderPrice)){
            Toast.makeText(this, "Enter Minimum Order Price", Toast.LENGTH_SHORT).show();
            return; //Don't Proceed Further
        }
        if (TextUtils.isEmpty(expireDate)){
            Toast.makeText(this, "Enter Expire Date", Toast.LENGTH_SHORT).show();
            return; //Don't Proceed Further
        }
        // All Fields Entered, Add Date To DB
        addDataToDb();
    }

    private void addDataToDb() {
        progressDialog.setMessage("Adding Promotion Code...");
        progressDialog.dismiss();

        String timestamp = ""+System.currentTimeMillis();

        // Setup Data To Add In DB
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", "" + timestamp);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("description", "" + description);
        hashMap.put("promoCode", "" + promoCode);
        hashMap.put("promoPrice", "" + promoPrice);
        hashMap.put("minimumOrderPrice", "" + minimumOrderPrice);
        hashMap.put("expireDate", "" + expireDate);

        // Initialization DB Reference Users -> Current User -> Promotions -> PromoID -> Promo Date
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Promotions").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Code Added
                        progressDialog.dismiss();
                        Toast.makeText(AddPromotionCodeActivity.this, "Promotion Code Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Adding Code Failed
                        progressDialog.dismiss();
                        Toast.makeText(AddPromotionCodeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}