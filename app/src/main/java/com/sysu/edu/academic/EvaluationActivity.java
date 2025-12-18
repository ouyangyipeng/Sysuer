package com.sysu.edu.academic;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.sysu.edu.R;
import com.sysu.edu.databinding.ActivityEvaluationBinding;

import java.util.Objects;

public class EvaluationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEvaluationBinding binding = ActivityEvaluationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationOnClickListener(v -> supportFinishAfterTransition());
        ((NavHostFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragment))).getNavController();
    }
}
