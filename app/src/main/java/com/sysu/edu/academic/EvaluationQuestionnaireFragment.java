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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.sysu.edu.R;
import com.sysu.edu.api.Params;
import com.sysu.edu.databinding.DialogEditTextBinding;
import com.sysu.edu.databinding.ItemOptionBinding;
import com.sysu.edu.databinding.RecyclerViewScrollBinding;
import com.sysu.edu.extra.LoginActivity;
import com.sysu.edu.todo.info.TitleAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class EvaluationQuestionnaireFragment extends Fragment {
    Params params;
    Handler handler;
    ActivityResultLauncher<Intent> launch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerViewScrollBinding binding = RecyclerViewScrollBinding.inflate(inflater, container, false);
        params = new Params(requireActivity());
        //StaggeredGridLayoutManager sgm = new StaggeredGridLayoutManager(params.getColumn(), 1);
        binding.getRoot().setLayoutManager(new LinearLayoutManager(requireContext()));
        ConcatAdapter adp = new ConcatAdapter(new ConcatAdapter.Config.Builder().setIsolateViewTypes(true).build());
        binding.getRoot().setAdapter(adp);
        launch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if (o.getResultCode() == Activity.RESULT_OK) {
                getEvaluation(requireArguments().getString("rwid"),
                        requireArguments().getString("wjid"),
                        requireArguments().getString("sxz"),
                        requireArguments().getString("pjrdm"),
                        requireArguments().getString("bpdm"),
                        requireArguments().getString("kcdm"),
                        requireArguments().getString("rwh"));
            }
        });
        //{"rwid", "wjid","sxz","pjrdm","bpdm","kcdm","rwh"};
        getEvaluation(requireArguments().getString("rwid"),
                requireArguments().getString("wjid"),
                requireArguments().getString("sxz"),
                requireArguments().getString("pjrdm"),
                requireArguments().getString("bpdm"),
                requireArguments().getString("kcdm"),
                requireArguments().getString("rwh")
        );
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    JSONObject data = JSON.parseObject((String) msg.obj);
                    if (data.get("code").equals("200")) {
                        data.getJSONObject("result").getJSONArray("assessedObjList").forEach(l -> {
                            ((JSONObject) l).getJSONArray("bpdxList").forEach(list -> {
                                TitleAdapter name = new TitleAdapter(requireContext());
                                /*if (((JSONObject) list).getString("pjlxmc") != null && !((JSONObject) list).getString("pjlxmc").isEmpty()) {
                                    String bprmc = ((JSONObject) list).getString("bprmc");
                                    name.setTitle(bprmc + " - " + ((JSONObject) list).getString("kcmc"));
                                    name.setHeader(1);
                                    adp.addAdapter(name);
                                }*/
                                if (((JSONObject) list).getString("bprmc") != null && !((JSONObject) list).getString("bprmc").isEmpty()) {
                                    String bprmc = ((JSONObject) list).getString("bprmc");
                                    name.setTitle(bprmc + " - " + ((JSONObject) list).getString("kcmc"));
                                    name.setHeader(1);
                                    adp.addAdapter(name);
                                }
                                ((JSONObject) list).getJSONArray("dtjgList").forEach(e -> {
                                    TitleAdapter title = new TitleAdapter(requireContext());
                                    title.setTitle(((JSONObject) e).getString("tgmc"));
                                    adp.addAdapter(title);
                                    if (Objects.equals(((JSONObject) e).getString("tmlx"), "1")) {
                                        OptionAdapter optionAdapter = new OptionAdapter(requireContext());
                                        ((JSONObject) e).getJSONArray("tmxxlist").forEach(o -> optionAdapter.add((JSONObject) o));
                                        adp.addAdapter(optionAdapter);
                                    } else if (Objects.equals(((JSONObject) e).getString("tmlx"), "6")) {
                                        BlanketAdapter blanketAdapter = new BlanketAdapter(requireContext());
                                        adp.addAdapter(blanketAdapter);
                                    } else if (Objects.equals(((JSONObject) e).getString("tmlx"), "5")) {
                                        RankAdapter rankAdapter = new RankAdapter(requireContext());
                                        //((JSONObject) e).getJSONArray("tmxxlist").forEach(o -> optionAdapter.add((JSONObject) o));
                                        adp.addAdapter(rankAdapter);
                                    }
                                });
                            });
                        });
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

    public void getEvaluation(String rwid, String wjid, String sxz, String pjrdm, String bpdm, String kcdm, String rwh) {
        //System.out.println(List.of(rwid, wjid, sxz, pjrdm, bpdm, kcdm, rwh));
        new OkHttpClient.Builder().build().newCall(new Request.Builder().url(String.format("https://pjxt.sysu.edu.cn/evaluationPattern/getQuestionnaireTopic?rwid=%s&wjid=%s&sxz=%s&pjrdm=%s&bpdm=%s&kcdm=%s&rwh=%s", rwid, wjid, sxz, pjrdm, bpdm, kcdm, rwh))
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


}

class OptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<JSONObject> data = new ArrayList<>();
    int selected;

    public OptionAdapter(Context context) {
        super();
        this.context = context;
    }

    public void add(JSONObject item) {
        data.add(item);
        selected = getItemCount() - 1;
        notifyItemInserted(data.size() - 1);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(ItemOptionBinding.inflate(LayoutInflater.from(context), parent, false).getRoot()) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemOptionBinding binding = ItemOptionBinding.bind(holder.itemView);
        binding.getRoot().setOnClickListener(view -> {
            if (selected != position) {
                int old = selected;
                selected = position;
                notifyItemChanged(old);
                notifyItemChanged(selected);
            }
        });
        binding.option.setChecked(selected == position);
        binding.option.setText(data.get(position).getString("xxmc"));
        binding.getRoot().updateAppearance(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 6;
    }
}

class RankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;

    public RankAdapter(Context context) {
        super();
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Slider p = new Slider(context);
        p.setValue(100);
        p.setStepSize(1);
        p.setValueFrom(0);
        p.setValueTo(100);
        return new RecyclerView.ViewHolder(p) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }
}

class BlanketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;

    public BlanketAdapter(Context context) {
        super();
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(DialogEditTextBinding.inflate(LayoutInflater.from(context), parent, false).getRoot()) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DialogEditTextBinding binding = DialogEditTextBinding.bind(holder.itemView);
        binding.getRoot().setHint("请输入答案");
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 5;
    }
}