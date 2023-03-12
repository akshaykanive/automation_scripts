package com.example.holiyay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private Button submit;
    DatabaseReference mDatabase;
    private ArrayList<String> admin;
    private ArrayList<String> manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        admin = new ArrayList<String>(10);
        admin.add("durgaprasadsn123@gmail.com");
//        admin.add("durgachintu123@gmail.com");

        manager = new ArrayList<String>(30);
        manager.add("chintusadali@gmail.com");

        auth = FirebaseAuth.getInstance();
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("Rotafest", MODE_PRIVATE);
            String usernameSP = sharedPreferences.getString("username", null);
            String passwordSP = sharedPreferences.getString("password", null);
            if(usernameSP != null && passwordSP != null) {
                signInUser(usernameSP, passwordSP);
            } else {
                setContentView(R.layout.activity_login);
                email = findViewById(R.id.editTextEmail);
                password = findViewById(R.id.editTextPassword);
                submit = findViewById(R.id.cirLoginButton);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onLoginButtonClick(view);
                    }
                });
            }

            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child("Uid").get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLoginButtonClick(View view) {
        try {
            String Email = email.getText().toString();
            String Password = password.getText().toString();
            if(password != null && Email != null) {
                signInUser(Email, Password);
            } else {
                Toast.makeText(getApplicationContext(), "Please Enter Required Details", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLoginClick(View View){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void signInUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Rotafest", "signInWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d("Rotafest", user.getEmail() + " " + user.getDisplayName());
                        SharedPreferences sharedPreferences = getSharedPreferences("Rotafest", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", email);
                        editor.putString("password", password);
                        editor.apply();

                        if(admin.contains(email)) {
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("Name", user.getDisplayName());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                            finish();
                        } else if(manager.contains(email)) {
                            Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("Name", user.getDisplayName());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("Name", user.getDisplayName());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                            finish();
                        }
                        //                            updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Rotafest", "signInWithEmail:failure", task.getException());
                        try {
                            throw task.getException();
                        }catch (FirebaseAuthException e) {
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Contact Administrator", Toast.LENGTH_SHORT).show();
                        }
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                    }
                });
    }


    public void onForgotPassword(View view) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(String.valueOf(email))
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Rotafest", "Email sent.");
                    }
                }
            });
    }
}