package com.tegogames.adminanimequiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.tegogames.adminanimequiz.Models.QuestionModel;
import com.tegogames.adminanimequiz.databinding.ActivityAddQuestionBinding;
import com.tegogames.adminanimequiz.databinding.ActivitySetsBinding;

public class AddQuestionActivity extends AppCompatActivity {



    ActivityAddQuestionBinding binding;

    int setNum;
    String categoryName;

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =ActivityAddQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        setNum = getIntent().getIntExtra("setNum",-1);
         categoryName = getIntent().getStringExtra("category");

         database = FirebaseDatabase.getInstance();

         if (setNum ==-1){
             finish();
             return;
         }

         binding.btnUploadQuestion.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 int correct = -1;

                 for (int i = 0; i < binding.optionContainer.getChildCount(); i++) {
                     EditText answer = (EditText) binding.answerContainer.getChildAt(i);

                     if (answer.getText().toString().isEmpty()) {
                         answer.setError("Required");
                         return;
                     }

                     RadioButton radioButton = (RadioButton) binding.optionContainer.getChildAt(i);
                     if (radioButton.isChecked()) {
                         correct = i;
                         break;
                     }
                 }

                 if (correct == -1) {
                     Toast.makeText(AddQuestionActivity.this, "Plase Mark the Correct Option", Toast.LENGTH_SHORT).show();
                     return;
                 }





                 QuestionModel model = new QuestionModel();

                     model.setQuestion(binding.inputQuestion.getText().toString());
                     model.setOption1(((EditText)binding.answerContainer.getChildAt(0)).getText().toString());
                     model.setOption2(((EditText)binding.answerContainer.getChildAt(1)).getText().toString());
                     model.setOption3(((EditText)binding.answerContainer.getChildAt(2)).getText().toString());
                     model.setOption4(((EditText)binding.answerContainer.getChildAt(3)).getText().toString());
                     model.setCorrectAnswer(((EditText)binding.answerContainer.getChildAt(correct)).getText().toString());
                     model.setSetNum(setNum);

                     database.getReference().child("Sets").child(categoryName).child("questions")
                             .push()
                             .setValue(model)
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     Toast.makeText(AddQuestionActivity.this, "questions Added", Toast.LENGTH_SHORT).show();
                                 }
                             });


                 }


         });
    }
}