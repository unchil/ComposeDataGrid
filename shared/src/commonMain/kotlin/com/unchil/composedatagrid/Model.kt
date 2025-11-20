package com.unchil.composedatagrid

import kotlinx.serialization.Serializable

@Serializable
data class SeaWaterInformation(
    val rtmWqWtchDtlDt:String,
    val rtmWqWtchStaCd:String,
    val rtmWqWtchStaName:String,
    val rtmWtchWtem:String,
    val rtmWqCndctv:String,
    val ph:String,
    val rtmWqDoxn:String,
    val rtmWqTu:String,
    val rtmWqChpla:String,
    val rtmWqSlnty:String,
    val lon: Double,
    val lat: Double,
)

fun SeaWaterInformation.toList(): List<Any?> {
    val convertList = mutableListOf<Any?>()
    convertList.add(this.rtmWqWtchDtlDt)
    convertList.add(this.rtmWqWtchStaCd)
    convertList.add(this.rtmWqWtchStaName)
    convertList.add(this.rtmWtchWtem)
    convertList.add(this.rtmWqCndctv)
    convertList.add(this.ph)
    convertList.add(this.rtmWqDoxn)
    convertList.add(this.rtmWqTu)
    convertList.add(this.rtmWqChpla)
    convertList.add(this.rtmWqSlnty)

    return convertList
}