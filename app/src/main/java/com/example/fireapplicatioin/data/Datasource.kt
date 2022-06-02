package com.example.fireapplicatioin.data

import com.example.fireapplicatioin.R
import com.example.fireapplicatioin.model.NewsFlash
import com.example.fireapplicatioin.model.Volunteer
import com.google.android.gms.maps.model.LatLng
import java.util.ArrayList

class Datasource {
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
    fun loadVolunteers(): ArrayList<Volunteer>{
        val volunteers: ArrayList<Volunteer> = ArrayList()
        volunteers.add(Volunteer(LatLng(55.048172769399466, 60.21560657855611)))
        volunteers.add(Volunteer(LatLng(55.23640775661726, 59.89580368562201)))
        volunteers.add(Volunteer(LatLng(55.20853984720401, 60.36157804360129)))
        volunteers.add(Volunteer(LatLng(55.33988927216496, 61.61622289229776)))
        volunteers.add(Volunteer(LatLng(56.59919818644739, 60.65735176185012)))
        volunteers.add(Volunteer(LatLng(56.13113522419258, 60.29349500987032)))
        volunteers.add(Volunteer(LatLng(55.45635031601465, 58.58972350241588)))
        volunteers.add(Volunteer(LatLng(56.186175974460666, 54.7183982923442)))
        return volunteers
    }
}