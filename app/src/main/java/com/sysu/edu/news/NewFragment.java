package com.sysu.edu.news;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.textview.MaterialTextView;
import com.sysu.edu.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewFragment extends Fragment{
    View view;
    String cookie;
    OkHttpClient http=new OkHttpClient.Builder().build();
    Handler handler;
    public NewFragment(String cookie){
        this.cookie=cookie;
    }
    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        if (view == null) {
            view = LayoutInflater.from(requireActivity()).inflate(R.layout.news_page, container, false);
            RecyclerView list=view.findViewById(R.id.news_page);
            list.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
            class Adp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
                ArrayList<HashMap<String,String>> data=new ArrayList<>();
                public Adp(){
                    super();
                }

                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new RecyclerView.ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false)) {
                    };
                }
                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                    MaterialTextView title = holder.itemView.findViewById(R.id.title);
                    MaterialTextView content= holder.itemView.findViewById(R.id.content);
                    AppCompatImageView image= holder.itemView.findViewById(R.id.image);
                    title.setText(data.get(position).get("title"));
                  //  content.setText(data.get(position).get("content"));
//                    Glide.with(requireContext()).load(new GlideUrl(data.get(position).get("image"), new LazyHeaders.Builder().addHeader("Cookie","").build())).into(image);
                }
                void add(String title){
                    data.add(new HashMap<>(Map.of("title",title)));
                    notifyItemInserted(getItemCount()-1);
                }
                @Override
                public int getItemCount() {
                    return data.size();
                }
            }
            Adp adp = new Adp();
            list.setAdapter(adp);
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    JSONObject data = JSON.parseObject((String) msg.obj);
                    System.out.println(data);
                    data.getJSONObject("data").getJSONArray("records").forEach(e->{
                        String title = ((JSONObject) e).getString("title");
                        System.out.println(title);
                        adp.add(title);
                        //String title = ((JSONObject) e).getString("title");
                       // String title = ((JSONObject) e).getString("title");
                    });
                }
            };

        }
        getSubscription();
        return view;
    }


    void getSubscription(){
                http.newCall(new Request.Builder().url("https://iportal-443.webvpn.sysu.edu.cn/ai_service/content-portal/user/content/page")
                        .post(RequestBody.create("{\"pageSize\":20,\"currentPage\":1,\"apiCode\":\"3ytr4e6c\",\"notice\":false}", MediaType.parse("application/json")))
                                .header("Content-type","application/json")
                                .header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidXNlcl9tYW5hZ2VyIl0sImNsaWVudF9pZF9zeXMiOiJ6c3NlYXJjaF8xMDAwNTAiLCJ1c2VyX25hbWUiOiLllJDotKTmoIciLCJzY29wZSI6WyJhbGwiXSwibmFtZSI6IuWUkOi0pOaghyIsImV4cCI6MTc0Mzc4MDEzNiwiYXV0aG9yaXRpZXMiOlsiQURNSU4iXSwianRpIjoiOG1KZDducmJyWXd0VmJEWEs0S2p3TDVPcEdVIiwiY2xpZW50X2lkIjoiMTY3M2YwMWQ5NjFhNjEwZmU5MjIwZWZmMGQ3YjNiYzQiLCJ1c2VybmFtZSI6IuWUkOi0pOaghyJ9.QprOrnDWcX2xVbnE96AX__SxDEPkcw-x-T4jM-pbmZg")
                                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0.0")
                        .header("Cookie",cookie+";_webvpn_key=eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoidGFuZ3hiNiIsImdyb3VwcyI6WzNdLCJpYXQiOjE3NDM3NTUzNjYsImV4cCI6MTc0Mzg0MTc2Nn0.Ngx_XcBvxk29xQUoUCUmEDb9r1WOOffZgSzrH18sMic;").build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Message msg = new Message();
                        if (response.body() != null) {
                            msg.obj=response.body().string();
                        }
                       handler.sendMessage(msg);
                    }
                });
    }
}
