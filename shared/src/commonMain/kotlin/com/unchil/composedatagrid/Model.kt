package com.unchil.composedatagrid

import kotlinx.serialization.Serializable

enum class PlatformAlias {
    ANDROID, IOS, JVM, WASM
}

@Serializable
data class SeaWaterInformationNifs(
    val sta_cde: String,
    val sta_nam_kor: String,
    val obs_datetime: String,
    val obs_lay: String,
    val wtr_tmp: String,
    val gru_nam: String,
    val lon: Double,
    val lat: Double,
)

fun SeaWaterInformationNifs.makeGridColumns():List<String>{
    val columns = mutableListOf<String>()
    columns.add("수집시간")
    columns.add("해역")
    columns.add("관측소코드")
    columns.add("관측소명")
    columns.add("관측층")
    columns.add("수온")
    columns.add("경도")
    columns.add("위도")

    return columns
}

fun SeaWaterInformationNifs.toGridData():List<Any?>{
    val data = mutableListOf<Any?>()
    data.add(this.obs_datetime)
    data.add(this.gru_nam)
    data.add(this.sta_cde)
    data.add(this.sta_nam_kor)
    data.add(when(this.obs_lay){
        "1" -> "표층"
        "2" -> "중층"
        "3" -> "심층"
        else -> ""
    })
    data.add(this.wtr_tmp)
    data.add(this.lon)
    data.add(this.lat)
    return data
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



fun SeaWaterInformation.makeGridColumns():List<String>{
    val columns = mutableListOf<String>()
    columns.add("수집시간")
    columns.add("관측지점")
    columns.add("수온")
    columns.add("수소이온농도")
    columns.add("용존산소량")
    columns.add("탁도")
    columns.add("엽록소")
    columns.add("염분")

    return columns
}

fun SeaWaterInformation.toGridData():List<Any?>{
    val data = mutableListOf<Any?>()

    data.add(this.rtmWqWtchDtlDt)
    data.add(this.rtmWqWtchStaName)
    data.add(this.rtmWtchWtem)
    data.add(this.ph)
    data.add(this.rtmWqDoxn)
    data.add(this.rtmWqTu)
    data.add(this.rtmWqChpla)
    data.add(this.rtmWqSlnty)

    return data
}
