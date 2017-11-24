package com.manan.dev.clubconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class AddNewCoordinatorActivity extends AppCompatActivity {

    private EditText coordName;
    private EditText coordEmail;
    private EditText coordPhone;
    private ImageView imgCoord;
    private FloatingActionButton submitCoord;
    private Coordinator coordinator;
    private int PICK_IMAGE_REQUEST = 111;
    private String clubName;
    private Uri userImage;

    private StorageReference firebaseStorage;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_coordinator);

        coordinator = new Coordinator();

        coordName = (EditText) findViewById(R.id.input_coord_name);
        coordEmail = (EditText) findViewById(R.id.et_email);
        coordPhone = (EditText) findViewById(R.id.et_phone);
        imgCoord = (ImageView) findViewById(R.id.img_coord);
        submitCoord = (FloatingActionButton) findViewById(R.id.bt_coord_submit);
        firebaseStorage = FirebaseStorage.getInstance().getReference();

        clubName = getIntent().getStringExtra("clubName");

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

        ImageView profilePhoto = (ImageView) findViewById(R.id.img_coord);
        Picasso.with(AddNewCoordinatorActivity.this).load(R.drawable.login_back).transform(new CircleTransform()).into(profilePhoto);

        imgCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        submitCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CreateCoordinator();

            }
        });
    }

    private void CreateCoordinator() {
        Boolean checker = (!coordName.getText().toString().equals("")&&!coordEmail.getText().toString().equals("")&&!coordPhone.getText().toString().equals("")&&userImage.toString().equals(""));

        if(checker) {
            pd.show();

            coordinator.setName(coordName.getText().toString());
            coordinator.setEmail(coordEmail.getText().toString());
            coordinator.setPhone(coordPhone.getText().toString());
            uploadImage();
        }
        else{
            if(coordName.getText().toString().equals("")){
                coordName.setError("Required");
            }
            if(coordEmail.getText().toString().equals("")){
                coordEmail.setError("Required");
            }
            if(coordPhone.getText().toString().equals("")){
                coordPhone.setError("Required");
            }
            pd.hide();
        }
    }

    private void uploadImage() {
        Bitmap bmp = null;
        String imgName = userImage.getLastPathSegment();
        try {

            bmp = MediaStore.Images.Media.getBitmap(AddNewCoordinatorActivity.this.getContentResolver(), userImage);
            bmp = Bitmap.createScaledBitmap(bmp, 500, (int) ((float) bmp.getHeight() / bmp.getWidth() * 500), true);

            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, boas);

            imgName = imgName.replace('.', '@');
            int lastIndex = imgName.lastIndexOf('@');
            String imgExtension = imgName.substring(lastIndex + 1);
            StorageReference childRef = firebaseStorage.child(coordName.getText().toString() + "." + imgExtension);

            UploadTask uploadTask = childRef.putBytes(boas.toByteArray());

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AddNewCoordinatorActivity.this, "Image Upload successful", Toast.LENGTH_SHORT).show();
                    Uri uri = taskSnapshot.getDownloadUrl();
                    assert uri != null;
                    coordinator.setPhoto(uri.toString());

                    FirebaseDatabase.getInstance().getReference().child("coordinators").child(clubName).push().setValue(coordinator).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddNewCoordinatorActivity.this, "Coordinator is added!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                                Intent i = new Intent(AddNewCoordinatorActivity.this, AdminDashboardActivity.class);
                                startActivity(i);
                                //  finish();
                            } else {
                                Toast.makeText(AddNewCoordinatorActivity.this, "coordinator add failed", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddNewCoordinatorActivity.this, "Image Upload failed", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            userImage = data.getData();
            int finalWidth = 100;
            try {
                Bitmap bitmap;
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), userImage);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) finalWidth, (int) finalWidth, true);
                imgCoord.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(AddNewCoordinatorActivity.this, "Upload Image", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }

    }



}
