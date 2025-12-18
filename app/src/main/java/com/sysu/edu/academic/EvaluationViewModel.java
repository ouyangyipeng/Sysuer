package com.sysu.edu.academic;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EvaluationViewModel extends ViewModel {
    MutableLiveData<String> category = new MutableLiveData<>();
    public EvaluationViewModel() {

    }
    public MutableLiveData<String> getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category.setValue(category);
    }
}
