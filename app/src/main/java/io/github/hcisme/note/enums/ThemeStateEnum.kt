package io.github.hcisme.note.enums

enum class ThemeStateEnum(val themeMode: Int) {
    Light(ThemeMode.LIGHT),
    Dark(ThemeMode.DARK),
    System(ThemeMode.SYSTEM);

    /**
     * Light -> Dark -> System -> Light -> ...
     */
    fun next(): ThemeStateEnum {
        return when (this) {
            Light -> Dark
            Dark -> System
            System -> Light
        }
    }

    companion object {
        fun fromMode(mode: Int): ThemeStateEnum? {
            return when (mode) {
                ThemeMode.LIGHT -> Light
                ThemeMode.DARK -> Dark
                ThemeMode.SYSTEM -> System
                else -> null
            }
        }
    }

    object ThemeMode {
        const val LIGHT = 0
        const val DARK = 1
        const val SYSTEM = 2
    }
}
