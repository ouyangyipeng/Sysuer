package com.sysu.edu.academic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.snackbar.Snackbar;
import com.sysu.edu.R;
import com.sysu.edu.api.Params;
import com.sysu.edu.databinding.ItemEvaluationBinding;
import com.sysu.edu.databinding.RecyclerViewScrollBinding;
import com.sysu.edu.extra.LoginActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EvaluationCategoryFragment extends Fragment {
    Params params;
    Handler handler;
    ActivityResultLauncher<Intent> launch;
    ArrayList<JSONObject> evaluations = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerViewScrollBinding binding = RecyclerViewScrollBinding.inflate(inflater, container, false);
        params = new Params(requireActivity());
        StaggeredGridLayoutManager sgm = new StaggeredGridLayoutManager(params.getColumn(), 1);
        binding.getRoot().setLayoutManager(sgm);
        CategoryAdapter adp = new CategoryAdapter(requireContext());
        binding.getRoot().setAdapter(adp);
        launch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if (o.getResultCode() == Activity.RESULT_OK) {
                getEvaluation();
            }
        });

        getEvaluation();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    JSONObject data = JSON.parseObject((String) msg.obj);
                    if (data.get("code").equals("200")) {
                        data.getJSONObject("result").getJSONArray("list").forEach(e -> evaluations.add((JSONObject) e));
                        adp.set(evaluations);
                    } else {
                        launch.launch(new Intent(requireContext(), LoginActivity.class));
                    }
                } else if (msg.what == -1) {
                    params.toast(R.string.no_wifi_warning);
                    Snackbar.make(binding.getRoot(), "去登录", Snackbar.LENGTH_LONG).setAction("登录", v -> launch.launch(new Intent(requireContext(), LoginActivity.class).putExtra("url", "https://pjxt.sysu.edu.cn"))).show();
                }
            }
        };
        return binding.getRoot();
    }
    public void getEvaluation() {
        new OkHttpClient.Builder().build().newCall(new Request.Builder().url("https://pjxt.sysu.edu.cn/personnelEvaluation/listObtainPersonnelEvaluationTasks?pageNum=1&pageSize=10")
                .header("Cookie", params.getCookie())
                //.addHeader("Cookie", "JSESSIONID=F547A1B2729098E0B101716397DC48DC;INCO=9b1595d95278e78f17d51a5f35287020;")
                // .post(RequestBody.create("{\"acadYear\":\"2024-2\",\"examWeekId\":\"1864116471884476417\",\"examWeekName\":\"18-19周期末考\",\"examDate\":\"\"}", MediaType.parse("application/json")))
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message message = new Message();
                message.what = -1;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = response.body().string();
                handler.sendMessage(msg);
            }
        });
    }

    public static class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        ArrayList<JSONObject> data = new ArrayList<>();

        public CategoryAdapter(Context context) {
            super();
            this.context = context;
        }

        public void set(ArrayList<JSONObject> mData) {
            clear();
            data.addAll(mData);
            notifyItemRangeInserted(0, mData.size());
        }


        public void clear() {
            int tmp = getItemCount();
            data.clear();
            notifyItemMoved(0, tmp);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(ItemEvaluationBinding.inflate(LayoutInflater.from(context), parent, false).getRoot()) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ItemEvaluationBinding binding = ItemEvaluationBinding.bind(holder.itemView);
            binding.open.setOnClickListener(v -> {
                NavController navController = ((NavHostFragment) Objects.requireNonNull(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment))).getNavController();
                navController.navigate(R.id.from_category_to_course);
            });
            holder.itemView.setOnClickListener(v -> {
            });
            binding.title.setText(data.get(position).getString("rwmc"));
            binding.startTime.setText(String.format("起始时间：%s", data.get(position).getString("rwkssj")));
            binding.endTime.setText(String.format("结束时间：%s", data.get(position).getString("rwjssj")));
            binding.total.setText(String.format("总评数：%s", data.get(position).getString("pjsl")));
            binding.totalFor.setText(String.format("已评数：%s", data.get(position).getString("ypsl")));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}