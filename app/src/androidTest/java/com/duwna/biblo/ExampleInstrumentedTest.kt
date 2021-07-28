package com.duwna.biblo

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.repositories.GroupsRepository
import com.duwna.biblo.utils.log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun insertManyGroups() {

        val groupsRepository = GroupsRepository()


        val users = MutableList((2..7).random()) {
            if (it == 0) User(idUser = groupsRepository.firebaseUserId)
            else User(name = words.random())
        }


        GlobalScope.launch(IO) {

            val groupsCount = groupsRepository.loadGroupItems().size
            val newGroups = 100

            println(groupsCount)

            repeat(newGroups) {
                groupsRepository.insertGroup(
                    name = words.random(),
                    currency = currency.random(),
                    avatarUri = null,
                    users = users,
                    groupItem = null
                )
            }

            Assert.assertEquals(
                groupsRepository.loadGroupItems().size.also { println(it) },
                groupsCount.plus(newGroups)
            )

        }

        //                groupItems.forEach { groupItem ->
//                    val repo = BillsRepository(groupItem.id)
//                    repeat((5..30).random()) {
//                        val bill = Bill(
//                            title = words.random(),
//                            description = buildString { repeat(5) { append(words.random()) } },
//                            payers = mutableMapOf<String, Double>().apply {
//                                groupItem.members.forEach { put(it.id, (0..10000).random().toDouble()) }
//                            },
//                            debtors = mutableMapOf<String, Double>().apply {
//                                groupItem.members.forEach { put(it.id, (0..10000).random().toDouble()) }
//                            }
//                        )
//                        repo.insertBill(bill)
//                    }
//                }

    }



    private fun randomString(length: Int = 10) = buildString {
        repeat(length) {
            append(('a'..'z').random())
        }
    }

    private val currency = listOf(
        "₽",
        "$",
        "¢",
        "£",
        "¤",
        "¥",
        "֏",
        "৲",
        "৳",
        "৻",
        "૱",
        "௹",
        "฿",
        "៛",
        "₠",
        "₡",
        "₢",
        "₣",
        "₤",
        "₥",
        "₦",
        "₧",
        "₨",
        "₩",
        "₪",
        "₫",
        "€",
        "₭",
        "₮",
        "₯",
        "₰",
        "₲",
        "₳",
        "₴",
        "₵",
        "₶",
        "₷",
        "₸",
        "₹",
        "₺"
    )

    private val words = listOf(
        "world",
        "any",
        "content",
        "believe",
        "strange",
        "same",
        "habit",
        "behavior",
        "coal",
        "curious",
        "absolute",
        "former",
        "camera",
        "stiffen",
        "enemy",
        "salt",
        "camp",
        "bridge",
        "accept",
        "polite",
        "plant",
        "wood",
        "ill",
        "wood",
        "drive"
    )
}
