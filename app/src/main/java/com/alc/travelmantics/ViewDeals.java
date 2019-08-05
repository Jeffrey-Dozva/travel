package com.alc.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alc.travelmantics.adapters.DealAdapter;
import com.alc.travelmantics.models.Deal;
import com.alc.travelmantics.utils.FirebaseUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewDeals extends AppCompatActivity {
  private static final int RC_SIGN_IN = 123;
  private Boolean isAdmin = false;
  RecyclerView deals_recycler_view;
  DealAdapter dealAdapter;
  private FirebaseAuth.AuthStateListener mAuthListener;
  private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
  private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("deals");
  private DatabaseReference adminsDatabase = FirebaseDatabase.getInstance().getReference("deals");
  private ArrayList<Deal> pDeals = new ArrayList<Deal>();
  private LinearLayoutManager dealsLayoutManager;
  private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//  public static void checkAdmin(String uid) {
//    isAdmin = false;
//
//    ref.keepSynced(true);
//  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_deals);
    Fresco.initialize(this);
    checkAdmin();
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
          signIn();

        } else {
          getData();
          Toast.makeText(ViewDeals.this, "Welcome back", Toast.LENGTH_LONG).show();
        }

      }
    };
    deals_recycler_view = findViewById(R.id.deals_recycler_view);

    dealsLayoutManager = new LinearLayoutManager(this);
    deals_recycler_view.setLayoutManager(dealsLayoutManager);


  }

  public void checkAdmin() {
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

  public void getData() {
    mDatabase.orderByChild("title").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        pDeals = new ArrayList<>();
        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
          Deal dealDetails = dataSnapshot1.getValue(Deal.class);
          dealDetails.setId(dataSnapshot1.getKey());
          pDeals.add(dealDetails);
        }

        dealAdapter = new DealAdapter(ViewDeals.this, pDeals);
        deals_recycler_view.setAdapter(dealAdapter);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(ViewDeals.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
      }
    });
    mDatabase.keepSynced(true);
  }

  public void signIn() {
    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
        new AuthUI.IdpConfig.EmailBuilder().build(),
        new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
    startActivityForResult(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build(),
        RC_SIGN_IN);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN) {
      IdpResponse response = IdpResponse.fromResultIntent(data);

      if (resultCode == RESULT_OK) {
        // Successfully signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // ...
      } else {
        // Sign in failed. If response is null the user canceled the
        // sign-in flow using the back button. Otherwise check
        // response.getError().getErrorCode() and handle the error.
        // ...
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.create_deal:
        Intent intent = new Intent(this, DealActivity.class);
        startActivity(intent);
        break;
      case R.id.logout:
        mFirebaseAuth.signOut();
//      default:

    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.deals_activity_menu, menu);

//    MenuItem createDealMenu = menu.findItem(R.id.create_deal);
//
//    if (isAdmin == true) {
//      createDealMenu.setVisible(true);
//    } else {
//      createDealMenu.setVisible(false);
//    }
    return true;
  }

  @Override
  protected void onStart() {
    super.onStart();
//    if (user == null) {
//      signIn();
//
//    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mAuthListener != null) {
      mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mAuthListener != null) {
      mFirebaseAuth.addAuthStateListener(mAuthListener);
    }
  }
}
