package com.example.avalokan.ui.detail

import com.example.avalokan.domain.model.Story

/**
 * A constrained, safe subset of Markdown for story bodies.
 *
 * Supported: ATX headings, paragraphs, bullet and numbered lists, blockquotes,
 * horizontal rules, bold, italic, and http(s) links. Everything else is shown
 * as plain text. Raw HTML is never interpreted and unsafe link schemes are
 * never turned into clickable annotations.
 */
sealed interface StoryBlock {
    data class Heading(val level: Int, val spans: List<StorySpan>) : StoryBlock
    data class Paragraph(val spans: List<StorySpan>) : StoryBlock
    data class BulletList(val items: List<List<StorySpan>>) : StoryBlock
    data class NumberedList(val items: List<List<StorySpan>>) : StoryBlock
    data class Quote(val spans: List<StorySpan>) : StoryBlock
    data object Divider : StoryBlock
}

/**
 * A run of story text with the inline styles the renderer understands.
 * [link] is only set after [isSafeStoryLink] accepts the URL.
 */
data class StorySpan(
    val text: String,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val link: String? = null,
)

/**
 * Only the metadata that actually exists. Blank Firestore fields become null so
 * the UI can omit a row instead of printing "not available".
 */
data class StoryMetadata(
    val author: String? = null,
    val date: String? = null,
    val readTime: String? = null,
    val curator: String? = null,
    val sourceLabel: String? = null,
    val sourceUrl: String? = null,
    val lastVerifiedAt: String? = null,
) {
    val hasDateOrReadTime: Boolean get() = date != null || readTime != null
    val hasProvenance: Boolean get() = sourceLabel != null || sourceUrl != null || lastVerifiedAt != null

    companion object {
        fun from(story: Story): StoryMetadata = StoryMetadata(
            author = story.author.cleanOrNull(),
            date = story.date.cleanOrNull(),
            readTime = story.readTime.cleanOrNull(),
            curator = story.curator.cleanOrNull(),
            sourceLabel = story.sourceLabel.cleanOrNull(),
            sourceUrl = story.sourceUrl.cleanOrNull(),
            lastVerifiedAt = story.lastVerifiedAt.cleanOrNull(),
        )
    }
}

internal fun parseStoryBody(markdown: String): List<StoryBlock> {
    val lines = markdown
        .replace("\r\n", "\n")
        .replace('\r', '\n')
        .split('\n')

    val blocks = mutableListOf<StoryBlock>()
    val paragraphLines = mutableListOf<String>()

    fun flushParagraph() {
        if (paragraphLines.isEmpty()) return
        val text = paragraphLines.joinToString("\n").trim()
        paragraphLines.clear()
        if (text.isNotEmpty()) {
            blocks += StoryBlock.Paragraph(parseStoryInline(text))
        }
    }

    var index = 0
    while (index < lines.size) {
        val line = lines[index].trimEnd()

        if (line.isBlank()) {
            flushParagraph()
            index++
            continue
        }

        if (DIVIDER.matches(line)) {
            flushParagraph()
            blocks += StoryBlock.Divider
            index++
            continue
        }

        val heading = HEADING.matchEntire(line)
        if (heading != null) {
            flushParagraph()
            val text = heading.groupValues[2].trimEnd('#').trim()
            if (text.isNotEmpty()) {
                blocks += StoryBlock.Heading(
                    level = heading.groupValues[1].length,
                    spans = parseStoryInline(text),
                )
            }
            index++
            continue
        }

        val quote = QUOTE.matchEntire(line)
        if (quote != null) {
            flushParagraph()
            val quoteLines = mutableListOf(quote.groupValues[1])
            index++
            while (index < lines.size) {
                val next = QUOTE.matchEntire(lines[index].trimEnd()) ?: break
                quoteLines += next.groupValues[1]
                index++
            }
            blocks += StoryBlock.Quote(parseStoryInline(quoteLines.joinToString("\n").trim()))
            continue
        }

        val bullet = BULLET.matchEntire(line)
        if (bullet != null) {
            flushParagraph()
            val items = mutableListOf<List<StorySpan>>()
            while (index < lines.size) {
                val item = BULLET.matchEntire(lines[index].trimEnd()) ?: break
                items += parseStoryInline(item.groupValues[1].trim())
                index++
            }
            blocks += StoryBlock.BulletList(items)
            continue
        }

        val numbered = NUMBERED.matchEntire(line)
        if (numbered != null) {
            flushParagraph()
            val items = mutableListOf<List<StorySpan>>()
            while (index < lines.size) {
                val item = NUMBERED.matchEntire(lines[index].trimEnd()) ?: break
                items += parseStoryInline(item.groupValues[1].trim())
                index++
            }
            blocks += StoryBlock.NumberedList(items)
            continue
        }

        paragraphLines += line.trim()
        index++
    }

    flushParagraph()
    return blocks
}

internal fun parseStoryInline(text: String): List<StorySpan> {
    if (text.isEmpty()) return emptyList()
    val spans = mutableListOf<StorySpan>()
    appendInline(text, 0, text.length, bold = false, italic = false, link = null, out = spans)
    return spans
}

/** Only ordinary web links are clickable; everything else stays literal text. */
internal fun isSafeStoryLink(url: String): Boolean {
    val value = url.trim()
    if (value.isEmpty()) return false
    if (value.any { it.isWhitespace() || it.isISOControl() }) return false
    return value.startsWith("https://", ignoreCase = true) ||
        value.startsWith("http://", ignoreCase = true)
}

/**
 * Share copy that only mentions details the story actually has. Missing
 * author/read-time fields are omitted rather than replaced with placeholders.
 */
internal fun buildStoryShareText(
    title: String,
    author: String,
    readTime: String,
    fallbackTitle: String,
): String {
    val displayTitle = title.trim().ifEmpty {
        fallbackTitle.trim().ifEmpty { "Untitled" }
    }
    val cleanAuthor = author.trim().takeIf { it.isNotEmpty() }
    val cleanReadTime = readTime.trim().takeIf { it.isNotEmpty() }
    val subject = when {
        cleanAuthor != null && cleanReadTime != null -> "a story by $cleanAuthor ($cleanReadTime)"
        cleanAuthor != null -> "a story by $cleanAuthor"
        cleanReadTime != null -> "a $cleanReadTime story"
        else -> "a story"
    }
    return "“$displayTitle” — $subject on Avalokan, a Nepal Heritage Explorer."
}

private fun appendInline(
    text: String,
    start: Int,
    end: Int,
    bold: Boolean,
    italic: Boolean,
    link: String?,
    out: MutableList<StorySpan>,
) {
    val buffer = StringBuilder()

    fun flush() {
        if (buffer.isNotEmpty()) {
            out += StorySpan(buffer.toString(), bold = bold, italic = italic, link = link)
            buffer.clear()
        }
    }

    var index = start
    while (index < end) {
        val char = text[index]

        if (char == '\\' && index + 1 < end && text[index + 1] in ESCAPABLE) {
            buffer.append(text[index + 1])
            index += 2
            continue
        }

        if (char == '[') {
            val bounds = findLinkBounds(text, index, end)
            if (bounds != null) {
                val url = text.substring(bounds.urlStart, bounds.urlEnd).trim()
                if (isSafeStoryLink(url)) {
                    flush()
                    val label = text.substring(index + 1, bounds.labelEnd)
                    val labelSpans = mutableListOf<StorySpan>()
                    appendInline(label, 0, label.length, bold, italic, url, labelSpans)
                    if (labelSpans.isEmpty()) {
                        out += StorySpan(url, bold = bold, italic = italic, link = url)
                    } else {
                        out += labelSpans
                    }
                    index = bounds.urlEnd + 1
                    continue
                }
            }
        }

        val strongMarker = when {
            index + 2 <= end && text.startsWith("**", index) -> "**"
            index + 2 <= end && text.startsWith("__", index) -> "__"
            else -> null
        }
        if (strongMarker != null) {
            val close = text.indexOf(strongMarker, index + 2)
            if (close in (index + 2) until end) {
                flush()
                appendInline(
                    text = text,
                    start = index + 2,
                    end = close,
                    bold = true,
                    italic = italic,
                    link = link,
                    out = out,
                )
                index = close + 2
                continue
            }
        }

        val emphasisMarker = when (char) {
            '*' -> "*"
            '_' -> "_"
            else -> null
        }
        if (emphasisMarker != null) {
            val previousIsWord = emphasisMarker == "_" &&
                index > start &&
                text[index - 1].isLetterOrDigit()
            if (!previousIsWord) {
                val close = text.indexOf(emphasisMarker, index + 1)
                if (close in (index + 1) until end) {
                    flush()
                    appendInline(
                        text = text,
                        start = index + 1,
                        end = close,
                        bold = bold,
                        italic = true,
                        link = link,
                        out = out,
                    )
                    index = close + 1
                    continue
                }
            }
        }

        buffer.append(char)
        index++
    }

    flush()
}

private data class LinkBounds(
    val labelEnd: Int,
    val urlStart: Int,
    val urlEnd: Int,
)

private fun findLinkBounds(text: String, open: Int, end: Int): LinkBounds? {
    val labelEnd = text.indexOf(']', open + 1)
    if (labelEnd <= open + 1 || labelEnd + 1 >= end || text[labelEnd + 1] != '(') return null
    val urlStart = labelEnd + 2
    val urlEnd = text.indexOf(')', urlStart)
    if (urlEnd == -1 || urlEnd >= end) return null
    return LinkBounds(labelEnd = labelEnd, urlStart = urlStart, urlEnd = urlEnd)
}

private fun String.cleanOrNull(): String? = trim().takeIf { it.isNotEmpty() }

private val ESCAPABLE = setOf('\\', '*', '_', '[', ']', '(', ')', '#', '>', '-', '+', '.', '!')
private val HEADING = Regex("""^(#{1,6})\s+(.*)$""")
private val QUOTE = Regex("""^\s*>\s?(.*)$""")
private val BULLET = Regex("""^\s*[-*+]\s+(.*)$""")
private val NUMBERED = Regex("""^\s*\d+[.)]\s+(.*)$""")
private val DIVIDER = Regex("""^\s*([-*_])(?:\s*\1){2,}\s*$""")
