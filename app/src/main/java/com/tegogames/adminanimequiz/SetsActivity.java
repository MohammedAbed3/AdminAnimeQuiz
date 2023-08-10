package com.tegogames.adminanimequiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.tegogames.adminanimequiz.Adapter.GrideAdapter;
import com.tegogames.adminanimequiz.databinding.ActivitySetsBinding;

public class SetsActivity extends AppCompatActivity {

    ActivitySetsBinding binding;
    FirebaseDatabase database;

    GrideAdapter adapter;

    int a = 1;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        database = FirebaseDatabase.getInstance();
        key = getIntent().getStringExtra("key");
         adapter = new GrideAdapter(getIntent().getIntExtra("sets", 0),
          getIntent().getStringExtra("category"), key, new GrideAdapter.GridListener() {
      @Override
      public void addSet() {
          database.getReference().child("categories").child(key).child("setNum")
                  .setValue(getIntent().getIntExtra("sets",0)+a++).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            adapter.sets++;
                            adapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(SetsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                      }
                  });
      }
  });

  binding.gridview.setAdapter(adapter);

    }
}