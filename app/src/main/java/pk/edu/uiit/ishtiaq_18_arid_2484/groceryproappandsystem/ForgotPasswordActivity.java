package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    // Declaring Forgot Password Activity UI Views
    ImageButton backBtn;
    EditText emailEt;
    Button recoverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ViewsInitialization();
        ViewsPerformanceActions();
    }

    // UI Views Initialization
    public void ViewsInitialization()
    {
        backBtn = findViewById(R.id.backBtn);
        emailEt = findViewById(R.id.emailEt);
        recoverBtn = findViewById(R.id.recoverBtn);
    }

    // UI Views Performance Actions
    public void ViewsPerformanceActions()
    {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}