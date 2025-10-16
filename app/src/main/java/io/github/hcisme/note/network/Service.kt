package io.github.hcisme.note.network

import io.github.hcisme.note.network.api.CaptchaApi
import io.github.hcisme.note.network.api.TodoItemApi
import io.github.hcisme.note.network.api.UserApi

val CaptchaService = Request.createService<CaptchaApi>()
val UserService = Request.createService<UserApi>()
val TodoItemService = Request.createService<TodoItemApi>()
