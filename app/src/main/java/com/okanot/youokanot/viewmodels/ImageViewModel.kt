package com.okanot.youokanot.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.okanot.youokanot.woundtreatmentscreen.Treatment

class ImageViewModel : ViewModel() {

    private var _image: MutableLiveData<Bitmap> = MutableLiveData()
    val image: LiveData<Bitmap> get() = _image

    fun updateImage(image: Bitmap) {
        this._image.value = image
    }

    private var _treatment: MutableLiveData<Treatment> = MutableLiveData()
    val treatment: LiveData<Treatment> get() = _treatment

    fun setTreatment(treatment: Treatment) {
        _treatment.value = treatment
    }

}