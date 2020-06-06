package com.duwna.biblo.utils

import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.MemberItem
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

val avatars = listOf(
    "https://randomuser.me/api/portraits/men/32.jpg",
    "https://randomuser.me/api/portraits/women/44.jpg",
    "https://randomuser.me/api/portraits/men/46.jpg",
    "https://randomuser.me/api/portraits/men/97.jpg",
    "https://randomuser.me/api/portraits/men/86.jpg",
    "https://pbs.twimg.com/profile_images/974736784906248192/gPZwCbdS.jpg",
    "https://images-na.ssl-images-amazon.com/images/M/MV5BODFjZTkwMjItYzRhMS00OWYxLWI3YTUtNWIzOWQ4Yjg4NGZiXkEyXkFqcGdeQXVyMTQ0ODAxNzE@._V1_UX172_CR0,0,172,256_AL_.jpg",
    "https://randomuser.me/api/portraits/women/63.jpg",
    "https://pbs.twimg.com/profile_images/969073897189523456/rSuiu_Hr.jpg"
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

        val members = buildList<MemberItem> {
            repeat((2..8).random()) {
                val memberItem = MemberItem(
                    "",
                    names[names.indices.random()],
                    avatars[avatars.indices.random()]
                )
                add(memberItem)
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
