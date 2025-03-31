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
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.sysu.edu.R;
import com.sysu.edu.databinding.NewsBinding;

public class News extends AppCompatActivity {
NewsBinding binding;
Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=NewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.pager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return new Fragment();
            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
        new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
            System.out.println(position);
            switch (position) {
                case 0:tab.setText("资讯");break;
                case 1:tab.setText("公众号");break;
                case 2:tab.setText("教务系统");break;
            };
        }).attach();
        handler=new Handler(getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {

                super.handleMessage(msg);
            }
        };
    }




}