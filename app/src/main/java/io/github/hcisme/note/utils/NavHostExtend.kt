package io.github.hcisme.note.utils

import androidx.navigation.NavHostController
import io.github.hcisme.note.constants.NavigationName.TODO_FORM_PAGE

/**
 * 导航到 编辑 TodoItem 界面
 */
fun NavHostController.navigateToTodoForm(id: Long? = null) {
    val route = if (id != null) {
        "$TODO_FORM_PAGE?id=$id"
    } else {
        TODO_FORM_PAGE
    }
    navigate(route)
}
