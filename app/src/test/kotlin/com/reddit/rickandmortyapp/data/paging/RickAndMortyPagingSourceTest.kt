package com.reddit.rickandmortyapp.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.testing.TestPager
import com.reddit.rickandmortyapp.data.api.RickAndMortyApi
import com.reddit.rickandmortyapp.data.api.RickAndMortyCharacter
import com.reddit.rickandmortyapp.data.api.RickAndMortyResponse
import com.reddit.rickandmortyapp.domain.entities.RickAndMortyCharacterEntity
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalSerializationApi::class)
class RickAndMortyPagingSourceTest {

    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var response: RickAndMortyResponse
    private lateinit var responseList: List<RickAndMortyCharacter>
    private lateinit var testList: List<RickAndMortyCharacterEntity>

    @MockK
    private lateinit var api: RickAndMortyApi
    private lateinit var pagingSource: RickAndMortyPagingSource
    private lateinit var pager: TestPager<Int, RickAndMortyCharacterEntity>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(StandardTestDispatcher())

        response = this::class.java.classLoader
            ?.getResourceAsStream("test_page.json")?.let {
                json.decodeFromStream<RickAndMortyResponse>(it)
            }!!.also {
                responseList = it.results
            }

        testList = responseList.map {
            RickAndMortyCharacterEntity(
                id = it.id,
                name = it.name,
                image = it.image
            )
        }

        coEvery {
            api.getCharacters(any<Int>())
        } returns response

        pagingSource = RickAndMortyPagingSource(api)
        pager = TestPager(
            pagingSource = pagingSource,
            config = PagingConfig(
                pageSize = 40
            ))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun testLoadInitial() = runTest {
        val initialPage = pager.refresh() as Page

        assert(initialPage.data.isNotEmpty())
        assertEquals(20, initialPage.data.size)
        assertEquals(2, initialPage.nextKey)
        assertNull(initialPage.prevKey)
        assertEquals(testList, initialPage.data)
    }

    @Test
    fun testLoadThreePages() = runTest {
        val page = with(pager) {
            refresh()
            append()
            append()
        } as Page

        assert(page.data.isNotEmpty())
        assertEquals(20, page.data.size)
        assertEquals(4, page.nextKey)
        assertEquals(2, page.prevKey)
        assertEquals(testList, page.data)
    }
}