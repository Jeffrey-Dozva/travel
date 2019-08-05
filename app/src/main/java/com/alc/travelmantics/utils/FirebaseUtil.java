package com.alc.travelmantics.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alc.travelmantics.ViewDeals;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
  private static final int RC_SIGN_IN = 123;
  public static FirebaseDatabase mFirebaseDatabase;
  public static DatabaseReference mDatabaseReference;
  public static FirebaseAuth mFirebaseAuth;
  public static FirebaseAuth.AuthStateListener mAuthStateListener;
  public static boolean isAdmin = false;
  private static FirebaseUtil firebaseUtil = new FirebaseUtil();
  private static Activity caller;

  private FirebaseUtil() {
  }

  private static void signIn() {
    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
        new AuthUI.IdpConfig.EmailBuilder().build(),
        new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
    caller.startActivityForResult(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build(),
        RC_SIGN_IN);
  }

  public static void openFbReference(String ref, final Activity callerActivity) {
    if (firebaseUtil == null) {
//      firebaseUtil =

      mFirebaseAuth = FirebaseAuth.getInstance();
      caller = callerActivity;
      checkAdmin();
    }

//    mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
  }

  public static void attachListener() {
    mFirebaseAuth.addAuthStateListener(mAuthStateListener);
  }

  public static void detachListener() {
    mFirebaseAuth.addAuthStateListener(mAuthStateListener);

  }

  public static void checkAdmin() {
    DatabaseReference adminsDatabase = FirebaseDatabase.getInstance().getReference("deals");

    adminsDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.d("dataSnapshot", "dataSnapshot" + dataSnapshot);
        isAdmin = true;
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        isAdmin = false;
      }
    });
    adminsDatabase.keepSynced(true);

  }

}
