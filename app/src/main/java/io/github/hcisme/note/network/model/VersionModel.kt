package io.github.hcisme.note.network.model

/**
 * 版本信息
 */
data class VersionModel(
    var id: Long? = null,
    var versionCode: Int? = null,
    var versionName: String? = null,
    var downloadUrl: String? = null,
    var fileSize: Long? = null,
    var fileMd5: String? = null,
    var updateContent: String? = null,
    var published: Int? = null,
    var publishTime: String? = null,
    var createdTime: String? = null,
    var updatedTime: String? = null
)
