package com.sysu.edu;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.sysu.edu.api.Params;
import com.sysu.edu.databinding.ActivityMainBinding;
import com.sysu.edu.preference.Language;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Handler handler;
    long downloadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavController navController = ((NavHostFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.dashboard_scroll))).getNavController();
        NavGraph g = new NavInflater(this,navController.getNavigatorProvider()).inflate(R.navigation.main_navigation);
        if(savedInstanceState==null){
            g.setStartDestination(new int[]{R.id.navigation_activity,R.id.navigation_service,R.id.navigation_account}[Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("home","0"))]);
        }
        navController.setGraph(g);
        NavigationUI.setupWithNavController((NavigationBarView) binding.navView, navController);
        Language.setLanguage(this);
        checkUpdate();
        Params params = new Params(this);
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                System.out.println(msg.obj);
                if(msg.what==-1){
                    params.toast(R.string.no_wifi_warning);
                }else{
                    JSONObject response = JSONObject.parseObject((String) msg.obj);
                    try {
                        int version = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                        if(version<response.getInteger("version")){
                            new MaterialAlertDialogBuilder(MainActivity.this).setMessage(response.getString("description")).setTitle("发现新版本").setPositiveButton("更新", (dialogInterface, i) -> {
                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(response.getString("link")));
                                request.setTitle("文件下载");
                                request.setDescription("正在下载文件...");
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationUri(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sysuer.apk")));
                                downloadId = downloadManager.enqueue(request);
                                DownloadManager.Query query = new DownloadManager.Query();
                                query.setFilterById(downloadId);
                                Cursor cursor = downloadManager.query(query);
                                if (cursor.moveToFirst()) {
                                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                        String filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                        System.out.println("文件路径: " + filePath);
                                    }
                                }
                                cursor.close();
                            }).setNegativeButton("取消", (dialogInterface, i) -> {
                            }).setCancelable(response.getBoolean("enforce")).create().show();
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (id == downloadId) {
                        params.toast("完成下载");
                    }
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),Context.RECEIVER_EXPORTED);
        }else{
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
        //WorkManager.getInstance(this).enqueue(new OneTimeWorkRequest.Builder(ClassIsland.class).build());
    }
    void checkUpdate(){
        new OkHttpClient.Builder().build().newCall(new Request.Builder().url("https://sysu-tang.github.io/latest.json").build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Message msg = new Message();
                msg.what = 0;
                msg.obj=response.body().string();
                handler.sendMessage(msg);
            }
        });
    }
}