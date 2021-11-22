package com.example.paasdcmfirebasehotel.ui.mihotel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MiHotelViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MiHotelViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is mihotel fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}