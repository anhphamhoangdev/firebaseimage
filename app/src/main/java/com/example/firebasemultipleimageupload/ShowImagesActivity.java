package com.example.firebasemultipleimageupload;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasemultipleimageupload.adapter.ImageAdapter;
import com.example.firebasemultipleimageupload.model.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;


public class ShowImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_images);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List Images");
        int customColor = Color.parseColor("#03AED2"); // Màu custom (ví dụ: đỏ)
        ColorDrawable colorDrawable = new ColorDrawable(customColor);
        actionBar.setBackgroundDrawable(colorDrawable);

        //actionBar.hide();


        FirebaseStorage.getInstance().getReference().child("images").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                ArrayList<Image> arrayList = new ArrayList<>();
                ImageAdapter adapter = new ImageAdapter(ShowImagesActivity.this, arrayList);
                adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Image image) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(image.getUrl()), "image/*");
                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClick(Image image) {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference imageRef = storageRef.child("images").child(image.getName());

                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        arrayList.remove(image);
                                        Toast.makeText(ShowImagesActivity.this, "Deleted ! ", Toast.LENGTH_SHORT).show();
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ShowImagesActivity.this, "Delete Fail", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                recyclerView.setAdapter(adapter);

                listResult.getItems().forEach(new Consumer<StorageReference>() {
                    @Override
                    public void accept(StorageReference storageReference) {
                        Image image = new Image();
                        image.setName(storageReference.getName());
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String url = task.getResult().toString();
                                image.setUrl(url);
                                arrayList.add(image);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(ShowImagesActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}