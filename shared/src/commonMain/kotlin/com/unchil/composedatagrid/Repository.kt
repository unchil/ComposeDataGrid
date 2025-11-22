package com.unchil.composedatagrid
import kotlinx.coroutines.flow.MutableStateFlow

class Repository {
    private val api = LocalRestApi()
    val _seaWaterInfoMof: MutableStateFlow<List<SeaWaterInformation>>
            = MutableStateFlow(emptyList())
    suspend fun getSeaWaterInfo(url:String){
        api.get<List<SeaWaterInformation>>(url)?.let { it ->
            _seaWaterInfoMof.value = it
            println("[${getPlatform().name}][${::getSeaWaterInfo::class.simpleName}] ReceiveCount[${it.count()}]")
        }
    }

}