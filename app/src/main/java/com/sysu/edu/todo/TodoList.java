package com.sysu.edu.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class TodoList {


    private final SQLiteDatabase db;

    public TodoList(Context context){
        db = context.openOrCreateDatabase("todo.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS todos (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, due_date DATETIME, done_date DATETIME, creat_time DATETIME DEFAULT CURRENT_TIMESTAMP, updat_time DATETIME DEFAULT CURRENT_TIMESTAMP, status INTEGER DEFAULT 0, priority INTEGER DEFAULT 0, todo_type INTEGER DEFAULT 0,subtask TEXT,attachment TEXT,subject TEXT, location TEXT,color TEXT,label TEXT,ddl DATETIME DEFAULT CURRENT_TIMESTAMP);");
    }

    public void close(){
        db.close();
    }

    public void delete(){
        db.delete("todos", "id  = ?", new String[]{"1"});
    }
    public void add(){
        ContentValues value = new ContentValues();
        value.put("title", "标题");
        value.put("description", "描述");
        value.put("due_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        value.put("status", Status.TODO.ordinal());
        value.put("priority", 0);
        value.put("todo_type", Type.HOMEWORK.ordinal());
        value.put("subtask", "['子任务1','子任务2']");
        value.put("attachment", "0");
        value.put("subject", "大学英语");
        value.put("location", "教学楼A座");
        value.put("color", "red");
        value.put("label", "#标签");
        db.insert("todos", null, value);
    }


    public SQLiteDatabase getDatabase() {
        return db;
    }
    public enum Status {
        TODO,
        DONE,
        OVERDUE,
        DELETED
    }
    public enum Type {
        HOMEWORK,
        EXAM,
        MEETING,
        VOLUNTEER,
        SPORTS,
        STUDY,
        TRAVEL,
        OTHER
    }

}
