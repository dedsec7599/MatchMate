package com.harshvardhan.matchmate.data.repos

import org.junit.Assert.assertEquals
import org.junit.Test

class MatchPagingConfigTest {

    @Test
    fun `next page is requested only at the final displayed item`() {
        assertEquals(0, matchPagingConfig().prefetchDistance)
    }
}
