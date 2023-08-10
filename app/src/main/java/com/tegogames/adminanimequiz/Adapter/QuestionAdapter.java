package com.tegogames.adminanimequiz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tegogames.adminanimequiz.Models.QuestionModel;
import com.tegogames.adminanimequiz.R;
import com.tegogames.adminanimequiz.databinding.ItemQuestionBinding;

import java.util.ArrayList;

public class QuestionAdapter extends  RecyclerView.Adapter<QuestionAdapter.viewHolder>{


    Context context;
    ArrayList<QuestionModel> list;
    String categoryName;
    DeleteListener listener;

    public QuestionAdapter(Context context, ArrayList<QuestionModel> list, String categoryName, DeleteListener listener) {
        this.context = context;
        this.list = list;
        this.categoryName = categoryName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_question,parent,false);


        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {


        QuestionModel model = list.get(position);

        holder.binding.question.setText(model.getQuestion());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLongClick(position,list.get(position).getKey());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ItemQuestionBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);


            binding = ItemQuestionBinding.bind(itemView);
        }
    }

public interface DeleteListener{

        public void onLongClick(int position,String id);
}
}
