package com.okanot.youokanot.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel : ViewModel() {

    private var _image: MutableLiveData<Bitmap> = MutableLiveData()
    val image: LiveData<Bitmap> get() = _image

    fun updateImage(image: Bitmap) {
        this._image.value = image
    }


}