package io.github.hcisme.note.enums

import io.github.hcisme.note.R

enum class BottomBarEnum(
    val index: Int,
    val label: String,
    val resourceId: Int
) {
    Note(0, "便签", R.drawable.task),

    Statistic(1, "统计", R.drawable.statistic),

    User(2, "我的", R.drawable.user);

    companion object {
        fun getByIndex(type: Int): BottomBarEnum? = entries.find { it.index == type }
    }
}