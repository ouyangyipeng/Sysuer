package com.sysu.edu.news;

import android.content.Intent;
import android.net.Uri;
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
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.textview.MaterialTextView;
import com.sysu.edu.R;
import com.sysu.edu.academic.BrowseActivity;

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
    int position;
    OkHttpClient http=new OkHttpClient.Builder().build();
    Handler handler;
    String authorization="Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidXNlcl9tYW5hZ2VyIl0sImNsaWVudF9pZF9zeXMiOiJ6c3NlYXJjaF8xMDAwNTAiLCJ1c2VyX25hbWUiOiLllJDotKTmoIciLCJzY29wZSI6WyJhbGwiXSwibmFtZSI6IuWUkOi0pOaghyIsImV4cCI6MTc0Mzg2NDIzMiwiYXV0aG9yaXRpZXMiOlsiQURNSU4iXSwianRpIjoidDVtRWVpeTNDMHRNRHJ3YzJhRW5OeE9tU0NFIiwiY2xpZW50X2lkIjoiMTY3M2YwMWQ5NjFhNjEwZmU5MjIwZWZmMGQ3YjNiYzQiLCJ1c2VybmFtZSI6IuWUkOi0pOaghyJ9.cteoSwZtsrJSTtbS-DRrpwjXeXfjXW_TX0WYIg3zP3k";
    public NewFragment(String cookie,int pos){
        this.cookie=cookie+";_webvpn_key=eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoidGFuZ3hiNiIsImdyb3VwcyI6WzNdLCJpYXQiOjE3NDM4NDE1MTQsImV4cCI6MTc0MzkyNzkxNH0.ToBD9pn52FC8FIHDP1CyChKPqBOBpW1RBJUdNDezgDo;";
        this.position=pos;
        //System.out.println(cookie);
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
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(view.getContext(), BrowseActivity.class).setData(Uri.parse(data.get(position).get("url"))), ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),view,"miniapp").toBundle());

                        }
                    });
                    title.setText(data.get(position).get("title"));
                  //  content.setText(data.get(position).get("content"));
                    String img=data.get(position).get("image");
                    if (img != null &&! img.isEmpty()) {System.out.println(img);
                        Glide.with(requireContext()).load(new GlideUrl(img,new LazyHeaders.Builder().addHeader("Cookie",cookie).addHeader("User-Agent","Wework").build()))
                                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)

                        .placeholder(R.drawable.logo)
                               // .override(1)
                        .override(400).fitCenter().transform(new RoundedCorners(16))
                                .into(image);
                    }
                }
                void add(String title,String image,String url){
                    data.add(new HashMap<>(Map.of("title",title,"image",image,"url",url)));
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
                    switch(msg.what){
                        case 1:System.out.println(msg.obj);break;
                        case 2:JSONObject data = JSON.parseObject((String) msg.obj);
                            Integer code = data.getInteger("code");
                            if(code==10000){
                                data.getJSONObject("data").getJSONArray("records").forEach(e->{
                                    String title = ((JSONObject) e).getString("title");
                                    JSONArray cover = ((JSONObject) e).getJSONArray("coversPicList");
                                    String image="";
                                     if (cover!=null && !cover.isEmpty()){
                                        image=cover.getJSONObject(0).getString("outLink");
                                    }
                                    String url = ((JSONObject) e).getString("url");
                                    adp.add(title,image,url);
                                    //String title = ((JSONObject) e).getString("title");
                                    // String title = ((JSONObject) e).getString("title");
                                });

                    };
                            break;
                        case 3:
                            JSONObject data2 = JSON.parseObject((String) msg.obj);
                            Integer code2 = data2.getInteger("code");
                            System.out.println(data2);
                            if(code2==10000){
                                data2.getJSONArray("data").forEach(e->{
                                    String title = ((JSONObject) e).getString("title");
                                    JSONArray cover = ((JSONObject) e).getJSONArray("coversPicList");
                                    String image = "";
                                    if (cover!=null && !cover.isEmpty()){
                                        image=cover.getJSONObject(0).getString("outLink");
                                    }
                                    if(image==null){
                                        image="";
                                    }String url = ((JSONObject) e).getString("url");
                                    adp.add(title,image,url);
                                    //String title = ((JSONObject) e).getString("title");
                                    // String title = ((JSONObject) e).getString("title");
                                });

                            }
                            break;
                }
                }
            };

        }
        switch (position){
            case 0:getSubscription();break;
            case 1:getNews();break;
        }
        //getAuthorization();
        return view;
    }

void getNews(){
    http.newCall(new Request.Builder().url("https://iportal-443.webvpn.sysu.edu.cn/ai_service/content-portal/recommend/query-recommend")
           // .post(RequestBody.create("{\"pageSize\":20,\"currentPage\":1,\"apiCode\":\"3ytr4e6c\",\"notice\":false}", MediaType.parse("application/json")))
                    .post(RequestBody.create("", MediaType.parse("application/json")))
            .header("Content-type","application/json")
            .header("Authorization",authorization)
            .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            .header("Cookie",cookie).build()).enqueue(new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {

        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if (response.body() != null) {
                Message msg = new Message();
                msg.what=3;
                msg.obj=response.body().string();
                handler.sendMessage(msg);
            }
        }
    });
}
    void getSubscription(){
                http.newCall(new Request.Builder().url("https://iportal-443.webvpn.sysu.edu.cn/ai_service/content-portal/user/content/page")
                        .post(RequestBody.create("{\"pageSize\":20,\"currentPage\":1,\"apiCode\":\"3ytr4e6c\",\"notice\":false}", MediaType.parse("application/json")))
                                .header("Content-type","application/json")
                                .header("Authorization",authorization)
                                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .header("Cookie",cookie).build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            Message msg = new Message();
                            msg.what=2;
                            msg.obj=response.body().string();
                            handler.sendMessage(msg);
                        }
                    }
                });
    }
    public void getAuthorization(){
        http.newCall(new Request.Builder().url("https://iportal-443.webvpn.sysu.edu.cn/ai_service/auth-center/account/zscasLogin?clientid=zssearch_100050;zsshow")
                        .header("Cookie",cookie)
                        .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0.0")
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Message msg = new Message();
                msg.what=1;
                msg.obj=response.header("Location");
                System.out.println(response.code());
                handler.sendMessage(msg);
            }
        });
    }
}
