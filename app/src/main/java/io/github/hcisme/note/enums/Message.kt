package io.github.hcisme.note.enums

enum class Message(val message: String) {
    ADD_SUCCESS("新增成功"),
    EDIT_SUCCESS("编辑成功"),
    DELETE_SUCCESS("删除成功"),
    DELETE_FAIL("删除失败"),

    LOGIN_SUCCESS("登录成功"),
    LOGIN_FAIL("登录失败"),
    PROFILE_UPDATE_SUCCESS("资料更新成功"),
    PROFILE_UPDATE_FAIL("资料更新失败"),
}
