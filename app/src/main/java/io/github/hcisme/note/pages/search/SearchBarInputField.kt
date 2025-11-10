package io.github.hcisme.note.pages.search

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.keyboardHeightCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarInputField(drawerState: DrawerState) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val searchVM = viewModel<SearchViewModel>()
    val focusRequester = remember { FocusRequester() }
    val keyboardHeightDp = keyboardHeightCalculator()

    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    LaunchedEffect(keyboardHeightDp) {
        if (keyboardHeightDp == 0.dp) {
            focusManager.clearFocus()
        }
    }

    SearchBarDefaults.InputField(
        modifier = Modifier.focusRequester(focusRequester),
        query = searchVM.searchWord,
        onQueryChange = {
            searchVM.searchWord = it
        },
        onSearch = {
            keyboardController?.hide()
            searchVM.search(word = it)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索"
            )
        },
        trailingIcon = {
            if (searchVM.expanded) {
                Row {
                    IconButton(
                        onClick = {
                            searchVM.searchWord = ""
                            searchVM.expanded = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭"
                        )
                    }

                    IconButton(
                        onClick = {
                            scope.launch {
                                keyboardController?.hide()
                                drawerState.open()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "打开过滤条件抽屉"
                        )
                    }
                }
            }
        },
        expanded = searchVM.expanded,
        onExpandedChange = { searchVM.expanded = it },
        placeholder = { Text("点击输入内容进行搜索") }
    )
}