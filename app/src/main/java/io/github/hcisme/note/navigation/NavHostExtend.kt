package io.github.hcisme.note.navigation

import androidx.navigation.NavHostController
import io.github.hcisme.note.navigation.NavigationName

/**
 * 导航到 编辑 TodoItem 界面
 */
fun NavHostController.navigateToTodoForm(id: Long? = null) {
    val route = if (id != null) {
        "${NavigationName.TODO_FORM_PAGE}?id=$id"
    } else {
        NavigationName.TODO_FORM_PAGE
    }
    navigate(route)
}

/**
 * 导航到 编辑 TodoItem 界面
 */
fun NavHostController.navigateToHomeAndClearStack() {
    navigate(NavigationName.HOME_PAGE) {
        popUpTo(NavigationName.LOGIN_PAGE) {
            inclusive = true
        }
    }
}

/**
 * 导航到 编辑 TodoItem 界面
 */
fun NavHostController.navigateToHome() {
    navigate(NavigationName.HOME_PAGE)
}
