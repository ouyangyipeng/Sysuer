package com.sysu.edu.todo.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sysu.edu.databinding.ItemTitleBinding;

public class TitleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    String title = "";
    public TitleAdapter(Context context){
        super();
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        this.title = title;
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(ItemTitleBinding.inflate(LayoutInflater.from(context), parent, false).getRoot()){};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemTitleBinding.bind(holder.itemView).title.setText(title);
    }
    @Override
    public int getItemCount() {
        return 0;
    }
        @Override
    public int getItemViewType(int position) {
        return 2;
    }
}
