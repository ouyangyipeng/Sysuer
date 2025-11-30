package com.sysu.edu.todo;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sysu.edu.databinding.ActivityTodoBinding;

import java.util.HashMap;
import java.util.Map;

public class TodoActivity extends AppCompatActivity {

    TodoList todoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTodoBinding binding = ActivityTodoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TodoViewModel viewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        binding.tool.setNavigationOnClickListener(view -> supportFinishAfterTransition());
        binding.add.setOnClickListener(view -> {
        });
        //binding.fragment_todo.getFragment.setAdapter(new TodoAdapter(this));
        todoDB = new TodoList(this);
        //todoDB.add();
        try {
            Cursor cursor = todoDB.getDatabase().query("todos", null, null, null, null, null, "due_date");
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"));
                viewModel.setTodoItem(new HashMap<>(Map.of("title",title, "description", description, "dueDate", dueDate)));
                //System.out.println("Title: " + title + ", Description: " + description + ", Due Date: " + dueDate);
            }
            cursor.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
