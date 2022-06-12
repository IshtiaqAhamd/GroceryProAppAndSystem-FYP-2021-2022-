package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.Constants;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;

public class SettingsActivity extends AppCompatActivity {
    // Declaring Settings Activity UI Views
    ImageButton backBtn;
    SwitchCompat fcmSwitch;
    TextView notificationStatusTv;

    private static final String enabledMessage = "Notifications are enabled";
    private static final String disabledMessage = "Notifications are disabled";

    //
    boolean isChecked  = false;

    // FirebaseAuth
    FirebaseAuth firebaseAuth;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ViewsInitialization();
        ViewsPerformanceActions();
    }
    // UI Views Initialization
    public void ViewsInitialization() {

        // Initialization Of Views
        backBtn = findViewById(R.id.backBtn);
        fcmSwitch = findViewById(R.id.fcmSwitch);
        notificationStatusTv = findViewById(R.id.notificationStatusTv);

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialization Of SharedPreferences
        sp = getSharedPreferences("SETTINGS_SP", MODE_PRIVATE);

        // Check Last Selected Option; True/Failed
        isChecked = sp.getBoolean("FCM_ENABLED", false);
        fcmSwitch.setChecked(isChecked);
        if (isChecked){
            // was enabled
            notificationStatusTv.setText(enabledMessage);
        }
        else {
            // was disabled
            notificationStatusTv.setText(disabledMessage);
        }

    }
    // UI Views Performance Actions
    public void ViewsPerformanceActions() {

        // Go Back To The Previous Activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fcmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // Checked, Enable Notification
                    subscribeToTopic();
                }
                else {
                    // unchecked, disable Notification
                    unSubscribeToTopic();
                }
            }
        });
    }

    public void subscribeToTopic(){

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Subscribed Successfully
                        // Save Setting In Shared Preferences
                        spEditor =  sp.edit();
                        spEditor.putBoolean("FCM_ENABLED", true);
                        spEditor.apply();

                        Toast.makeText(SettingsActivity.this, ""+enabledMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(enabledMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed Subscribing
                        Toast.makeText(SettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void unSubscribeToTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Unsubscribed
                        // Save Setting In Shared Preferences
                        spEditor =  sp.edit();
                        spEditor.putBoolean("FCM_ENABLED", false);
                        spEditor.apply();

                        Toast.makeText(SettingsActivity.this, ""+disabledMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(disabledMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed Unsubscribing
                        Toast.makeText(SettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}