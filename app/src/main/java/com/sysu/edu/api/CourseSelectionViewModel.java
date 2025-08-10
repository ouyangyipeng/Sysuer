package com.sysu.edu.api;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class CourseSelectionViewModel extends ViewModel {
    private String returnData;
    private HashMap<String,String> filter;

    public HashMap<String, String> getFilter() {
        if(filter==null){
            filter=new HashMap<>();
        }
        return filter;
    }

    public void setFilter(HashMap<String, String> filter) {
        this.filter = filter;
    }

    public String getReturnData() {
        return returnData;
    }

    public void setReturnData(String data) {
        returnData = data;
    }

    // 清空数据，避免重复处理
    public void clearReturnData() {
        returnData = null;
    }

}

