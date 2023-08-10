package com.tegogames.adminanimequiz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tegogames.adminanimequiz.Models.CategoryModel;
import com.tegogames.adminanimequiz.R;
import com.tegogames.adminanimequiz.SetsActivity;
import com.tegogames.adminanimequiz.databinding.ItemCategoryBinding;

import java.util.ArrayList;
import java.util.Set;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewHolaer>{


    Context context;
    ArrayList<CategoryModel> list;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolaer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_category,parent,false);
        return new viewHolaer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolaer holder, int position) {
        CategoryModel model= list.get(position);
        holder.binding.categoryName2.setText(model.getCategoryName());

        Picasso.get()
                .load(model.getCategoryImage())
                .placeholder(R.drawable.enosky)
                .into(holder.binding.categoryImage3);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SetsActivity.class);
                intent.putExtra("category",model.getCategoryName());
                intent.putExtra("sets",model.getSetNum());
                intent.putExtra("key",model.getKey());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolaer extends RecyclerView.ViewHolder{

        ItemCategoryBinding binding;
        public viewHolaer(@NonNull View itemView) {
            super(itemView);

            binding = ItemCategoryBinding.bind(itemView);
        }
    }
}
