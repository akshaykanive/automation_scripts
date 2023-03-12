package com.example.holiyay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText Name;
    private EditText Email;
    private EditText number;
    private EditText password;
    private Button register;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        Name = findViewById(R.id.editTextName);
        Email = findViewById(R.id.editTextEmail);
        number = findViewById(R.id.editTextMobile);
        password = findViewById(R.id.editTextPassword);
        register = findViewById(R.id.cirRegisterButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginButtonClick(view);
            }
        });
    }

    public void onLoginButtonClick(View view) {
        try {
            String email = Email.getText().toString();
            String pass = password.getText().toString();
            String name = Name.getText().toString();
            String num = number.getText().toString();
            registerUser(email, pass, name);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLoginClick(View view){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void registerUser(String email, String password, String name) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Rotafest", "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Log.d("Rotafest", user.getDisplayName() + " " + user.getEmail());
                            Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_SHORT).show();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Rotafest", "User profile updated.");
                                                Log.d("Rotafest", user.getDisplayName());
                                            }
                                        }
                                    });
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
                            finish();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Rotafest", "createUserWithEmail:failure", task.getException());
                            Log.d("Rotafest", String.valueOf(task.getException().getClass().equals("class com.google.firebase.auth.FirebaseAuthUserCollisionException")) + task.getException().getClass());
                            if(task.getException().getClass().equals("class com.google.firebase.auth.FirebaseAuthUserCollisionException")) {
                                Toast.makeText(getApplicationContext(), "Email ID is already registered", Toast.LENGTH_SHORT).show();
                            }
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(getApplicationContext(), "Password is Very weak Change the password", Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Toast.makeText(getApplicationContext(), "Email ID is already registered", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Please Enter Valid Credentials", Toast.LENGTH_SHORT).show();
                            }
//                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }
}
