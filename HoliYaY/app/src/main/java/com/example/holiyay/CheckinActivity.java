package com.example.holiyay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;

public class CheckinActivity extends UserActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    DatabaseReference mDatabase;
    boolean user_exist = false;
    HashMap<String, String> obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, start barcode scanner
            startBarcodeScanner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start barcode scanner
                startBarcodeScanner();
            } else {
                // Permission denied, show error message and finish activity
                Toast.makeText(this, "Camera permission is required to scan barcodes",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startBarcodeScanner() {
        // Create intent integrator to initiate barcode scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get the result from the barcode scanner
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            // Barcode was scanned successfully
            String barcodeContent = result.getContents();
//            Toast.makeText(this, "Scanned barcode: " + barcodeContent, Toast.LENGTH_LONG).show();
            Handler h1 = new Handler();
            h1.post(new Runnable() {
                @Override
                public void run() {
                    update_data(barcodeContent);
                }
            });
        } else {
            // Barcode scanning was cancelled or failed
            Log.e("BarcodeScannerActivity", "Error scanning barcode");
        }
    }

    public void update_data(String data) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String temp = "Yes";
        Log.d("Durgachec", data + mDatabase.child("Users").child(data));
        mDatabase.child("Users").child(data).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()) {
                    Log.d("Durga ", "Error getting data");
                    Toast.makeText(CheckinActivity.this, "Error getting data", Toast.LENGTH_SHORT).show();
                    Log.d("Durga ", "Error getting data");
                } else {
                    Log.d("Durga cg", "success");
                    if(task.getResult().getValue() == null) {
                        Log.d("Durga cg", "result was null");
                        Toast.makeText(CheckinActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                        user_exist = false;
                        return;
                    } else {
                        user_exist = true;
                        obj = (HashMap<String, String>) task.getResult().getValue();
                        for (String key: obj.keySet()) {
                            String value = obj.get(key);
                            Log.d("Durga chintu ", key + " " + value);
                        }
                    }
                }
            }
        });
        if(user_exist) {
//            setValues(data, obj);
            if(obj.get("CheckedIn").equals("Yes")) {
                Intent intent = new Intent(this, UserActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(this, "Already checked In", Toast.LENGTH_SHORT).show();
            } else {
                mDatabase.child("Users").child(data).child("CheckedIn").setValue(temp).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CheckinActivity.this, "Successfully Checked In", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CheckinActivity.this, "Error Updating...", Toast.LENGTH_LONG).show();
                    }
                });
            }
            /*
            mDatabase.child("Users").child(data).child("Food").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                        Toast.makeText(CheckinActivity.this, "Error getting data", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                        Map<String, String> obj = new HashMap<>();
//                        obj.put("item1", "Yes1");
//                        obj.put("item2", "Yes1");
//                        obj.put("item3", "Yes1");
                        mDatabase.child("Users").child(data).child("Kit").setValue(task.getResult().getValue());
//                        showAlertDialog(data, (HashMap<String, String>) task.getResult().getValue());
                        Toast.makeText(CheckinActivity.this, "Successfully retrieved", Toast.LENGTH_SHORT).show();
                    }
                }
            });

             */
        }
    }
}
