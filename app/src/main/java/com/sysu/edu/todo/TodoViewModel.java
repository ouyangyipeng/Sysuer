package com.sysu.edu.todo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class TodoViewModel extends ViewModel {
    MutableLiveData<HashMap<String,String>> todoItem = new MutableLiveData<>();

    public MutableLiveData<HashMap<String, String>> getTodoItem() {
        return todoItem;
    }

    public void setTodoItem(HashMap<String, String> todoItem) {
        getTodoItem().setValue(todoItem);
    }
}
