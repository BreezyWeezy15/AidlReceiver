package com.app.lockcomposeAdmin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ShowAppListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowAppListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShowAppListViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}