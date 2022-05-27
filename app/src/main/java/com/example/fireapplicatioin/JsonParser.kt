package com.example.fireapplicatioin

import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class JsonParser {
    fun parseJsonObject(obj: JSONObject): HashMap<String, String>{
        val dataList = HashMap<String, String>()
        try {
            val name = obj.getString("name")
            val latitude = obj.getJSONObject("geometry")
                .getJSONObject("location")
                .getString("lat")
            val longitude = obj.getJSONObject("geometry")
                .getJSONObject("location")
                .getString("lng")

            dataList.put("name", name)
            dataList.put("lat", latitude)
            dataList.put("lng", longitude)
        }catch(e: Exception){

        }

        return dataList
    }

    fun parseJsonArray(objects: JSONArray): List<HashMap<String, String>>{
        var dataList = ArrayList<HashMap<String, String>>()
        for (i in 0..objects.length()){
            try {
                val data = parseJsonObject(objects.get(i) as JSONObject)
                dataList.add(data)
            }catch (e: Exception){

            }
        }
        return dataList
    }

    fun parseResult(obj: JSONObject): List<HashMap<String, String>>{
        var jsonArray: JSONArray? = null
        try {
            jsonArray = obj.getJSONArray("results")
        }catch (e: Exception){

        }
        return parseJsonArray(jsonArray!!)
    }
}