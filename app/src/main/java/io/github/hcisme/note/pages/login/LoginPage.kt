package io.github.hcisme.note.pages.login

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.R
import io.github.hcisme.note.components.AnimatedLabelText
import io.github.hcisme.note.components.RotationIcon
import io.github.hcisme.note.navigation.navigateToHomeAndClearStack
import io.github.hcisme.note.utils.LocalNavController

@Composable
fun LoginPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current
    val loginVM = viewModel<LoginViewModel>()

    LaunchedEffect(Unit) {
        loginVM.getCaptcha()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Text(
            text = "登录",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp, Alignment.CenterVertically)
        ) {
            TextField(
                value = loginVM.email,
                onValueChange = { loginVM.email = it },
                label = {
                    AnimatedLabelText(defaultLabel = "邮箱", errorMessage = loginVM.emailError)
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = null
                    )
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 20.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            TextField(
                value = loginVM.password,
                onValueChange = { loginVM.password = it },
                label = {
                    AnimatedLabelText(defaultLabel = "密码", errorMessage = loginVM.passwordError)
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null
                    )
                },
                visualTransformation = if (loginVM.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        painterResource(if (loginVM.passwordVisible) R.drawable.visible else R.drawable.invisible),
                        contentDescription = null,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            loginVM.passwordVisible = !loginVM.passwordVisible
                        }
                    )
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 20.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = loginVM.captcha,
                    onValueChange = { loginVM.captcha = it },
                    label = {
                        AnimatedLabelText(
                            defaultLabel = "验证码",
                            errorMessage = loginVM.captchaError
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.weight(1F)
                )

                Surface(
                    modifier = Modifier
                        .size(
                            140.dp,
                            TextFieldDefaults.MinHeight + 1.dp
                        ),
                    shape = MaterialTheme.shapes.small
                ) {
                    loginVM.captchaBitmap?.let {
                        Image(
                            bitmap = it,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { loginVM.getCaptcha() },
                            contentDescription = "验证码图片",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Button(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 24.dp, end = 8.dp)
                    .width(160.dp),
                onClick = { loginVM.submit { navController.navigateToHomeAndClearStack() } },
                enabled = loginVM.isLoginIng.not(),
                shape = MaterialTheme.shapes.small
            ) {
                if (loginVM.isLoginIng) {
                    RotationIcon(painter = painterResource(R.drawable.loading_circle))
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("登录")
            }
        }
    }

    BackHandler {
        (context as Activity).finish()
    }
}

