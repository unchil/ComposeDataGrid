package com.unchil.composedatagrid

import kotlinx.serialization.Serializable

enum class PlatformAlias {
    ANDROID, IOS, JVM, WASM
}

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

fun makeGridColumns(type: PlatformAlias):List<String>{
    val columns = mutableListOf<String>()
    when(type){
        PlatformAlias.ANDROID, PlatformAlias.IOS -> {
            columns.add("수집시간")
            columns.add("관측지점")
            columns.add("수온")
        }
        PlatformAlias.JVM, PlatformAlias.WASM -> {
            columns.add("수집시간")
            columns.add("관측지점")
            columns.add("수온")
            columns.add("수소이온농도")
            columns.add("용존산소량")
            columns.add("탁도")
            columns.add("엽록소")
            columns.add("염분")
        }
    }
    return columns
}
fun SeaWaterInformation.toGridData(type: PlatformAlias):List<Any?>{
    val data = mutableListOf<Any?>()
    when(type){
        PlatformAlias.ANDROID, PlatformAlias.IOS -> {
            data.add(this.rtmWqWtchDtlDt)
            data.add(this.rtmWqWtchStaName)
            data.add(this.rtmWtchWtem)
        }
        PlatformAlias.JVM, PlatformAlias.WASM -> {
            data.add(this.rtmWqWtchDtlDt)
            data.add(this.rtmWqWtchStaName)
            data.add(this.rtmWtchWtem)
            data.add(this.ph)
            data.add(this.rtmWqDoxn)
            data.add(this.rtmWqTu)
            data.add(this.rtmWqChpla)
            data.add(this.rtmWqSlnty)
        }
    }

    return data
}
