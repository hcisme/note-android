package io.github.hcisme.note.utils

import io.github.hcisme.note.network.model.TodoItemModel

val tasks = listOf(
    TodoItemModel(
        id = 1,
        userId = "user_001",
        title = "早间跑步",
        content = "30 分钟慢跑，拉伸 10 分钟，记录心率",
        isCompleted = 0,
        startTime = "2025-10-16 06:30:00",
        endTime = "2025-10-16 07:10:00",
        createdTime = "2025-10-15 20:12:34",
        updatedTime = "2025-10-15 20:12:34"
    ),
    TodoItemModel(
        id = 2,
        userId = "user_001",
        title = "项目评审",
        content = "与团队讨论功能设计并产出评审记录",
        isCompleted = 0,
        startTime = "2025-10-16 10:00:00",
        endTime = "2025-10-16 11:00:00",
        createdTime = "2025-10-10 09:05:12",
        updatedTime = "2025-10-12 14:22:03"
    ),
    TodoItemModel(
        id = 3,
        userId = "user_001",
        title = "午餐",
        content = "外卖：清淡少油，记得备注不要辣",
        isCompleted = 1,
        startTime = "2025-10-16 12:30:00",
        endTime = "2025-10-16 13:00:00",
        createdTime = "2025-10-16 08:00:00",
        updatedTime = "2025-10-16 13:05:00"
    ),
    TodoItemModel(
        id = 4,
        userId = "user_001",
        title = "代码重构",
        content = "提取公共组件，添加单元测试，修复两个边界 bug",
        isCompleted = 0,
        startTime = "2025-10-17 09:00:00",
        endTime = "2025-10-17 12:00:00",
        createdTime = "2025-10-14 18:30:00",
        updatedTime = "2025-10-15 09:45:00"
    ),
    TodoItemModel(
        id = 5,
        userId = "user_001",
        title = "买菜",
        content = "购买本周蔬菜水果与牛奶，记得带购物袋",
        isCompleted = 1,
        startTime = "2025-10-15 18:00:00",
        endTime = "2025-10-15 18:40:00",
        createdTime = "2025-10-14 11:20:00",
        updatedTime = "2025-10-15 19:00:00"
    ),
    TodoItemModel(
        id = 6,
        userId = "user_001",
        title = "学习 Kotlin 协程",
        content = "阅读协程章节并完成三个练习题",
        isCompleted = 0,
        startTime = "2025-10-18 20:00:00",
        endTime = "2025-10-18 21:30:00",
        createdTime = "2025-10-13 22:10:00",
        updatedTime = "2025-10-13 22:10:00"
    ),
    TodoItemModel(
        id = 7,
        userId = "user_001",
        title = "体检预约",
        content = "预约下周五上午的常规体检，带好身份证与医保卡",
        isCompleted = 0,
        startTime = "2025-10-24 09:00:00",
        endTime = "2025-10-24 10:00:00",
        createdTime = "2025-09-30 16:45:00",
        updatedTime = "2025-10-01 08:12:00"
    ),
    TodoItemModel(
        id = 8,
        userId = "user_001",
        title = "阅读",
        content = "阅读《Clean Code》两章并做笔记",
        isCompleted = 1,
        startTime = "2025-10-12 21:00:00",
        endTime = "2025-10-12 22:15:00",
        createdTime = "2025-10-11 20:00:00",
        updatedTime = "2025-10-12 22:20:00"
    )
)
