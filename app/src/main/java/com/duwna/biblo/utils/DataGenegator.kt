package com.duwna.biblo.utils

import com.duwna.biblo.models.items.GroupItem
import java.util.*
import java.util.concurrent.TimeUnit


val names = listOf(
    "Дмитpий",
    "Ярослав",
    "Руслан",
    "Эдуард",
    "Егор",
    "Юрий",
    "Алексей",
    "Анатолий",
    "Артем",
    "Станислав",
    "Татьяна",
    "Елена",
    "Светлана",
    "Ольга",
    "Юлиана",
    "Яна",
    "Юлия",
    "Алена"
)

val groupNames = listOf(
    "Сервис",
    "Дом",
    "Альянс",
    "Фото",
    "Альфа",
    "Маркет",
    "Снаб",
    "Лэнд",
    "Мета",
    "Альфа",
    "Атон",
    "Профи",
    "Запад",
    "Фан",
    "Комплект"
)

//fun getGroupList(): List<GroupItem> {
//    val list = mutableListOf<GroupItem>()
//    groupNames.forEach {
//        val members = mutableListOf<GroupItem.Member>().apply {
//            repeat((2..5).random()) {
//                add(GroupItem.Member(names[(names.indices).random()], ""))
//            }
//        }
//        val groupItem = GroupItem(
//            "",
//            it,
//            "",
//            "Р",
//            Date().addDays((-10..10).random()).format(),
//            members
//        )
//        list.add(groupItem)
//    }
//    return list
//}

fun Date.addDays(days: Int) = Date(time + TimeUnit.DAYS.toMillis(1))

