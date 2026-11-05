package com.example.avalokan

import com.example.avalokan.domain.model.Story
import com.example.avalokan.ui.detail.StoryBlock
import com.example.avalokan.ui.detail.StoryMetadata
import com.example.avalokan.ui.detail.StorySpan
import com.example.avalokan.ui.detail.buildStoryShareText
import com.example.avalokan.ui.detail.isSafeStoryLink
import com.example.avalokan.ui.detail.parseStoryBody
import com.example.avalokan.ui.detail.parseStoryInline
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StoryMarkdownTest {

    @Test
    fun `parses headings paragraphs lists and quotes`() {
        val blocks = parseStoryBody(
            """
            ## A living mandala

            The stupa is a mandala.

            - Prayer wheels
            - Butter lamps

            1. Walk clockwise
            2. Remove shoes

            > The stupa is a mandala of the mind.
            """.trimIndent()
        )

        assertEquals(5, blocks.size)

        val heading = blocks[0] as StoryBlock.Heading
        assertEquals(2, heading.level)
        assertEquals("A living mandala", heading.spans.text())

        assertTrue(blocks[1] is StoryBlock.Paragraph)
        assertEquals("The stupa is a mandala.", (blocks[1] as StoryBlock.Paragraph).spans.text())

        val bullets = blocks[2] as StoryBlock.BulletList
        assertEquals(listOf("Prayer wheels", "Butter lamps"), bullets.items.map { it.text() })

        val numbered = blocks[3] as StoryBlock.NumberedList
        assertEquals(listOf("Walk clockwise", "Remove shoes"), numbered.items.map { it.text() })

        val quote = blocks[4] as StoryBlock.Quote
        assertEquals("The stupa is a mandala of the mind.", quote.spans.text())
    }

    @Test
    fun `parses bold italic and safe links`() {
        val spans = parseStoryInline(
            "**Bold** and *italic* with [a source](https://example.com/source)."
        )

        assertEquals(
            listOf(
                StorySpan("Bold", bold = true),
                StorySpan(" and "),
                StorySpan("italic", italic = true),
                StorySpan(" with "),
                StorySpan("a source", link = "https://example.com/source"),
                StorySpan("."),
            ),
            spans
        )
    }

    @Test
    fun `unsafe links stay literal and are never clickable`() {
        val spans = parseStoryInline(
            "[click](javascript:alert(1)) and [ok](http://example.com)"
        )

        assertTrue(spans.none { it.link?.startsWith("javascript") == true })
        assertEquals("http://example.com", spans.last().link)
    }

    @Test
    fun `link safety only allows http and https`() {
        assertTrue(isSafeStoryLink("https://example.com"))
        assertTrue(isSafeStoryLink("HTTP://example.com"))
        assertFalse(isSafeStoryLink("javascript:alert(1)"))
        assertFalse(isSafeStoryLink("data:text/html,<script>"))
        assertFalse(isSafeStoryLink("https://example.com/has space"))
        assertFalse(isSafeStoryLink(""))
    }

    @Test
    fun `supported markdown markers are not rendered literally`() {
        val blocks = parseStoryBody(
            "## Heading\n\n**Bold** and *italic* with [a link](https://example.com)."
        )

        val rendered = buildString {
            blocks.forEach { block ->
                when (block) {
                    is StoryBlock.Heading -> append(block.spans.text())
                    is StoryBlock.Paragraph -> append(block.spans.text())
                    else -> Unit
                }
            }
        }

        assertFalse(rendered.contains("##"))
        assertFalse(rendered.contains("**"))
        assertFalse(rendered.contains("*italic*"))
        assertFalse(rendered.contains("](https://"))
        assertTrue(rendered.contains("Heading"))
        assertTrue(rendered.contains("Bold"))
        assertTrue(rendered.contains("italic"))
        assertTrue(rendered.contains("a link"))
    }

    @Test
    fun `parses a horizontal rule`() {
        assertEquals(listOf(StoryBlock.Divider), parseStoryBody("---"))
    }

    @Test
    fun `blank story bodies produce no blocks`() {
        assertTrue(parseStoryBody("").isEmpty())
        assertTrue(parseStoryBody("   \n\n  ").isEmpty())
    }

    @Test
    fun `metadata drops blank values instead of inventing fallbacks`() {
        val metadata = StoryMetadata.from(
            Story(
                id = "story",
                title = "Title",
                author = " ",
                date = "",
                readTime = "  ",
                curator = "",
                sourceLabel = "",
                sourceUrl = "",
                lastVerifiedAt = ""
            )
        )

        assertNull(metadata.author)
        assertNull(metadata.date)
        assertNull(metadata.readTime)
        assertNull(metadata.curator)
        assertNull(metadata.sourceLabel)
        assertNull(metadata.sourceUrl)
        assertNull(metadata.lastVerifiedAt)
        assertFalse(metadata.hasDateOrReadTime)
        assertFalse(metadata.hasProvenance)
    }

    @Test
    fun `metadata keeps only fields that exist`() {
        val metadata = StoryMetadata.from(
            Story(
                id = "story",
                title = "Title",
                author = "Samrat Parajuli",
                readTime = "6 min read",
                curator = "Heritage desk",
                sourceLabel = "Department of Archaeology",
                sourceUrl = "https://example.com/source",
                lastVerifiedAt = "2026-10-01"
            )
        )

        assertEquals("Samrat Parajuli", metadata.author)
        assertEquals("6 min read", metadata.readTime)
        assertEquals("Heritage desk", metadata.curator)
        assertEquals("Department of Archaeology", metadata.sourceLabel)
        assertEquals("https://example.com/source", metadata.sourceUrl)
        assertEquals("2026-10-01", metadata.lastVerifiedAt)
        assertTrue(metadata.hasDateOrReadTime)
        assertTrue(metadata.hasProvenance)
    }

    @Test
    fun `share text omits missing author and read time`() {
        val text = buildStoryShareText(
            title = "The Sacred Echoes",
            author = "",
            readTime = "",
            fallbackTitle = "Untitled"
        )

        assertEquals(
            "“The Sacred Echoes” — a story on Avalokan, a Nepal Heritage Explorer.",
            text
        )
        assertFalse(text.contains("not available", ignoreCase = true))
        assertFalse(text.contains("Author", ignoreCase = true))
    }

    @Test
    fun `share text includes real author and read time`() {
        val text = buildStoryShareText(
            title = "The Sacred Echoes",
            author = "Samrat Parajuli",
            readTime = "6 min read",
            fallbackTitle = "Untitled"
        )

        assertEquals(
            "“The Sacred Echoes” — a story by Samrat Parajuli (6 min read) " +
                "on Avalokan, a Nepal Heritage Explorer.",
            text
        )
    }
}

private fun List<StorySpan>.text(): String = joinToString("") { it.text }
