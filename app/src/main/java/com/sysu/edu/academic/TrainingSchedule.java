package com.sysu.edu.academic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.chip.Chip;
import com.sysu.edu.R;
import com.sysu.edu.databinding.TrainingScheduleBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TrainingSchedule extends AppCompatActivity {
TrainingScheduleBinding binding;
    OkHttpClient http = new OkHttpClient.Builder().build();
    String cookie="";
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=TrainingScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cookie=getSharedPreferences("privacy",0).getString("Cookie","");
        setSupportActionBar(binding.tool);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        handler=new Handler(getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                JSONObject data = JSON.parseObject((String) msg.obj);
                if(data.getInteger("code")==200){
                    switch (msg.what){

                        case 1:{ArrayList<String> list = new ArrayList<>();
                            data.getJSONArray("data").forEach(e->{
                            list.add(((JSONObject)e).getString("departmentName"));
                        });
                            binding.college.setSimpleItems(list.toArray(new String[]{}));}
                        case 2:{ArrayList<String> list = new ArrayList<>();
                            data.getJSONArray("data").forEach(e->{
                                String n = ((JSONObject) e).getString("dataName");
                                list.add(n);
                                Chip chip= (Chip) getLayoutInflater().inflate(R.layout.chip,binding.grade,false);
                                chip.setText(n);
                                binding.grade.addView(chip);
                            });}
                            //binding.college.setSimpleItems(list.toArray(new String[]{}));}

                    }
                }
                super.handleMessage(msg);
            }
        };
        getColleges();
        getGrades();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }
    void getColleges(){
        http.newCall(new Request.Builder().url("https://jwxt.sysu.edu.cn/jwxt/base-info/department/recruitUnitPull").post(RequestBody.create("{\"departmentName\":null,\"subordinateDepartmentNumber\":null,\"id\":null}",MediaType.parse("application/json"))).header("Cookie",cookie).header("Referer", "https://jwxt.sysu.edu.cn/jwxt/mk/").build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Message msg = new Message();
                msg.what=1;
                if (response.body() != null) {
                    msg.obj=response.body().string();
                }
                handler.sendMessage(msg);
            }
        });
    }void getGrades(){
        http.newCall(new Request.Builder()
                .url("https://jwxt.sysu.edu.cn/jwxt/base-info/codedata/findcodedataNames?datableNumber=127").header("Cookie",cookie).header("Referer", "https://jwxt.sysu.edu.cn/jwxt/mk/").build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Message msg = new Message();
                msg.what=2;
                if (response.body() != null) {
                    msg.obj=response.body().string();
                }
                handler.sendMessage(msg);
            }
        });
    }
}