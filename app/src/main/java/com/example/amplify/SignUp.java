    package com.example.amplify;
    
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    
    import android.content.Intent;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;
    
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    
    public class SignUp extends AppCompatActivity {
    
        EditText username, email, password, cpassword;
        Button SignUpBtn;
        FirebaseDatabase db;
        private DatabaseReference mDatabase;
        private FirebaseAuth mAuth;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign_up);
    
            mAuth = FirebaseAuth.getInstance();
            username = findViewById(R.id.username);
            email = findViewById(R.id.emailSignUp);
            password = findViewById(R.id.passwordSignUp);
            cpassword = findViewById(R.id.cpasswordSignUp);
            SignUpBtn = findViewById(R.id.SignUpBtn);
    
    
            SignUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
    
                    String usernamedb = username.getText().toString().trim();
                    String emaildb = email.getText().toString().trim();
                    String passdb = password.getText().toString().trim();
                    String cpassdb = cpassword.getText().toString().trim();
    
                    if (TextUtils.isEmpty(usernamedb)) {
                        Toast.makeText(SignUp.this, "Enter the Name", Toast.LENGTH_SHORT).show();
                        return;
                    }
    
                    if (TextUtils.isEmpty(emaildb)) {
                        Toast.makeText(SignUp.this, "Enter the email", Toast.LENGTH_SHORT).show();
                        return;
                    }
    
                    if (TextUtils.isEmpty(passdb)) {
                        Toast.makeText(SignUp.this, "Enter the password", Toast.LENGTH_SHORT).show();
                        return;
                    }
    
                    if (!passdb.equals(cpassdb)) {
                        Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }
    
                    mAuth.createUserWithEmailAndPassword(emaildb, passdb)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Users users = new Users(usernamedb,emaildb);
                                        db = FirebaseDatabase.getInstance();
                                        mDatabase = db.getReference("Users");
                                        mDatabase.child(user.getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    username.setText("");
                                                    email.setText("");
                                                    password.setText("");
                                                    cpassword.setText("");
                                                    Toast.makeText(SignUp.this,"Account created successfully",Toast.LENGTH_SHORT).show();
                                                    updateUI(user);
                                                } else {
                                                    Toast.makeText(SignUp.this,"Failed to create account",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(SignUp.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }
            });

            TextView toLogin = findViewById(R.id.toLogin);
            Intent toLoginIntent = new Intent(SignUp.this, Login.class);
            toLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toLoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(toLoginIntent);
                    finish();
                }
            });

        }
    
        private void updateUI(FirebaseUser user) {
            if (user != null) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    
    
    }
