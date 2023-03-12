package com.example.holiyay;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextView nameHolder;
    private Button checkinBtn;
    private Button foodBtn;
    private TextView msgText;
    private TextView msgFormat;
    DatabaseReference mDatabase;
    private String scaned_data;
    boolean user_exist = false;
    String item1 = "Thandai";
    String item2 = "VadaPav";
    String item3 = "Tang";
    HashMap<String, String> obj;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        nameHolder = findViewById(R.id.nameHolder);
        checkinBtn = findViewById(R.id.check_in_button);
        foodBtn = findViewById(R.id.food_button);
        String temp = nameHolder.getText().toString();
        String display = temp + getIntent().getStringExtra("Name");
        nameHolder.setText(display);
        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Chintu", "OnClick");
                IntentIntegrator intentIntegrator = new IntentIntegrator(UserActivity.this);
                intentIntegrator.setRequestCode(1);
                intentIntegrator.setPrompt("Scan a barcode or QR Code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();
            }
        });
        foodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(UserActivity.this);
                intentIntegrator.setRequestCode(2);
                intentIntegrator.setPrompt("Scan a barcode or QR Code");
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.initiateScan();
            }
        });

        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Chintu", String.valueOf(requestCode) + String.valueOf(resultCode));

        if(requestCode == 1) {
            Log.d("Chintu", "request code 1");
            if(resultCode == RESULT_OK) {
                String barcode = data.getStringExtra(Intents.Scan.RESULT);
                Log.d("Chintu", barcode);
                if(barcode.isEmpty()) {
                    Log.d("Chintu", "content is null");
                    Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Handler h1 = new Handler();
                    h1.post(new Runnable() {
                        @Override
                        public void run() {
                            update_checkin_data(barcode, "CheckedIn");
                        }
                    });
                }
//                IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//                // if the intentResult is null then
//                // toast a message as "cancelled"
//                if (intentResult != null) {
//                    if (intentResult.getContents() == null) {
//                        Log.d("Chintu", "content results null");
//                        Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // if the intentResult is not null we'll set
//                        // the content and format of scan message
//                        Log.d("Chintu", "call update checkin");
//                        scaned_data = intentResult.getContents();
//                        Handler h1 = new Handler();
//                        h1.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                update_checkin_data(scaned_data, "CheckedIn");
//                            }
//                        });
//                    }
//                } else {
//                    Log.d("Chintu", "Result is null");
//                    super.onActivityResult(requestCode, resultCode, data);
//                }
            }
        }
        else if(requestCode == 2) {
            if(resultCode == RESULT_OK) {
                Log.d("Chintu", "result ok");
//                IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                String barcode = data.getStringExtra(Intents.Scan.RESULT);
                Log.d("Chintu", barcode);
                if(barcode.isEmpty()) {
                    Log.d("Chintu", "content is null");
                        Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Handler h1 = new Handler();
                        h1.post(new Runnable() {
                            @Override
                            public void run() {
                                update_checkin_data(barcode, "Food");
                            }
                        });
                }
            }
        }
    }

    public void update_checkin_data(String data, String type) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String temp = "Yes";
        Log.d("Durgachec", data + mDatabase.child("Users").child(data));
        mDatabase.child("Users").child(data).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()) {
                    Log.d("Durga ", "Error getting data");
                    Toast.makeText(UserActivity.this, "Error getting data", Toast.LENGTH_SHORT).show();
                    Log.d("Durga ", "Error getting data");
                } else {
                    Log.d("Durga cg", "success");
                    if(task.getResult().getValue() == null) {
                        Log.d("Durga cg", "result was null");
                        Toast.makeText(UserActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                        user_exist = false;
                        return;
                    } else {
                        user_exist = true;
//                        obj = (HashMap<String, String>) task.getResult().getValue();
//                        for (String key: obj.keySet()) {
//                            String value = obj.get(key);
//                            Log.d("Durga chintu ", key + " " + value);
//                        }
                    }
                }
            }
        });
        if(user_exist) {
            mDatabase.child("Users").child(data).child(type).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(!task.isSuccessful()) {
                        Log.d("Durga ", "Error getting data");
                        Toast.makeText(UserActivity.this, "Error getting data", Toast.LENGTH_SHORT).show();
                        Log.d("Durga ", "Error getting data");
                    } else {
                        Log.d("Durga cg", "success");
//                        Log.d("Chintu", (String) task.getResult().getValue());
//                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        Log.d("Chintu", type);
                        if(!type.equals("Food")) {
                            if (task.getResult().getValue().equals("Yes")) {
                                Log.d("Durga cg", "result was null");
                                Toast.makeText(UserActivity.this, "User already checked in", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                mDatabase.child("Users").child(data).child(type).setValue("Yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(UserActivity.this, "Successfully CheckedIn", Toast.LENGTH_LONG).show();
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserActivity.this, "Failed to check in please scan Again", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            showAlertDialog(data, (HashMap<String, String>) task.getResult().getValue(), type);
                            Toast.makeText(UserActivity.this, "Successfully retrieved", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void showAlertDialog(String data, HashMap<String, String> kit, String type) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserActivity.this);
        alertDialog.setTitle("AlertDialog");
//        String[] items = kit.keySet().toArray(new String[0]);
        String[] items = {item1, item2, item3};
        boolean[] checkedItems = {false, false, false, false};
        HashMap<String, String> hm = (HashMap<String, String>) kit;
        for (Map.Entry i : hm.entrySet()) {
            if(i.getKey().equals(item1) && i.getValue().equals("Yes")) {
                checkedItems[0] = true;
            } else if(i.getKey().equals(item2) && i.getValue().equals("Yes")) {
                checkedItems[1] = true;
            } else if(i.getKey().equals(item3) && i.getValue().equals("Yes")) {
                checkedItems[2] = true;
            } else if(i.getKey().equals(item1) && i.getValue().equals("No")) {
                checkedItems[0] = false;
            } else if(i.getKey().equals(item2) && i.getValue().equals("No")) {
                checkedItems[1] = false;
            } else if(i.getKey().equals(item3) && i.getValue().equals("No")) {
                checkedItems[2] = false;
            }
        }

        Map<String, String> obj = new HashMap<>(kit);
//        obj.put("item1", "No");
//        obj.put("item2", "No");
//        obj.put("item3", "No");
        alertDialog.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                switch (which) {
                    case 0:
                        if(isChecked) {
                            obj.put(item1, "Yes");
//                            Toast.makeText(UserActivity.this, "Clicked on java", Toast.LENGTH_LONG).show();
                        } else {
                            obj.put(item1, "No");
                        }
                        break;
                    case 1:
                        if(isChecked) {
                            obj.put(item2, "Yes");
//                            Toast.makeText(UserActivity.this, "Clicked on android", Toast.LENGTH_LONG).show();
                        } else {
                            obj.put(item2, "No");
                        }
                        break;
                    case 2:
                        if(isChecked) {
                            obj.put(item3, "Yes");
//                            Toast.makeText(UserActivity.this, "Clicked on Data Structures", Toast.LENGTH_LONG).show();
                        } else {
                            obj.put(item3, "No");
                        }
                        break;
                }
            }
        });
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Log.d("Rotafest", "Yes is clicked");
                        mDatabase.child("Users").child(data).child(type).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UserActivity.this, "Successfully Updated the DB", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserActivity.this, "Error Updating...", Toast.LENGTH_LONG).show();
                            }
                        });
//                        Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

//    public void update_data(String data) {
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        String temp = "Yes";
//        Log.d("Durgachec", data + mDatabase.child("Users").child(data));
//        mDatabase.child("Users").child(data).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(!task.isSuccessful()) {
//                    Log.d("Durga ", "Error getting data");
//                    Toast.makeText(UserActivity.this, "Error getting data", Toast.LENGTH_SHORT).show();
//                    Log.d("Durga ", "Error getting data");
//                } else {
//                    Log.d("Durga cg", "success");
//                    if(task.getResult().getValue() == null) {
//                        Log.d("Durga cg", "result was null");
//                        Toast.makeText(UserActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
//                        user_exist = false;
//                        return;
//                    } else {
//                        user_exist = true;
//                        obj = (HashMap<String, String>) task.getResult().getValue();
//                        for (String key: obj.keySet()) {
//                            String value = obj.get(key);
//                            Log.d("Durga chintu ", key + " " + value);
//                        }
//                    }
//                }
//            }
//        });
//        if(user_exist) {
////            setValues(data, obj);
//            /*
//            mDatabase.child("Users").child(data).child("CheckedIn").setValue(temp).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Toast.makeText(UserActivity.this, "Successfully Checked In", Toast.LENGTH_LONG).show();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(UserActivity.this, "Error Updating...", Toast.LENGTH_LONG).show();
//                }
//            });
//            mDatabase.child("Users").child(data).child("Food").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if (!task.isSuccessful()) {
//                        Log.e("firebase", "Error getting data", task.getException());
//                        Toast.makeText(UserActivity.this, "Error getting data", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
////                        Map<String, String> obj = new HashMap<>();
////                        obj.put("item1", "Yes1");
////                        obj.put("item2", "Yes1");
////                        obj.put("item3", "Yes1");
//                        mDatabase.child("Users").child(data).child("Kit").setValue(task.getResult().getValue());
//                        showAlertDialog(data, (HashMap<String, String>) task.getResult().getValue());
//                        Toast.makeText(UserActivity.this, "Successfully retrieved", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//             */
//        }
//    }


    private void setValues(String data, HashMap<String, String> temp) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserActivity.this);
        alertDialog.setTitle("AlertDialog");

        String[] items = {item1, item2};
        boolean[] checkedItems = {temp.get(item1) == "Yes", temp.get(item2) == "Yes"};
//        HashMap<String, String> obj = new HashMap<>();
        alertDialog.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                switch (which) {
                    case 0:
                        if (isChecked) {
                            obj.put(item1, "Yes");
//                            Toast.makeText(UserActivity.this, "Clicked on java", Toast.LENGTH_LONG).show();
                        } else {
                            obj.put(item1, "No");
                        }
                        break;
                    case 1:
                        if (isChecked) {
                            obj.put(item2, "Yes");
//                            Toast.makeText(UserActivity.this, "Clicked on android", Toast.LENGTH_LONG).show();
                        } else {
                            obj.put(item2, "No");
                        }
                        break;
                }
            }
        });
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Log.d("Rotafest", "Yes is clicked");
                        mDatabase.child("Users").child(data).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UserActivity.this, "Successfully Updated the DB", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserActivity.this, "Error Updating...", Toast.LENGTH_LONG).show();
                            }
                        });
//                        Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();


    }

//    private void showAlertDialog(String data, HashMap<String, String> kit) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserActivity.this);
//        alertDialog.setTitle("AlertDialog");
////        String[] items = kit.keySet().toArray(new String[0]);
//        String[] items = {item1, item2, item3, item4};
//        boolean[] checkedItems = {false, false, false, false};
//        HashMap<String, String> hm = (HashMap<String, String>) kit;
//        for (Map.Entry i : hm.entrySet()) {
//            if(i.getKey().equals(item1) && i.getValue().equals("Yes")) {
//                checkedItems[0] = true;
//            } else if(i.getKey().equals(item2) && i.getValue().equals("Yes")) {
//                checkedItems[1] = true;
//            } else if(i.getKey().equals(item3) && i.getValue().equals("Yes")) {
//                checkedItems[2] = true;
//            } else if(i.getKey().equals(item4) && i.getValue().equals("Yes")) {
//                checkedItems[3] = true;
//            } else if(i.getKey().equals(item1) && i.getValue().equals("No")) {
//                checkedItems[0] = false;
//            } else if(i.getKey().equals(item2) && i.getValue().equals("No")) {
//                checkedItems[1] = false;
//            } else if(i.getKey().equals(item3) && i.getValue().equals("No")) {
//                checkedItems[2] = false;
//            } else if(i.getKey().equals(item4) && i.getValue().equals("No")) {
//                checkedItems[3] = false;
//            }
//        }
//
//        Map<String, String> obj = new HashMap<>(kit);
////        obj.put("item1", "No");
////        obj.put("item2", "No");
////        obj.put("item3", "No");
//        alertDialog.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                switch (which) {
//                    case 0:
//                        if(isChecked) {
//                            obj.put(item1, "Yes");
////                            Toast.makeText(UserActivity.this, "Clicked on java", Toast.LENGTH_LONG).show();
//                        } else {
//                            obj.put(item1, "No");
//                        }
//                        break;
//                    case 1:
//                        if(isChecked) {
//                            obj.put(item2, "Yes");
////                            Toast.makeText(UserActivity.this, "Clicked on android", Toast.LENGTH_LONG).show();
//                        } else {
//                            obj.put(item2, "No");
//                        }
//                        break;
//                    case 2:
//                        if(isChecked) {
//                            obj.put(item3, "Yes");
////                            Toast.makeText(UserActivity.this, "Clicked on Data Structures", Toast.LENGTH_LONG).show();
//                        } else {
//                            obj.put(item3, "No");
//                        }
//                        break;
//                    case 3:
//                        if(isChecked) {
//                            obj.put(item4, "Yes");
//                        } else {
//                            obj.put(item4, "No");
//                        }
//                }
//            }
//        });
//        alertDialog.setPositiveButton("OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,
//                                        int which) {
//                        Log.d("Rotafest", "Yes is clicked");
//                        mDatabase.child("Users").child(data).child("Kit").setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(UserActivity.this, "Successfully Updated the DB", Toast.LENGTH_LONG).show();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(UserActivity.this, "Error Updating...", Toast.LENGTH_LONG).show();
//                            }
//                        });
////                        Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
//                    }
//                });
//        AlertDialog alert = alertDialog.create();
//        alert.setCanceledOnTouchOutside(false);
//        alert.show();
//    }

    public void onLogOutClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("Rotafest", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        finish();
    }

    public void onAgendaClick(View view) {
        alertDialog(Util.Agenda, "Agenda");
    }

    public void onPOCClick(View view) {
        alertDialog(Util.contact, "Point of contact");
    }


    private void alertDialog(String heading, String data) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage(heading);
        dialog.setTitle(data);
        ScrollView sv =new ScrollView(getBaseContext());

        final TextView text = new TextView(UserActivity.this);
        LinearLayout linear =new LinearLayout(getBaseContext());
        sv.addView(linear);
        linear.addView(text);

        dialog.setView(sv);

        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Log.d("Rotafest", "Yes is clicked");
//                        Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    public void onEventDetails(View view) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Events").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    Toast.makeText(UserActivity.this, "Error Getting the Data", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Toast.makeText(UserActivity.this, "Successfully retrieved the data", Toast.LENGTH_LONG).show();
                    HashMap<String, HashMap> hm = (HashMap<String, HashMap>) task.getResult().getValue();
                    String final_str = " ";
                    for (Map.Entry i : hm.entrySet()) {
                        HashMap<String, String> j = (HashMap<String, String>) i.getValue();
                        String event = "";
                        String price = "";
                        for(Map.Entry k : j.entrySet()) {
                            if(k.getKey().equals("Event Name")) {
                                event  = String.valueOf(k.getValue()).split(":")[1];
                            }
                            if(k.getKey().equals("Price")) {
                                price = String.valueOf(k.getValue());
                            }
                        }
                        final_str = final_str + event + " \n " + price + "\n\n";
                    }
                    alertDialog(final_str, "Event Data");
                }
            }
        });
    }

    //Users and Permission

    //Admin - grant/revoke access and statistics
    //Organizer - rest all the features
    //Manager - Only the statistics


    // Agenda
    // Contacts
    // Statistics of
    // No of kits and registration
    //

    // Name
    // Phone Number
    // Mail ID
    // How many events
    //
}
