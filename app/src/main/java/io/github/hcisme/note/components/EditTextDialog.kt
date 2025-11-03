package io.github.hcisme.note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.hcisme.note.enums.FormFieldEnum
import io.github.hcisme.note.utils.noRippleClickable

@Composable
fun EditTextDialog(
    visible: Boolean,
    formFieldEnum: FormFieldEnum,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val view = LocalView.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val keyboardHeightDp = keyboardHeightCalculator()
    val isShowKeyboard = remember(keyboardHeightDp) { keyboardHeightDp != 0.dp }

    LaunchedEffect(isShowKeyboard) {
        if (!isShowKeyboard) {
            focusManager.clearFocus(force = true)
        }
    }

    if (visible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .noRippleClickable {
                        if (!isShowKeyboard) {
                            focusManager.clearFocus(force = true)
                            onDismiss()
                        }
                    }
            ) {
                val focusRequester = remember { FocusRequester() }
                var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }

                // 自动获取焦点并弹出键盘
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }

                Column(
                    modifier = Modifier
                        .padding(bottom = keyboardHeightDp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Color.White,
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .then(if (!isShowKeyboard) Modifier.navigationBarsPadding() else Modifier)
                        .padding(16.dp)
                ) {
                    TextField(
                        value = textFieldValue,
                        onValueChange = {
                            textFieldValue = it
                            onValueChange(it.text)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = { Text("请输入内容") },
                        singleLine = formFieldEnum == FormFieldEnum.INPUT,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        TextButton(
                            onClick = {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("取消")
                        }

                        TextButton(
                            onClick = {
                                onValueChange(textFieldValue.text)
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("确定")
                        }
                    }
                }
            }
        }
    }
}
