package com.example.newproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import android.hardware.biometrics.BiometricPrompt;
import android.os.CancellationSignal;
import android.widget.ImageButton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginButton, login_google_button,btn_back;
    TextView signupRedirectText, backText;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;

    ImageButton fingerprintButton;
    BiometricPrompt biometricPrompt;
    CancellationSignal cancellationSignal;
    Executor executor;

    int RC_SIGN_IN = 20;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_userEmail);
        loginPassword = findViewById(R.id.login_password);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        backText = findViewById(R.id.backText);
        loginButton = findViewById(R.id.login_button);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        fingerprintButton = findViewById(R.id.fingerprint_button);
        executor = Executors.newSingleThreadExecutor();

        biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Use fingerprint to log in")
                .setNegativeButton("Cancel", executor, (dialogInterface, i) -> {
                    Toast.makeText(getApplicationContext(), "Authentication canceled", Toast.LENGTH_SHORT).show();
                }).build();

        fingerprintButton.setOnClickListener(v -> authenticateUser());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        String s = "Not yet registered? Sign Up";
        SpannableString ss = new SpannableString(s);

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(Color.BLUE);
        ss.setSpan(fcsBlue, 19, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signupRedirectText.setText(ss);

        String a = "Continue previewing TravelSence";
        SpannableString aa = new SpannableString(a);

        ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.GREEN);
        aa.setSpan(fcsYellow, 0, a.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        backText.setText(aa);


        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);

            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(loginEmail.getText());
                String password = String.valueOf(loginPassword.getText());

                if (TextUtils.isEmpty(email) | TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "All information must be enter!!", Toast.LENGTH_SHORT).show();

                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, homePage.class);
                startActivity(intent);
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();
                firebaseAuth(idToken);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", user.getDisplayName());
                            map.put("name", user.getDisplayName());
                            map.put("email", user.getEmail());
                            map.put("phone", user.getPhoneNumber());
                            map.put("userProfileimg", user.getPhotoUrl().toString());

                            database.getReference().child("users").child(user.getUid()).setValue(map);
                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            intent.putExtra("name", user.getDisplayName());
                            intent.putExtra("email", user.getEmail());
                            intent.putExtra("username", user.getDisplayName());
                            intent.putExtra("phone", user.getPhoneNumber());
                            intent.putExtra("userProfileimg", user.getPhotoUrl().toString());
                            startActivity(intent);
                        } else {

                            Toast.makeText(LoginActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }


}