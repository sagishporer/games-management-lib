package com.games.management.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.games.GamesScopes;

public class BaseAuthActivity extends AppCompatActivity {
    private static final String TAG = BaseAuthActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 1001;
    private static final int RC_REQUEST_AUTHORIZATION = 1002;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mGoogleSignInAccount;

    private ProgressDialog mProgressDialogConnecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleSignInAccount = null;
        requestSignIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                handleSignInResult(resultData);
            } else
                handleSignInFailed();
        }
        if (requestCode == RC_REQUEST_AUTHORIZATION) {
            if (resultCode == Activity.RESULT_OK)
                onConnectSuccess();
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    protected GoogleSignInAccount getGoogleSignInAccount() {
        return mGoogleSignInAccount;
    }

    private void requestSignIn() {
        Log.d(TAG, "Requesting sign-in");

        mProgressDialogConnecting = ProgressDialog.show(this, "Signing-in", "Please wait...", true);

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(GamesScopes.GAMES))
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);

        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    protected void signOut() {
        mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mGoogleSignInAccount = null;
                Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleSignInFailed() {
        Log.d(TAG, "Sign in failed");
        String reason = "";
        if ((resultData != null)&&(resultData.getExtras() != null)) {
            Object obj = resultData.getExtras().get("googleSignInStatus");
            if (obj != null)
                reason = obj.toString();
        }

        Toast.makeText(this, "Sign-in failed. " + reason, Toast.LENGTH_LONG).show();
		
        mProgressDialogConnecting.dismiss();
        mProgressDialogConnecting = null;
    }

    private void handleSignInResult(Intent result) {
        mProgressDialogConnecting.dismiss();
        mProgressDialogConnecting = null;

        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        Log.d(TAG, "Signed in as " + googleSignInAccount.getEmail());
                        if (mGoogleSignInAccount != googleSignInAccount)
                            mGoogleSignInAccount = googleSignInAccount;

                        onConnectSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Unable to sign in.", e);

                        mGoogleSignInAccount = null;
                        onConnectFailed(e);
                    }
                });
    }

    protected void onConnectSuccess() {
    }

    protected void onConnectFailed(Exception e) {
        reportError("Sign-in failed", e);
    }

    private boolean tryRecoverFrom(Exception e) {
        if (e == null)
            return false;

        Intent recoverIntent = null;
        if (e instanceof UserRecoverableAuthException)
            recoverIntent = ((UserRecoverableAuthException)e).getIntent();
        else if (e instanceof UserRecoverableAuthIOException)
            recoverIntent = ((UserRecoverableAuthIOException)e).getIntent();

        if (recoverIntent != null) {
            startActivityForResult(recoverIntent, RC_REQUEST_AUTHORIZATION);
            return true;
        }

        return false;
    }

    protected void reportError(String msg, Exception e) {
        e.printStackTrace();
        Log.e(TAG, msg, e);

        if (tryRecoverFrom(e))
            return;

        new AlertDialog.Builder(this)
                .setMessage(e.toString())
                .setTitle(msg)
                .setCancelable(true)
                .show();
    }
}
