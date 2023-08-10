package com.tegogames.adminanimequiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tegogames.adminanimequiz.Adapter.QuestionAdapter;
import com.tegogames.adminanimequiz.Models.QuestionModel;
import com.tegogames.adminanimequiz.databinding.ActivityAddQuestionBinding;
import com.tegogames.adminanimequiz.databinding.ActivityQuestionBinding;

import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {
    ActivityQuestionBinding binding;

    FirebaseDatabase database;

    ArrayList<QuestionModel> list;
    QuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        list= new ArrayList<>();

        int setNum = getIntent().getIntExtra("setNum",0);
        String categoryName = getIntent().getStringExtra("categoryName");


        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.recyQuestion.setLayoutManager(manager);

        adapter = new QuestionAdapter(this, list, categoryName, new QuestionAdapter.DeleteListener() {
            @Override
            public void onLongClick(int position, String id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);
                builder.setTitle("Delete Question");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    // الشيفرة لحذف السؤال
                    database.getReference().child("Sets").child(categoryName).child("questions")
                            .child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(QuestionActivity.this, "Question Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });


                });
                builder.setNegativeButton("No", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });

                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        binding.recyQuestion.setAdapter(adapter);


        database.getReference().child("Sets").child(categoryName).child("questions")
                        .orderByChild("setNum").equalTo(setNum)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    list.clear();


                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        QuestionModel model = dataSnapshot.getValue(QuestionModel.class);
                                        model.setKey(dataSnapshot.getKey());
                                        list.add(model);
                                    }
                                    adapter.notifyDataSetChanged();
                                }else {
                                    Toast.makeText(QuestionActivity.this, "Ther is No question", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


        binding.addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddQuestionActivity.class);
                intent.putExtra("setNum",setNum);
                intent.putExtra("category",categoryName);
                startActivity(intent);
            }
        });
    }
}