package com.sistemaestudos.model

import android.graphics.Bitmap

data class Weather (
    val date: String,
    val desc: String,
    val temp: String,
    val imgUrl: String,
    var bitmap: Bitmap? = null
) {
    companion object {
        val LOADING = Weather(
            date = "LOADING",
            desc = "LOADING",
            temp = "--",
            imgUrl = "LOADING",
            bitmap = null
        )
    }
}