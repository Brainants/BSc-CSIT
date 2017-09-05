package com.brainants.bsccsit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.brainants.bsccsit.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 1001;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    GoogleApiClient client;
    KenBurnsView mKenBurns;
    CardView googleLoginButton, fbLoginButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String token = FirebaseInstanceId.getInstance().getToken();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("uid", user.getUid());
                    map.put("name", user.getDisplayName());
                    map.put("email", user.getEmail());
                    map.put("instance_id", token);
                    try {
                        map.put("image_url", user.getPhotoUrl().toString());
                    } catch (Exception ignored) {
                    }

                    //adding user info to the database
                    FirebaseDatabase.getInstance().getReference()
                            .child("user_data")
                            .child(user.getUid())
                            .setValue(map);

                    //go to the completion form
                    startActivity(new Intent(LoginActivity.this, CompleteLogin.class));
                    finish();
                }
            }
        };

        mKenBurns = (KenBurnsView) findViewById(R.id.kenBurns);

        handleViews();

        handleFbLogin();

        handleGoogleLogin();
    }

    private void handleGoogleLogin() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();

        client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)

                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(client);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void handleViews() {
        fbLoginButton = (CardView) findViewById(R.id.fbLoginButton);
        googleLoginButton = (CardView) findViewById(R.id.googleLoginButton);
    }

    private void handleFbLogin() {
        loginButton = new LoginButton(this);

        loginButton.setReadPermissions("public_profile", "email", "user_hometown");

        callbackManager = CallbackManager.Factory.create();

        //login fbLoginButton ko kaam
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                new EventSender().logEvent("fb_signedup");
                attachCredToFirebase(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()));
            }

            @Override
            public void onCancel() {
                Snackbar.make(findViewById(R.id.LoginCore), "Login process aborted.", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.LoginCore), "Unable to connect.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginButton.callOnClick();
                    }
                }).show();
            }
        });
        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.callOnClick();
            }
        });
    }

    private void attachCredToFirebase(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        new EventSender().logEvent("user_signup");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        new EventSender().logEvent("attach_error");
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(LoginActivity.this, "Email address in use by another account. Try another option.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Unable to login.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                new EventSender().logEvent("google_logged_in");
                attachCredToFirebase(GoogleAuthProvider.getCredential(account.getIdToken(), null));
            } else {
                Toast.makeText(this, "Unable to connect. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
