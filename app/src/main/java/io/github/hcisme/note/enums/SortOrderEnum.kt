package io.github.hcisme.note.enums

import io.github.hcisme.note.R

enum class SortOrderEnum(
    val code: String,
    val message: String,
    val resourceId: Int,
    val status: Int
) {
    ASC("ASC", "正序", R.drawable.asc, 0),
    DESC("DESC", "倒序", R.drawable.desc, 1);

    val not: SortOrderEnum
        get() = when (this) {
            ASC -> DESC
            DESC -> ASC
        }
}
