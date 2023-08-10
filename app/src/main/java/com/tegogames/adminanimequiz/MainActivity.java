package com.tegogames.adminanimequiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tegogames.adminanimequiz.Adapter.CategoryAdapter;
import com.tegogames.adminanimequiz.Models.CategoryModel;
import com.tegogames.adminanimequiz.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    FirebaseDatabase database;
    FirebaseStorage storage;

    CircleImageView categoryImage;
    EditText categoryName;
    Button uploadCategory;
    View fetchImage;
    Dialog dialog;

    Uri imageUri;

    ProgressDialog progressDialog;
    int i =0;

    ArrayList<CategoryModel> list;
    CategoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        storage =FirebaseStorage.getInstance();

        list = new ArrayList<>();

        dialog= new Dialog(this);
        dialog.setContentView(R.layout.item_add_category_dialog);


        if (dialog.getWindow()!=null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
        }

        progressDialog =new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Plase Waite");

        uploadCategory = dialog.findViewById(R.id.btnUpload);
        categoryImage = dialog.findViewById(R.id.categoryImage3);
        categoryName = dialog.findViewById(R.id.inputCategoryName);
        fetchImage =dialog.findViewById(R.id.fetchImage);


        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        binding.recyclerCategory.setLayoutManager(layoutManager);

        adapter = new CategoryAdapter(this,list);

        binding.recyclerCategory.setAdapter(adapter);
        database.getReference().child("categories").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Object valueFromDatabase = snapshot.child("setNum").getValue();
                Object valueFromDatabas;

                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("categoryName").getValue() != null &&
                                dataSnapshot.child("categoryImage").getValue() != null &&
                                dataSnapshot.child("setNum").getValue() != null) {

                            String categoryName = dataSnapshot.child("categoryName").getValue().toString();
                            String categoryImage = dataSnapshot.child("categoryImage").getValue().toString();
                            String key = dataSnapshot.getKey();

                            int intValue = 0;  // القيمة الافتراضية
                               valueFromDatabase = dataSnapshot.child("setNum").getValue();

                            if (valueFromDatabase != null) {
                                try {
                                    intValue = Integer.parseInt(valueFromDatabase.toString());
                                } catch (NumberFormatException e) {
                                    // يمكنك تنفيذ إجراءات خاصة هنا إذا فشل التحويل
                                }
                            }

                            list.add(new CategoryModel(categoryName, categoryImage, key, intValue));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "No categories", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        binding.addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        fetchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });


        uploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = categoryName.getText().toString();

                if (imageUri == null) {
                    Toast.makeText(MainActivity.this, "Enter Image ", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()) {
                    categoryName.setError("Enter Category Name");
                }

                else {
                    progressDialog.show();
                    uploadData();
                }
            }

        });


    }

    private void uploadData() {
    final StorageReference reference =storage.getReference().child("category")
            .child(new Date().getTime()+"");

    reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    CategoryModel categoryModel = new CategoryModel();
                    categoryModel.setCategoryName(categoryName.getText().toString());
                    categoryModel.setSetNum(0);
                    categoryModel.setCategoryImage(uri.toString()); // استخدم uri.toString() بدلاً من imageUri.toString()

                    database.getReference().child("categories").child("category" + i++)
                            .setValue(categoryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "data upload", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            });

        }
    });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==1){
            if (data!=null){
                imageUri = data.getData();
                categoryImage.setImageURI(imageUri);
            }
        }
    }
}