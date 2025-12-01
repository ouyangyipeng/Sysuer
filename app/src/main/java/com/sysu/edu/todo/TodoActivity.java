package com.sysu.edu.todo;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sysu.edu.R;
import com.sysu.edu.databinding.ActivityTodoBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TodoActivity extends AppCompatActivity {

    TodoList todoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTodoBinding binding = ActivityTodoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TodoViewModel viewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        binding.tool.setNavigationOnClickListener(view -> supportFinishAfterTransition());AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(R.layout.dialog_todo)
                .setPositiveButton(R.string.submit, (dialog1, which) -> {
                    // 处理提交按钮点击事件
                })
                .setNegativeButton(R.string.cancel, (dialog12, which) -> {
                    // 处理取消按钮点击事件
                })
                .create();
        Window window = Objects.requireNonNull(dialog.getWindow());
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(R.drawable.top_capsule);
        window.setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setEnterTransition(new ChangeImageTransform());
//        window.setExitTransition(new ChangeImageTransform());
        window.setWindowAnimations(com.google.android.material.R.style.Animation_MaterialComponents_BottomSheetDialog);
        binding.add.setOnClickListener(view -> dialog.show());
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
