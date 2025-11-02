package io.github.hcisme.note.pages.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.hcisme.note.enums.BottomBarEnum

class HomeViewModel : ViewModel() {
    var currentPageEnum by mutableStateOf(BottomBarEnum.Note)

    fun changePage(pageEnum: BottomBarEnum) {
        currentPageEnum = pageEnum
    }
}