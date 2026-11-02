package com.example.avalokan

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.avalokan.ui.components.HeritageCard
import com.example.avalokan.ui.theme.AvalokanTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeritageCardTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun rendersContentAndReportsSaveClicks() {
        var saved = false
        compose.setContent {
            AvalokanTheme {
                HeritageCard(
                    title = "Patan Durbar Square",
                    subtitle = "Lalitpur • Historical Site",
                    badge = "UNESCO Site",
                    rating = "4.8",
                    description = "Artistic heritage of Lalitpur.",
                    showSave = true,
                    isSaved = false,
                    onSaveClick = { saved = true },
                )
            }
        }

        compose.onNodeWithText("Patan Durbar Square").assertIsDisplayed()
        compose.onNodeWithText("Lalitpur • Historical Site").assertIsDisplayed()
        compose.onNodeWithText("UNESCO Site").assertIsDisplayed()
        compose.onNodeWithText("4.8").assertIsDisplayed()

        compose.onNodeWithContentDescription("Save Patan Durbar Square to collection")
            .performClick()

        assertTrue(saved)
    }
}
