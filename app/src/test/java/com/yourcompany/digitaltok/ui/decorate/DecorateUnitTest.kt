package com.yourcompany.digitaltok.ui.decorate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DecorateUnitTest {

    @Test
    fun testDecorateItemCreation() {
        val item = DecorateItem(
            id = "test_1",
            title = "내 사진",
            imageUrl = "https://example.com/test.jpg"
        )

        assertEquals("test_1", item.id)
        assertEquals("내 사진", item.title)
        assertEquals("https://example.com/test.jpg", item.imageUrl)
    }

    @Test
    fun testRecentItemsListPrepend() {
        val addSlot = DecorateItem("add", "추가")
        val defaultItems = listOf(
            addSlot,
            DecorateItem("1", "템플릿 1"),
            DecorateItem("2", "템플릿 2")
        )

        val croppedItem = DecorateItem("user_123", "내 사진")
        
        // Simulating the item insertion logic in DecorateScreen onCropSuccess
        val updatedItems = listOf(defaultItems.first()) + listOf(croppedItem) + defaultItems.drop(1)

        assertEquals(4, updatedItems.size)
        assertEquals("add", updatedItems[0].id)
        assertEquals("user_123", updatedItems[1].id)
        assertEquals("1", updatedItems[2].id)
    }
}
