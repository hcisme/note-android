package io.github.hcisme.note.navigation

import androidx.navigation.NavHostController

/**
 * 导航到 编辑 TodoItemFormData 界面
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
 * 导航到 编辑 TodoItemFormData 界面
 */
fun NavHostController.navigateToHomeAndClearStack() {
    navigate(NavigationName.HOME_PAGE) {
        popUpTo(NavigationName.LOGIN_PAGE) {
            inclusive = true
        }
    }
}

/**
 * 导航到 编辑 TodoItemFormData 界面
 */
fun NavHostController.navigateToHome() {
    navigate(NavigationName.HOME_PAGE)
}

/**
 * 导航到 编辑 TodoItemFormData 界面
 */
fun NavHostController.navigateToSetting() {
    navigate(NavigationName.SETTING_PAGE)
}

/**
 * 退出登录
 */
fun NavHostController.navigateToLoginAndClearStack() {
    navigate(NavigationName.LOGIN_PAGE) {
        popUpTo(NavigationName.HOME_PAGE) {
            inclusive = true
        }
    }
}

/**
 * 导航到 编辑 Search 界面
 */
fun NavHostController.navigateToSearch() {
    navigate(NavigationName.SEARCH_PAGE)
}
