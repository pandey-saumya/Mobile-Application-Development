package com.example.saumya.sharegram;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saumya.sharegram.Model.User;
import com.example.saumya.sharegram.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    private Context thisActivity;

    private TextView signintext;
    private EditText email, username, password;
    private String emailText, usernameText, passwordText;
    private Button bSignup;
    private ProgressDialog progressDialog;
    private AppCompatCheckBox revealPass;

    private static final String TAG = "SignUp";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods fMethods;

    private FirebaseDatabase fbaseDB;
    private DatabaseReference dbRef;

    private String append = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ConstraintLayout cl = findViewById(R.id.relativeLayout);
        AnimationDrawable draw = (AnimationDrawable) cl.getBackground();
        draw.setEnterFadeDuration(2000);
        draw.setExitFadeDuration(2000);
        draw.start();

        thisActivity = SignUp.this;
       // System.out.print("Hello");
//        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        username = (EditText)findViewById(R.id.Etusername);
        email = (EditText)findViewById(R.id.Etemailid);
        password=(EditText)findViewById(R.id.Etpassword);
        bSignup =(Button)findViewById(R.id.btsignup);
        signintext = (TextView)findViewById(R.id.singintv);
        revealPass = findViewById(R.id.cbRevealpass);

        setupFirebaseAuth();
        init();

//        signintext.setOnClickListener(this);
//        bSignup.setOnClickListener(this);
    }

    private void init(){
        bSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailText = email.getText().toString();
                usernameText = username.getText().toString();
                passwordText = password.getText().toString();

                if(emailText.equals("") || usernameText.equals("") || passwordText.equals("")){
                    Toast.makeText(thisActivity,"Please fill in your details", Toast.LENGTH_SHORT).show();
                } else{
                    fMethods = new FirebaseMethods(thisActivity);
                    fMethods.registerNewEmail(emailText, passwordText, usernameText);

                }

            }
        });
        revealPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }


//    public void onClick(View v) {
//        if (v == signintext) {
//            Intent intent = og Intent(SignUp.this, LogIn.class);
//            startActivity(intent);
//            finish();
//        }
//        if(v == bSignup){
//            userRegister();
//        }
//    }



    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if  " + username + " already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        append = dbRef.push().getKey().substring(5,8);
                        Log.d(TAG, "onDataChange: username already exists. Appending random string to name: " + append);
                    }
                }

                String tempUsername = "";
                tempUsername = username + append;

                //add og user to the database
                fMethods.addNewUser(emailText, tempUsername, "", "", "");

//                Toast.makeText(thisActivity, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();
                Toast.makeText(thisActivity, "Account created, please login.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //firebase thing starts here

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth");
        mAuth = FirebaseAuth.getInstance();
        fbaseDB = FirebaseDatabase.getInstance();
        dbRef = fbaseDB.getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    //somebody signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        String unique = "";
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkIfUsernameExists(usernameText);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    finish();
                } else{
                    //nobodys here
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    //firebase thing end here

    /*private void userRegister() {
        String Email = email.getText().toString().trim();
        String Username = username.getText().toString().trim();
        String Password = password.getText().toString().trim();

        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(this, "Please Enter Email ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Username)) {
            Toast.makeText(this, "Please Enter  Username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User....");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, og OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
//                            startActivity(og Intent(getApplicationContext(),LogIn.class));
//                            finish();
                        } else {
                            return;
                        }
                    }
                });

        User user = og User(mAuth.getCurrentUser().getUid(), 1, Email, Username);
        dbRef.child("users")
                .child(mAuth.getCurrentUser().getUid())
                .setValue(user);

        UserAccountSetting settings = og UserAccountSetting(
                "",
                "",
                0,
                0,
                0,
                "",
                Username);
        dbRef.child("user_account_setting").child(mAuth.getCurrentUser().getUid()).setValue(settings);



    }*/

}
