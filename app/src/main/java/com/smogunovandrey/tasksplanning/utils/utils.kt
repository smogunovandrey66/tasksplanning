package com.smogunovandrey.tasksplanning.utils

import com.google.gson.Gson

fun <T> MutableList<T>.swap(index1: Int, index2: Int){
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

//fun Any.deepCopy():Any {
//    val JSON = Gson().toJson(this)
//    return Gson().fromJson(JSON, Any::class.java)
//}