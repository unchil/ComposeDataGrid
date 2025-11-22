package com.unchil.composedatagrid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unchil.composedatagrid.Repository
import com.unchil.composedatagrid.SeaWaterInformation
import com.unchil.composedatagrid.getPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MofSeaWaterInfoViewModel: ViewModel() {
    private val repository = Repository()

    val _seaWaterInfo: MutableStateFlow<List<SeaWaterInformation>>
            = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            repository._seaWaterInfoMof.collectLatest {
                _seaWaterInfo.value = it
            }
        }
    }

    suspend fun onEvent(event: Event) {
        when (event) {
            is Event.Refresh -> {
                getSeaWaterInfo()
            }
        }
    }

    suspend fun getSeaWaterInfo(){
        val endPoint = "http://${if( getPlatform().name.contains("Android") ) "10.0.2.2" else "localhost"}:7788"
        repository.getSeaWaterInfo("${endPoint}/mof/swi/mof_oneday")
    }


    sealed class Event {
        object Refresh : Event()
    }


}