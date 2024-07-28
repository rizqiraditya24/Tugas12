package com.example.database.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FriendViewModel(private val friendDao: FriendDao) : ViewModel() {

    val name = MutableLiveData<String>()
    val school = MutableLiveData<String>()
    val hobby = MutableLiveData<String>()

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getFriend() = friendDao.getAll()

    suspend fun insertFriend(data: Friend) {
        friendDao.insert(data)
    }

    fun deleteAll() {
        coroutineScope.launch {
            friendDao.getAll().first().forEach { friend ->
                friendDao.delete(friend)
            }
        }
    }

    fun updateFriend(friend: Friend) {
        coroutineScope.launch {
            friendDao.update(friend)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
