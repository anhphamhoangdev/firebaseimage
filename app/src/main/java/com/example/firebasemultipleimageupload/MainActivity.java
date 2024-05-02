package com.example.firebasemultipleimageupload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView tvCountImages;
    Button btnSelectImages, btnUploadImages, btnShowImages;
    private ArrayList<Uri> arrayList;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if(o.getResultCode() == RESULT_OK)
            {
                arrayList.clear();
                if(o.getData() != null && o.getData().getClipData() != null)
                {
                    int count = o.getData().getClipData().getItemCount();
                    for(int i = 0 ; i < count ; i++)
                    {
                        Uri imageUri = o.getData().getClipData().getItemAt(i).getUri();
                        arrayList.add(imageUri);
                    }
                    if(!arrayList.isEmpty())
                    {
                        btnUploadImages.setEnabled(true);
                        if(arrayList.size() <= 1)
                        {
                            tvCountImages.setText(MessageFormat.format("{0} Image selected.", arrayList.size()));
                        }else
                        tvCountImages.setText(MessageFormat.format("{0} Images selected.", arrayList.size()));
                    }
                }

            }

        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        FirebaseApp.initializeApp(MainActivity.this);

        arrayList = new ArrayList<>();
        btnSelectImages = findViewById(R.id.selectImages);
        btnUploadImages = findViewById(R.id.uploadImages);
        btnShowImages = findViewById(R.id.showImages);
        tvCountImages = findViewById(R.id.selectedTV);

        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(intent);
            }
        });

        btnUploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUploadImages.setText("Uploading...");
                btnUploadImages.setEnabled(false);

                uploadImages(new ArrayList<>());
            }
        });


        btnShowImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowImagesActivity.class);
                startActivity(intent);
            }
        });


    }

    private void uploadImages(ArrayList<String> imageUrlList) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child(UUID.randomUUID().toString());
        Uri uri = arrayList.get(imageUrlList.size());
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String url = task.getResult().toString();
                        imageUrlList.add(url);

                        if(arrayList.size() == imageUrlList.size())
                        {
                            Toast.makeText(MainActivity.this, "Images Uploaded Successfully...!", Toast.LENGTH_SHORT).show();
                            btnUploadImages.setText("Upload images");
                            btnUploadImages.setEnabled(false);
                            tvCountImages.setText("");
                        }
                        else
                        {
                            uploadImages(imageUrlList);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to upload...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to upload...", Toast.LENGTH_SHORT).show();
            }
        });
    }

}