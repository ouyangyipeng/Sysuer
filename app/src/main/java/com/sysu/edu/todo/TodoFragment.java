package com.sysu.edu.todo;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sysu.edu.databinding.FragmentTodoBinding;
import com.sysu.edu.databinding.ItemTodoBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TodoFragment extends Fragment {

    FragmentTodoBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentTodoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        TodoAdapter adp = new TodoAdapter(requireContext());
        binding.recyclerView.setAdapter(adp);
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        adp.add(new HashMap<>(Map.of("title","title", "description", "description", "dueDate", "dueDate")));
        TodoViewModel viewModel = new ViewModelProvider(requireActivity()).get(TodoViewModel.class);
        viewModel.getTodoItem().observe(getViewLifecycleOwner(), adp::add);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    ArrayList<HashMap<String,String>> data = new ArrayList<>();
    public TodoAdapter(Context context) {
        super();
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTodoBinding binding = ItemTodoBinding.inflate(LayoutInflater.from(context), parent, false);
        return new RecyclerView.ViewHolder(binding.getRoot()) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemTodoBinding binding = ItemTodoBinding.bind(holder.itemView);
        HashMap<String,String> item = data.get(position);
        binding.title.setText(item.get("title"));
        binding.description.setText(item.get("description"));
        binding.getRoot().setOnClickListener(v -> {

        });
        binding.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.title.setPaintFlags(isChecked ? binding.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : binding.title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            binding.description.setPaintFlags(isChecked ? binding.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : binding.description.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            binding.getRoot().setAlpha(isChecked ? 0.5f : 1f);
            binding.menu.setEnabled(isChecked);
        });
        //binding.dueDate.setText(item.get("due_date"));
    }

    public void add(HashMap<String,String> item){
        data.add(item);
        notifyItemInserted(data.size()-1);
    }
    public void clear(){
        int tmp = getItemCount();
        data.clear();
        notifyItemRangeRemoved(0, tmp);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
}