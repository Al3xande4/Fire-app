package com.example.fireapplicatioin.data

import com.example.fireapplicatioin.R
import com.example.fireapplicatioin.model.FirePoint
import com.google.android.gms.maps.model.LatLng
import java.util.ArrayList

class Datasource {
    fun loadFirePoints(): ArrayList<FirePoint>{
        val firePoints: ArrayList<FirePoint> = ArrayList()
        firePoints.add(FirePoint(LatLng(55.148172769399466, 60.21560657855611), 10000f))
        firePoints.add(FirePoint(LatLng(55.23640775661726, 59.99580368562201), 10000f))
        firePoints.add(FirePoint(LatLng(55.30853984720401, 60.36157804360129), 10000f))
        firePoints.add(FirePoint(LatLng(55.33988927216496, 61.71622289229776), 10000f))
        firePoints.add(FirePoint(LatLng(56.69919818644739, 60.75735176185012), 10000f))
        firePoints.add(FirePoint(LatLng(56.03113522419258, 60.19349500987032), 10000f))
        firePoints.add(FirePoint(LatLng(55.45635031601465, 58.48972350241588), 10000f))
        firePoints.add(FirePoint(LatLng(56.086175974460666, 54.7183982923442), 10000f))
        return firePoints
    }

    fun loadNewsFlashes(): ArrayList<NewsFlash>{
        val newsFlashes: ArrayList<NewsFlash> = ArrayList()
        newsFlashes.add(NewsFlash(R.drawable.img_1, R.string.fire_title_1))
        newsFlashes.add(NewsFlash(R.drawable.img_2, R.string.fire_title_2))
        newsFlashes.add(NewsFlash(R.drawable.img_3, R.string.fire_title_3))
        newsFlashes.add(NewsFlash(R.drawable.img_4, R.string.fire_title_4))
        newsFlashes.add(NewsFlash(R.drawable.img_5, R.string.fire_title_5))
        newsFlashes.add(NewsFlash(R.drawable.img_6, R.string.fire_title_6))
        newsFlashes.add(NewsFlash(R.drawable.img_7, R.string.fire_title_7))
        newsFlashes.add(NewsFlash(R.drawable.img_8, R.string.fire_title_8))
        return  newsFlashes
    }
}