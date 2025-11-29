package com.unchil.composedatagrid
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow

class Repository {
    private val api = LocalRestApi()
    val _seaWaterInfoMof: MutableStateFlow<List<SeaWaterInformation>>
            = MutableStateFlow(emptyList())

    val _seaWaterInfoNifs: MutableStateFlow<List<SeaWaterInformationNifs>>
            = MutableStateFlow(emptyList())
    suspend fun getSeaWaterInfo(url:String){
        api.get<List<SeaWaterInformation>>(url)?.let { it ->
            _seaWaterInfoMof.value = it
            println("[${getPlatform().name}][${getPlatform().alias.name}] ReceiveCount[${it.count()}]")
        }
    }

    suspend fun getSeaWaterInfoNifs(url:String) {
        api.get<List<SeaWaterInformationNifs>>(url)?.let { it ->
            _seaWaterInfoNifs.value = it
            println("[${getPlatform().name}][${getPlatform().alias.name}] ReceiveCount[${it.count()}]")
        }
    }



}