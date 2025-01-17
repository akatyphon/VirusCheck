package com.madinaappstudio.viruscheck.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.madinaappstudio.viruscheck.database.HistoryDao
import com.madinaappstudio.viruscheck.database.HistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(private val historyDao: HistoryDao) : ViewModel() {

    fun insertHistory(historyEntity: HistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.insertHistory(historyEntity)
        }
    }

    fun deleteHistory(historyEntity: HistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.deleteHistory(historyEntity)
        }
    }

    fun getAllHistory(): LiveData<List<HistoryEntity>> {
        val liveDataHistory = MutableLiveData<List<HistoryEntity>>()
        viewModelScope.launch(Dispatchers.IO) {
            val historyList = historyDao.getAllHistory()
            liveDataHistory.postValue(historyList)
        }
        return liveDataHistory
    }

    fun deleteAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.deleteAllHistory()
        }
    }

    class HistoryViewModelFactory(private val historyDao: HistoryDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HistoryViewModel(historyDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
