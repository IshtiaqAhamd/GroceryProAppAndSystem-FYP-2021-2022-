package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;

public class LoginActivity extends AppCompatActivity {
    // Declaring Login Activity UI Views
    EditText emailEt, passwordEt;
    TextView forgotTv, noAccountTv;
    Button loginBtn;

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    // Progress Dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewsInitialization();
        ViewsPerformanceActions();
    }

    // UI Views Initialization
    public void ViewsInitialization() {
        // Initialization Of Views
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        forgotTv = findViewById(R.id.forgotTv);
        loginBtn = findViewById(R.id.loginBtn);
        noAccountTv = findViewById(R.id.noAccountTv);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialization Of Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    // UI Views Performance Actions
    public void ViewsPerformanceActions() {
        forgotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterUserActivity.class));
            }
        });
    }

    private String email, password;
    private void loginUser() {
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        // Validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "Please Enter Valid Email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Enter Password!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Logged In Successfully
                        makeMeOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed Logging In
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeMeOnline() {
        // After Login In, Make User Online
        progressDialog.setMessage("Checking User....");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","true");
        // Update Value To DB
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Update successfully
                        checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed Updating
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType() {
        //If User Is Seller, Start Main Seller Activity
        //If User Is Buyer, start Main User Activity
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String accountType = ""+ds.child("accountType").getValue();
                            if (accountType.equals("Seller"))
                            {
                                progressDialog.dismiss();
                                // User Is Seller
                                startActivity(new Intent(LoginActivity.this, MainSellerActivity.class));
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                // User Is Buyer
                                startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}