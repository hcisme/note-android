package io.github.hcisme.note.pages.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    var currentPage by mutableIntStateOf(0)

    fun changePage(page: Int) {
        currentPage = page
    }
}