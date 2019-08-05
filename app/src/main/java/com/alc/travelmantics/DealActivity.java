package com.alc.travelmantics;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alc.travelmantics.models.Deal;
import com.alc.travelmantics.utils.FirebaseUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DealActivity extends AppCompatActivity {
  private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
  private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("deals");
  private TextInputLayout deal_title_container, deal_description_container, deal_price_container;
  private String deal_title, deal_description, deal_price;
  private Deal deal;
  private Uri selectedImage;
  private MaterialButton select_image;
  private SimpleDraweeView image_preview;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_deal);
    FirebaseUtil.openFbReference("deals", this);
    deal_title_container = findViewById(R.id.deal_title_container);
    deal_description_container = findViewById(R.id.deal_description_container);
    deal_price_container = findViewById(R.id.deal_price_container);
    select_image = findViewById(R.id.select_image);
    image_preview = findViewById(R.id.image_preview);


    Intent intent = getIntent();
    deal = (Deal) intent.getSerializableExtra("deal");
    if (deal == null) {
      deal = new Deal();
    }
    deal_title_container.getEditText().setText(deal.getTitle());
    deal_description_container.getEditText().setText(deal.getDescription());
    deal_price_container.getEditText().setText(deal.getPrice());

    if (deal.getImageUrl() != null) {
      Uri uri = Uri.parse(deal.getImageUrl() );

      image_preview.setImageURI(uri.toString());

    }

    select_image.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        pickFromGallery();
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.save_menu:
        saveDeal();
        Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
        clean();
        break;
      case R.id.delete_deal:
        deleteDeal();
        Toast.makeText(this, "Deal Deleted", Toast.LENGTH_SHORT).show();
        break;
//      default:

    }
    return super.onOptionsItemSelected(item);
  }

  private void clean() {
    deal_title_container.getEditText().setText("");
    deal_description_container.getEditText().setText("");
    deal_price_container.getEditText().setText("");
  }

  private void saveDeal() {
    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("dealImages" + "/" + user.getUid());
    mDatabase = mDatabase.push();
    deal_title = deal_title_container.getEditText().getText().toString().trim();
    deal_description = deal_description_container.getEditText().getText().toString().trim();
    deal_price = deal_price_container.getEditText().getText().toString().trim();

    deal.setTitle(deal_title);
    deal.setDescription(deal_description);
    deal.setPrice(deal_price);


    StorageMetadata metadata = new StorageMetadata.Builder()
        .setContentType("image/jpeg")
        .build();

    UploadTask uploadTask = filePath.putFile(selectedImage, metadata);

    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override
          public void onSuccess(Uri uri) {
            mDatabase.child("imageUrl").setValue(uri.toString());

          }
        });
      }
    });
    if (deal.getId() == null) {
      mDatabase.setValue(deal);

    } else {
      mDatabase.child(deal.getId()).setValue(deal);
    }
    finish();

  }

  private void deleteDeal() {
    if (deal == null) {
      Toast.makeText(this, "Please save deal before deleting", Toast.LENGTH_SHORT).show();
      return;
    } else {
      mDatabase.child(deal.getId()).removeValue();
      finish();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.save_menu, menu);
    return true;
  }

  //open picker code
  private void pickFromGallery() {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    String[] mimeTypes = {"image/jpeg", "image/png"};
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
    startActivityForResult(intent, 1);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK)
      switch (requestCode) {
        case 1:
          image_preview.setImageURI(data.getData());
          selectedImage = data.getData();

          break;

      }
  }


}
