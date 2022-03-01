package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    // Declaring Forgot Password Activity UI Views
    ImageButton backBtn;
    EditText emailEt;
    Button recoverBtn;

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    // Progress Dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ViewsInitialization();
        ViewsPerformanceActions();
    }

    // UI Views Initialization
    public void ViewsInitialization() {
        // Initialization Of Views
        backBtn = findViewById(R.id.backBtn);
        emailEt = findViewById(R.id.emailEt);
        recoverBtn = findViewById(R.id.recoverBtn);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialization Of Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    // UI Views Performance Actions
    public void ViewsPerformanceActions() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassword();
            }
        });
    }

    String email;
    private void recoverPassword() {
        email = emailEt.getText().toString().trim();
        // Validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(ForgotPasswordActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Sending Instructions to Reset Password!");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Instructions Sent
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Password Reset Instructions Sent to Your Email!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Failed sending Instructions
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}