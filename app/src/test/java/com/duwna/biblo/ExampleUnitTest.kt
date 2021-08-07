package com.duwna.biblo

import com.duwna.biblo.data.repositories.GroupsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun checkCount() {
        runBlocking {
            GroupsRepository()
                .loadGroupItems()
                .flatMap { it.members }
                .distinct()
                .size
                .also { println(it) }
        }
    }
}
