package com.sysu.edu.academic;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.alibaba.fastjson2.JSONObject;
import com.sysu.edu.R;
import com.sysu.edu.databinding.CourseQueryBinding;

import java.util.Objects;

public class CourseQuery extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private CourseQueryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CourseQueryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationOnClickListener(v->supportFinishAfterTransition());
        binding.toolbar.getMenu().add(getString(R.string.reset)).setIcon(R.drawable.reset).setOnMenuItemClickListener(menuItem -> {
            recreate();
            return  false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        NavHostFragment nav = (NavHostFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_course_query));
        NavController navController = nav.getNavController();
        InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //appBarConfiguration = new AppBarConfiguration.Builder().build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        binding.fab.setOnClickListener(view -> {
            CourseQueryFragment fragment = (CourseQueryFragment) nav.getChildFragmentManager().getFragments().get(0);
            if (fragment != null) {
                setResult(RESULT_OK,getIntent().putExtra("filter",fragment.getMap()));
                supportFinishAfterTransition();
                JSONObject.from(fragment.getMap());
            }
        });
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_course_query);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}