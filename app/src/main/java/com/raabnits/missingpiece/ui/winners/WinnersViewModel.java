package com.raabnits.missingpiece.ui.winners;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WinnersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WinnersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is winners fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}