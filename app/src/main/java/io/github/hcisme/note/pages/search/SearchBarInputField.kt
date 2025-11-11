package io.github.hcisme.note.pages.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.keyboardHeightCalculator
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.noRippleClickable
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarInputField() {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val navController = LocalNavController.current
    val searchVM = viewModel<SearchViewModel>()
    val focusRequester = remember { FocusRequester() }
    val keyboardHeightDp = keyboardHeightCalculator()

    LaunchedEffect(Unit) {
        delay(80)
        focusRequester.requestFocus()
    }

    LaunchedEffect(keyboardHeightDp) {
        if (keyboardHeightDp == 0.dp) {
            focusManager.clearFocus()
        }
    }

    fun removeFocusAndKeyBoard() {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    CenterAlignedTopAppBar(
        modifier = Modifier.focusRequester(focusRequester),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.inverseOnSurface, CircleShape)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索"
                )

                BasicTextField(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Thin
                    ),
                    singleLine = true,
                    value = searchVM.searchWord,
                    onValueChange = { searchVM.searchWord = it },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            searchVM.search()
                        }
                    ),
                    decorationBox = @Composable { innerTextField ->
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .fillMaxSize()
                                .padding(horizontal = 4.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            innerTextField()
                        }
                    }
                )

                if (searchVM.searchWord.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .noRippleClickable { searchVM.searchWord = "" }
                            .padding(4.dp)
                    )
                }
            }

        },
        navigationIcon = {
            IconButton(
                onClick = {
                    removeFocusAndKeyBoard()
                    navController.popBackStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回"
                )
            }
        }
    )


    BackHandler(keyboardHeightDp != 0.dp) {
        removeFocusAndKeyBoard()
    }
}