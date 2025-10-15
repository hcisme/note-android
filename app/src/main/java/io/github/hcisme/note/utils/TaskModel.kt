package io.github.hcisme.note.utils

import io.github.hcisme.note.R

data class TaskModel(
    val title: String,
    val label: String,
    val face: Int,
    val teacher: String,
    val icon: Int? = null,
    val mannerIcon: Int,
    val manner: String,
    val startTime: String,
    val endTime: String
)

val tasks = listOf(
    TaskModel(
        title = "Physics",
        label = "Chapter 03: Force",
        face = R.drawable.ic_launcher_background,
        teacher = "Alex Jesus",
        icon = R.drawable.ic_launcher_background,
        mannerIcon = R.drawable.ic_launcher_background,
        manner = "Google Meet",
        startTime = "9:30",
        endTime = "10:20"
    ),
    TaskModel(
        title = "Geography",
        label = "Chapter 12: soil Profile",
        face = R.drawable.ic_launcher_background,
        teacher = "Jenifer Clark",
        icon = null,
        mannerIcon = R.drawable.ic_launcher_background,
        manner = "Zoom",
        startTime = "11:00",
        endTime = "11:50"
    ),
    TaskModel(
        title = "Assignment",
        label = "World Regional Pattern",
        face = R.drawable.ic_launcher_background,
        teacher = "Alexa Clark",
        icon = null,
        mannerIcon = R.drawable.ic_launcher_background,
        manner = "Google Docs",
        startTime = "12:20",
        endTime = "13:00"
    ),
)
