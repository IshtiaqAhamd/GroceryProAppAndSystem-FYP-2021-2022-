package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.Constants;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;

public class AddProductActivity extends AppCompatActivity {
    // Declaring Add Product Activity UI Views
    ImageButton backBtn;
    ImageView productIconIv;
    EditText titleEt, descriptionEt, quantityEt, priceEt, discountPriceEt, discountNoteEt;
    TextView categoryTv;
    SwitchCompat discountSwitch;
    Button addProductBtn;

    // Permission Constant
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    // Image Pick Constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // Image Picked URI
    private Uri image_uri;

    // Permission Arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    // FirebaseAuth
    FirebaseAuth firebaseAuth;

    // Progress Dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ViewsInitialization();
        ViewsPerformanceActions();
    }
    // UI Views Initialization
    public void ViewsInitialization() {
        // Initialization Of Views
        backBtn = findViewById(R.id.backBtn);
        productIconIv = findViewById(R.id.productIconIv);
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        quantityEt = findViewById(R.id.quantityEt);
        priceEt = findViewById(R.id.priceEt);
        discountPriceEt = findViewById(R.id.discountPriceEt);
        discountNoteEt = findViewById(R.id.discountNoteEt);
        categoryTv = findViewById(R.id.categoryTv);
        discountSwitch = findViewById(R.id.discountSwitch);
        addProductBtn = findViewById(R.id.addProductBtn);

        // UnChecked, Hide Product Discount Price, Product Discount Note
        discountPriceEt.setVisibility(View.GONE);
        discountNoteEt.setVisibility(View.GONE);

        // Initialization Of Permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // Initialization Of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialization Of Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    // UI Views Performance Actions
    public void ViewsPerformanceActions() {
        productIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Dialog To Pick Image
                showImagePickDialog();
            }
        });
        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pick Category
                categoryDialog();
            }
        });
        // If DiscountSwitch is checked: Show DiscountPrice, DiscountNote | If DiscountSwitch is not checked: Hide DiscountPrice, DiscountNote
        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    // Checked, Show Product Discount Price, Product Discount Note
                    discountPriceEt.setVisibility(View.VISIBLE);
                   discountNoteEt.setVisibility(View.VISIBLE);
                }
                else
                {
                    // UnChecked, Hide Product Discount Price, Product Discount Note
                    discountPriceEt.setVisibility(View.GONE);
                    discountNoteEt.setVisibility(View.GONE);
                }
            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*  Flow:
                    1) Input Data
                    2) Validate Data
                    3) Add Data to Database (Firebase)
                */
                inputData();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Goto Previous Activity
                onBackPressed();
            }
        });
    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Get Picked Category
                        String Categories = Constants.productCategories[which];

                        //Set Picked Category
                        categoryTv.setText(Categories);
                    }
                })
                .show();
    }

    String productTitle, productDescription, productCategory, productQuantity, originalPrice, discountPrice, discountNote;
    boolean discountAvailable = false;
    private void inputData() {
        // 1) Input Data
        productTitle = titleEt.getText().toString().trim();
        productDescription = descriptionEt.getText().toString().trim();
        productCategory = categoryTv.getText().toString().trim();
        productQuantity = quantityEt.getText().toString().trim();
        originalPrice = priceEt.getText().toString().trim();
        discountAvailable = discountSwitch.isChecked(); // true/false

        discountPrice = discountPriceEt.getText().toString().trim();
        discountNote = discountNoteEt.getText().toString().trim();

        // 2) Validate Data
        if(TextUtils.isEmpty(productTitle))
        {
            Toast.makeText(this, "Title is Required!", Toast.LENGTH_SHORT).show();
            return; // Don't Proceed Further
        }
        if(TextUtils.isEmpty(productCategory))
        {
            Toast.makeText(this, "Category is Required!", Toast.LENGTH_SHORT).show();
            return; // Don't Proceed Further
        }
        if(TextUtils.isEmpty(originalPrice))
        {
            Toast.makeText(this, "Price is Required!", Toast.LENGTH_SHORT).show();
            return; // Don't Proceed Further
        }
        if(discountAvailable)
        //Product With Discount
        {
            discountPrice = discountPriceEt.getText().toString().trim();
            discountNote = discountNoteEt.getText().toString().trim();
            if(TextUtils.isEmpty(discountPrice))
            {
                Toast.makeText(this, "Discount Price is Required!", Toast.LENGTH_SHORT).show();
                return; // Don't Proceed Further
            }
            if(TextUtils.isEmpty(discountNote))
            {
                Toast.makeText(this, "Discount Note is Required!", Toast.LENGTH_SHORT).show();
                return; // Don't Proceed Further
            }
        }
        else
        {
            //Product Without Discount
            discountPrice = "0";
            discountNote = "";
        }
        addProduct();
    }
    private void addProduct() {
        // 3) Add Data to DB
        progressDialog.setMessage("Adding Product....");
        progressDialog.show();
        String timestamp = ""+System.currentTimeMillis();

        if (image_uri==null)
        {
            //Upload Without Image
            //Setup Data To Upload
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("productID",""+timestamp);
            hashMap.put("productTitle",""+productTitle);
            hashMap.put("productDescription", ""+productDescription);
            hashMap.put("productCategory", ""+productCategory);
            hashMap.put("productQuantity", ""+productQuantity);
            hashMap.put("productIcon", ""); // No Image, Set Empty
            hashMap.put("originalPrice", ""+originalPrice);
            hashMap.put("discountPrice", ""+discountPrice);
            hashMap.put("discountNote", ""+discountNote);
            hashMap.put("discountAvailable", ""+discountAvailable);
            hashMap.put("timestamp", ""+timestamp);
            hashMap.put("uid", ""+firebaseAuth.getUid());

            //Add to Database (Firebase)
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid())
                    .child("Products").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //Added to  DB
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
                            clearData();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed adding to DB
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
        {
            // Upload With Image

            // First Upload Image To Storage

            // Name and Path of Image to be Upload
            String filePathAndName = "product_images/" + "" + timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image Uploaded
                            // Get URL of Uploaded Image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful())
                            {
                                // Url of image received, Upload to DB
                                //Setup Data To Upload
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("productID",""+timestamp);
                                hashMap.put("productTitle",""+productTitle);
                                hashMap.put("productDescription", ""+productDescription);
                                hashMap.put("productCategory", ""+productCategory);
                                hashMap.put("productQuantity", ""+productQuantity);
                                hashMap.put("productIcon", "" + downloadImageUri);
                                hashMap.put("originalPrice", ""+originalPrice);
                                hashMap.put("discountPrice", ""+discountPrice);
                                hashMap.put("discountNote", ""+discountNote);
                                hashMap.put("discountAvailable", ""+discountAvailable);
                                hashMap.put("timestamp", ""+timestamp);
                                hashMap.put("uid", ""+firebaseAuth.getUid());

                                //Add to DB
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid())
                                        .child("Products").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //Added to  DB
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed adding to DB
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed Uploading Image
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearData() {
        titleEt.setText("");
        descriptionEt.setText("");
        categoryTv.setText("");
        quantityEt.setText("");
        priceEt.setText("");
        discountPriceEt.setText("");
        discountNoteEt.setText("");
        productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
        image_uri = null;
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
                        // Handle Items Clicks
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
    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result; // Returns true/false
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

    private void pickFromGallery(){
        // Intent To Pick Image From Gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }
    private void pickFromCamera() {

        // Intent To Pick Image From Camera
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

    //Handle Image Pick Results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK)
        {
            if (requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                // Get Picked Image
                image_uri = data.getData();
                // Set to ImageView
                productIconIv.setImageURI(image_uri);
            }
            else  if (requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                // Set to ImageView
                productIconIv.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}