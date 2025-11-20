package com.unchil.composedatagrid

import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.flow.MutableStateFlow

class Repository {
    internal val LOGGER = KtorSimpleLogger( "Repository" )

    private val api = LocalRestApi()

    val _seaWaterInfoMof: MutableStateFlow<List<SeaWaterInformation>>
            = MutableStateFlow(emptyList())

    suspend fun getSeaWaterInfo(url:String){
        api.get<List<SeaWaterInformation>>(url)?.let { it ->
            _seaWaterInfoMof.value = it
            LOGGER.debug("getSeaWaterInfo() called[${it.count()}]")
        }
    }
}