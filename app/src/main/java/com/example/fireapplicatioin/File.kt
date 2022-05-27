package com.example.fireapplicatioin

import kotlinx.coroutines.runBlocking

private lateinit var listResult: List<Fire>

fun main() = runBlocking{
    try {
        listResult = Api.retrofitService.getProperties()
        print(listResult)
    } catch (e: Exception) {
        print("Failure: ${e.message}")
    }
}