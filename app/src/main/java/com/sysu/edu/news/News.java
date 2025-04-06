package com.sysu.edu.news;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.sysu.edu.R;
import com.sysu.edu.databinding.NewsBinding;

public class News extends AppCompatActivity {
NewsBinding binding;
String cookie;
Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=NewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cookie=getSharedPreferences("privacy",MODE_PRIVATE).getString("Cookie","");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        class Adapter extends FragmentStateAdapter{
          //  final List<NewFragment> pages = List.of(new NewFragment(cookie,0),new NewFragment(cookie,1));
            public Adapter(@NonNull FragmentActivity fragmentActivity) {
                super(fragmentActivity);
            }
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return new NewFragment(cookie,position);
            }
            @Override
            public int getItemCount() {
                return 4;
            }
        }
        Adapter adapter = new Adapter(this);
        binding.pager.setAdapter(adapter);
        //adapter.getItem(0).getSubscription();
        new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> tab.setText(new String[]{"资讯","公众号","通知","今日中大"}[position])).attach();
        handler=new Handler(getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };
    }




}

