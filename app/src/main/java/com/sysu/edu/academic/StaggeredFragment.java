package com.sysu.edu.academic;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sysu.edu.R;
import com.sysu.edu.api.Params;
import com.sysu.edu.databinding.CardItemBinding;
import com.sysu.edu.databinding.RecyclerViewBinding;
import com.sysu.edu.databinding.TwoColumnBinding;

import java.util.ArrayList;
import java.util.List;

public class StaggeredFragment extends Fragment {

    RecyclerViewBinding binding;
    Params params;
    int position;
    StaggeredAdapter staggeredAdapter;
    StaggeredGridLayoutManager lm;

    public static StaggeredFragment newInstance(int position){
        StaggeredFragment s=new StaggeredFragment();
        s.position=position;
        return s;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        params = new Params(requireActivity());
        binding = RecyclerViewBinding.inflate(inflater, container, false);
        lm  = new StaggeredGridLayoutManager(params.getColumn(), StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(lm);
        staggeredAdapter = staggeredAdapter==null?new StaggeredAdapter(requireContext()):staggeredAdapter;
        binding.recyclerView.setAdapter(staggeredAdapter);
        return binding.getRoot();
    }
    public void add(String title,List<String> keys,List<String> values){
        staggeredAdapter.add(title, keys, values);
    }
    public void add(Context context,String title,List<String> keys,List<String> values){
        if (staggeredAdapter==null) {
            staggeredAdapter = new StaggeredAdapter(context);
        }
        staggeredAdapter.add(title, keys, values);
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        lm.setSpanCount(params.getColumn());
    }
}
class StaggeredAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    ArrayList<String> titles=new ArrayList<>();
    ArrayList<List<String>> keys=new ArrayList<>();

    ArrayList<List<String>> values=new ArrayList<>();
    public StaggeredAdapter(Context c){
        super();
        context = c;
    }
    public void add(String title,List<String> keys,List<String> values){
        titles.add(title);
        this.keys.add(keys);
        this.values.add(values);
        notifyItemInserted(getItemCount());
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardItemBinding item = CardItemBinding.inflate(LayoutInflater.from(context), parent, false);
        RecyclerView list = RecyclerViewBinding.inflate(LayoutInflater.from(context), item.getRoot(), false).getRoot();
        list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        list.setNestedScrollingEnabled(false);
        list.setId(0);
        item.card.addView(list);
        return new RecyclerView.ViewHolder(item.getRoot()) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TextView)holder.itemView.findViewById(R.id.title)).setText(titles.get(position));
        ColumnAdp adp = new ColumnAdp(context, keys.get(position), values.get(position));
        ((RecyclerView)holder.itemView.findViewById(0)).setAdapter(adp);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}
class ColumnAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<String> value;
    Context context;
    List<String> key;
    public ColumnAdp(Context context,List<String> data,List<String> value){
        super();
        this.key = data;
        this.value = value;
        this.context=context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TwoColumnBinding b = TwoColumnBinding.inflate(LayoutInflater.from(context),parent,false);
        b.item.setOnClickListener(view -> {

        });
        return new RecyclerView.ViewHolder(b.getRoot()) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TextView)holder.itemView.findViewById(R.id.key)).setText(key.get(position));
        holder.itemView.setOnClickListener(v->{
            ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setPrimaryClip(ClipData.newPlainText(key.get(position),value.get(position)));
        });
        if (position< value.size()&&value.get(position)!=null) {
            ((TextView)holder.itemView.findViewById(R.id.value)).setText(value.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return key.size();
    }
}