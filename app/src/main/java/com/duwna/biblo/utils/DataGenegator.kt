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

fun getGroupList(): List<GroupItem> {
    val list = mutableListOf<GroupItem>()
    groupNames.forEach {
        val members = buildList<String> {
            repeat((2..5).random()) {
                add(names[names.indices.random()])
            }
        }
        val groupItem = GroupItem(
            "",
            it,
            null,
            "Р",
            Date().addDays((-10..10).random()).format("HH:mm\ndd.MM"),
            members
        )
        list.add(groupItem)
    }
    return list
}

fun Date.addDays(days: Int) = Date(time + TimeUnit.DAYS.toMillis(1))

inline fun <E> buildList(builderAction: MutableList<E>.() -> Unit): List<E> {
    return ArrayList<E>().apply(builderAction)
}
