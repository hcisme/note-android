package io.github.hcisme.note.network

import io.github.hcisme.note.network.api.CaptchaApi
import io.github.hcisme.note.network.api.TodoItemApi
import io.github.hcisme.note.network.api.UserApi
import io.github.hcisme.note.network.api.VersionApi

val CaptchaService = Request.createService<CaptchaApi>()
val UserService = Request.createService<UserApi>()
val TodoItemService = Request.createService<TodoItemApi>()
val VersionService = Request.createService<VersionApi>()
