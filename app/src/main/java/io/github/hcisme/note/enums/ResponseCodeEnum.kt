package io.github.hcisme.note.enums

enum class ResponseCodeEnum(val code: Int, val msg: String) {
    CODE_200(200, "请求成功"),
    CODE_401(401, "身份验证已过期，请重新登陆"),
    CODE_404(404, "请求地址不存在"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "信息已经存在"),
    CODE_500(500, "服务器返回错误，请联系管理员");
}
