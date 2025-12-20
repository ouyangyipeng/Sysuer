package com.sysu.edu.academic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.sysu.edu.R;
import com.sysu.edu.api.Params;
import com.sysu.edu.databinding.DialogEditTextBinding;
import com.sysu.edu.databinding.FragmentQuestionnaireBinding;
import com.sysu.edu.databinding.ItemOptionBinding;
import com.sysu.edu.extra.LoginActivity;
import com.sysu.edu.todo.info.TitleAdapter;

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


public class EvaluationQuestionnaireFragment extends Fragment {
    Params params;
    Handler handler;
    ActivityResultLauncher<Intent> launch;
    JSONObject answers = JSONObject.parseObject("{\"pjidlist\":[],\"pjjglist\":[],\"pjzt\": \"2\"}");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentQuestionnaireBinding binding = FragmentQuestionnaireBinding.inflate(inflater, container, false);
        params = new Params(requireActivity());
        //StaggeredGridLayoutManager sgm = new StaggeredGridLayoutManager(params.getColumn(), 1);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ConcatAdapter adp = new ConcatAdapter(new ConcatAdapter.Config.Builder().setIsolateViewTypes(true).build());
        binding.recyclerView.setAdapter(adp);
        launch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if (o.getResultCode() == Activity.RESULT_OK) {
                getEvaluation(requireArguments().getString("rwid"),
                        requireArguments().getString("wjid"),
                        requireArguments().getString("sxz"),
                        requireArguments().getString("pjrdm"),
                        requireArguments().getString("bpdm"),
                        requireArguments().getString("kcdm"),
                        requireArguments().getString("rwh"),
                        Objects.equals(requireArguments().getString("lsjgzt"), "2") ? "1" : "",
                        requireArguments().getString("bpmc"));
            }
        });
        //{"rwid", "wjid","sxz","pjrdm","bpdm","kcdm","rwh"};
        getEvaluation(requireArguments().getString("rwid"),
                requireArguments().getString("wjid"),
                requireArguments().getString("sxz"),
                requireArguments().getString("pjrdm"),
                requireArguments().getString("bpdm"),
                requireArguments().getString("kcdm"),
                requireArguments().getString("rwh"),
                Objects.equals(requireArguments().getString("lsjgzt"), "2") ? "1" : "",
                requireArguments().getString("bpmc"));
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    JSONObject data = JSON.parseObject((String) msg.obj);
                    if (data.get("code").equals("200")) {
                        data.getJSONObject("result").getJSONArray("assessedObjList").forEach(l ->
                        {
                            ((JSONObject) l).getJSONArray("bpdxList").forEach(list ->
                            {
                                JSONObject pjjglist = ((JSONObject) list).clone();
                                pjjglist.remove("dtjgList");
                                answers.getJSONArray("pjjglist").add(pjjglist);
                                TitleAdapter name = new TitleAdapter(requireContext());
                                String bprmc = ((JSONObject) list).getString("bprmc");
                                if (bprmc != null && !bprmc.isEmpty()) {
                                    name.setTitle(bprmc);
                                    name.setHeader(1);
                                    adp.addAdapter(name);
                                }// 被评名称
                                ((JSONObject) list).getJSONArray("dtjgList").forEach(e ->
                                {
                                    String pjxxlistString = String.format(
                                            "{\"sjly\": \"1\",\"stlx\": \"1\",\"wjid\": \"%s\",\"wjssrwid\": \"%s\",\"wjstctid\": \"\",\"wjstid\": \"%s\",\"xxdalist\": []}",
                                            ((JSONObject) list).getString("wjid"),
                                            ((JSONObject) list).getString("wjssrwid"),
                                            ((JSONObject) e).getString("tmid")
                                    );
                                    JSONObject pjxxlist = JSONObject.parse(pjxxlistString);
                                    JSONArray tmxxda = ((JSONObject) e).getJSONArray("tmxxda");
                                    pjxxlist.put("xxdalist", ((JSONObject) e).getJSONArray("tmxxda"));
                                    answers.getJSONArray("pjjglist").getJSONObject(answers.getJSONArray("pjjglist").size() - 1).getJSONArray("pjxxlist").add(pjxxlist);

                                    JSONArray da = pjxxlist.getJSONArray("xxdalist");
                                    TitleAdapter title = new TitleAdapter(requireContext());
                                    title.setTitle(((JSONObject) e).getString("tgmc"));
                                    adp.addAdapter(title); // 题目标题

                                    if (Objects.equals(((JSONObject) e).getString("tmlx"), "1")) {
                                        OptionAdapter optionAdapter = new OptionAdapter(requireContext());
                                        if (!tmxxda.isEmpty()) {
                                            optionAdapter.setOption(tmxxda.getString(0));
                                        }
                                        ((JSONObject) e).getJSONArray("tmxxlist").forEach(o -> optionAdapter.add((JSONObject) o));
                                        optionAdapter.updateAnswer(da);
                                        adp.addAdapter(optionAdapter);
                                    } else if (Objects.equals(((JSONObject) e).getString("tmlx"), "6")) {
                                        BlanketAdapter blanketAdapter = new BlanketAdapter(requireContext());
                                        if (!tmxxda.isEmpty()) {
                                            blanketAdapter.setText(tmxxda.getString(0));
                                        }
                                        blanketAdapter.updateAnswer(da);
                                        adp.addAdapter(blanketAdapter);
                                    } else if (Objects.equals(((JSONObject) e).getString("tmlx"), "5")) {
                                        RankAdapter rankAdapter = new RankAdapter(requireContext());
                                        rankAdapter.updateAnswer(da);
                                        if (!tmxxda.isEmpty()) {
                                            rankAdapter.setRank(Integer.parseInt(tmxxda.getString(0)));
                                        }
                                        //((JSONObject) e).getJSONArray("tmxxlist").forEach(o -> optionAdapter.add((JSONObject) o));
                                        adp.addAdapter(rankAdapter);
                                    }
                                });
                            });
                        });
                    } else {
                        launch.launch(new Intent(requireContext(), LoginActivity.class));
                    }
                } else if (msg.what==2) {
                    System.out.println(answers);
                    JSONObject data = JSON.parseObject((String) msg.obj);
                    if (data.get("code").equals("200")) {
                        params.toast(R.string.save_successfully);
                    } else {
                        params.toast(String.format("%s：%s", getString(R.string.save_fail), data.getString("msg")));
                    }
                }else if (msg.what==3) {
                    JSONObject data = JSON.parseObject((String) msg.obj);
                    if (data.get("code").equals("200")) {
                        params.toast(R.string.submit_successfully);
                    } else {
                        params.toast(String.format("%s：%s", getString(R.string.submit_fail), data.getString("msg")));
                    }
                }
                else if (msg.what == -1) {
                    params.toast(R.string.no_wifi_warning);
                    Snackbar.make(binding.getRoot(), "去登录", Snackbar.LENGTH_LONG).setAction("登录", v -> launch.launch(new Intent(requireContext(), LoginActivity.class).putExtra("url", "https://pjxt.sysu.edu.cn"))).show();
                }
            }
        };
        binding.save.setOnClickListener(view -> {
            saveEvaluation();
        });
        binding.submit.setOnClickListener(view -> Snackbar.make(binding.getRoot(), "提交后不可更改", Snackbar.LENGTH_LONG).setAction(R.string.confirm, v -> submitEvaluation()).show());
        return binding.getRoot();
    }

    public void getEvaluation(String rwid, String wjid, String sxz, String pjrdm, String bpdm, String kcdm, String rwh,String pjzt,String bpmc) {
        new OkHttpClient.Builder().build().newCall(new Request.Builder().url(String.format("https://pjxt.sysu.edu.cn/evaluationPattern/getQuestionnaireTopic?rwid=%s&wjid=%s&sxz=%s&pjrdm=%s&bpdm=%s&kcdm=%s&rwh=%s&pjzt=%s&bpmc=%s", rwid, wjid, sxz, pjrdm, bpdm, kcdm, rwh,pjzt,bpmc))
                .header("Cookie", params.getCookie())
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

    public void saveEvaluation() {
        postEvaluation("2",2);
    }
    public void submitEvaluation() {
        postEvaluation("1",3);
    }
    public void postEvaluation(String mode,int what) {
        answers.put("pjzt", mode);
        new OkHttpClient.Builder().build().newCall(new Request.Builder().url("https://pjxt.sysu.edu.cn/evaluationPattern/submitSaveEvaluation")
                .header("Cookie", params.getCookie())
                .post(RequestBody.create(answers.toString(), MediaType.parse("application/json")))
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
                msg.what = what;
                msg.obj = response.body().string();
                handler.sendMessage(msg);
            }
        });
    }
}

class OptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<JSONObject> data = new ArrayList<>();
    int selected = -1;
    String option;
    JSONArray answer;

    public OptionAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void updateAnswer(JSONArray answers) {
        this.answer = answers;
    }

    public void add(JSONObject item) {
        data.add(item);
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
        int pos = holder.getBindingAdapterPosition();
        ItemOptionBinding binding = ItemOptionBinding.bind(holder.itemView);
        binding.getRoot().setOnClickListener(view -> {
            if (selected != pos) {
                int old = selected;
                selected = pos;
                answer.set(0, data.get(pos).getString("tmxxid"));
                notifyItemChanged(old);
                notifyItemChanged(selected);
            }
        });
        if (selected == -1 && Objects.equals(data.get(pos).getString("tmxxid"), option)) {
            selected = pos;
        }
        binding.option.setChecked(selected == pos);
        binding.option.setText(data.get(pos).getString("xxmc"));
        binding.getRoot().updateAppearance(pos, getItemCount());
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
    int rank;

    JSONArray answer;

    public RankAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void updateAnswer(JSONArray answers) {
        this.answer = answers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Slider p = new Slider(context);
        p.setValue(rank == 0 ? 100 : rank);
        p.setStepSize(1);
        p.setValueFrom(0);
        p.setValueTo(100);
        p.setLabelBehavior(LabelFormatter.LABEL_FLOATING);
        p.addOnChangeListener((slider, value, fromUser) -> {
            answer.set(0, String.valueOf((int) value));
        });
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
    String content;

    JSONArray answer;

    public BlanketAdapter(Context context) {
        super();
        this.context = context;
    }

    public void updateAnswer(JSONArray answers) {
        this.answer = answers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(DialogEditTextBinding.inflate(LayoutInflater.from(context), parent, false).getRoot()) {
        };
    }

    public void setText(String text) {
        this.content = text;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DialogEditTextBinding binding = DialogEditTextBinding.bind(holder.itemView);
        binding.getRoot().setHint(R.string.please_enter_content);
        if (content != null && !content.isEmpty()) {
            answer.set(0, content);
            binding.edit.setText(content);
        }
        binding.edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.isEmpty()) {
                    if (!answer.isEmpty()) {
                        answer.remove(0);
                    }
                } else {
                    answer.set(0, text);
                }
            }
        });
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