package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class RegisterUserActivity extends AppCompatActivity implements LocationListener {
    // Declaring Register User Activity UI Views
    ImageButton backBtn,gpsBtn;
    ImageView profileIv;
    EditText nameEt, phoneEt, countryEt, stateEt, cityEt, addressEt, emailEt, passwordEt, cPasswordEt;
    Button registerBtn;
    TextView registerSellerTv;


    // Permission Constant
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    // Image Pick Constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // Image Picked URI
    private Uri image_uri;

    // Permission Arrays
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private double latitude, longitude;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        ViewsInitialization();
        ViewsPerformanceActions();
    }

    // UI Views Initialization
    public void ViewsInitialization()
    {
        backBtn = findViewById(R.id.backBtn);
        gpsBtn = findViewById(R.id.gpsBtn);
        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        countryEt = findViewById(R.id.countryEt);
        stateEt = findViewById(R.id.stateEt);
        cityEt = findViewById(R.id.cityEt);
        addressEt = findViewById(R.id.addressEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        cPasswordEt = findViewById(R.id.cPasswordEt);
        registerBtn = findViewById(R.id.recoverBtn);
       registerSellerTv = findViewById(R.id.registerSellerTv);

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Detect Current Location
                if(checkLocationPermission()){
                    // Already Allowed
                    detectLocation();
                }
                else {
                    // Not Allowed, Request
                    requestLocationPermission();
                }
            }
        });
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pick Image
                showImagePickDialog();
            }
        });
//        registerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Register User/Customer/Buyer
//            }
//        });
        registerSellerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUserActivity.this, RegisterSellerActivity.class));
            }
        });
    }

    private void showImagePickDialog() {
        // Options To Display In Dialog
        String[] options = {"Camera", "Gallery"};
        //Dialog Box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle Clicks
                        if(which==0)
                        {
                            // Camera Clicked
                            if(checkCameraPermission())
                            {
                                // Camera Permission allowed
                                pickFromCamera();
                            }
                            else
                            {
                                //Not allowed, request
                                requestCameraPermission();
                            }
                        }
                        else
                        {
                            // Gallery Clicked
                            if(checkStoragePermission())
                            {
                                // Storage Permission allowed
                                pickFromGallery();
                            }
                            else
                            {
                                // Not allowed, request
                                requestStoragePermission();
                            }
                        }
                    }
                }).show();
    }

    public boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result1 && result2;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void detectLocation() {
        Toast.makeText(this, "Please Wait...", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, this);
    }
    private void findAddress() {
        // Find Address, Country, State and City
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Get Addresses
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String country = addresses.get(0).getCountryName(); // Get Country Name
            String state = addresses.get(0).getAdminArea(); // Get State Name
            String city = addresses.get(0).getLocality(); // Get City Name
            String address = addresses.get(0).getAddressLine(0); // Get Complete Address
            // Set Addresses
            countryEt.setText(country);
            stateEt.setText(state);
            cityEt.setText(city);
            addressEt.setText(address);
        }
        catch (Exception exception){
            Toast.makeText(this, ""+ exception.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }
    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        // Permission Allowed
                        detectLocation();
                    }
                    else{
                        // Permission Denied
                        Toast.makeText(this, "Location Permission is Necessary !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        // Permission Allowed
                        pickFromCamera();
                    }
                    else{
                        // Permission Denied
                        Toast.makeText(this, "Camera Permissions are Necessary !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        // Permission Allowed
                        pickFromGallery();
                    }
                    else{
                        // Permission Denied
                        Toast.makeText(this, "Storage Permission is Necessary !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Location Detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        findAddress();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // GPS/Location Disabled
        Toast.makeText(this, "Please Turn On Location !!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK)
        {
            if (requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                // Get Picked Image
                image_uri = data.getData();
                // Set to ImageView
                profileIv.setImageURI(image_uri);
            }
            else  if (requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                // Set to ImageView
                profileIv.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}