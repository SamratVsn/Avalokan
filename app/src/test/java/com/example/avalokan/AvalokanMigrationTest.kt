package com.example.avalokan

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import com.example.avalokan.data.MIGRATION_9_10
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the v9 -> v10 cache migration without a device.
 *
 * Room's `MigrationTestHelper` needs instrumentation, but the migration itself
 * only talks to `androidx.sqlite`. A recording connection lets us pin the exact
 * DDL that runs on an existing install: every new classification column must be
 * added as `TEXT NOT NULL DEFAULT ''` so cached rows survive the update instead
 * of being dropped by a destructive fallback.
 */
class AvalokanMigrationTest {

    private class RecordingConnection : SQLiteConnection {
        val statements = mutableListOf<String>()

        override fun prepare(sql: String): SQLiteStatement {
            statements += sql
            return NoopStatement
        }

        override fun close() = Unit
    }

    private object NoopStatement : SQLiteStatement {
        override fun bindBlob(index: Int, value: ByteArray) = Unit
        override fun bindDouble(index: Int, value: Double) = Unit
        override fun bindLong(index: Int, value: Long) = Unit
        override fun bindText(index: Int, value: String) = Unit
        override fun bindNull(index: Int) = Unit
        override fun getBlob(index: Int): ByteArray = error("not used")
        override fun getDouble(index: Int): Double = error("not used")
        override fun getLong(index: Int): Long = error("not used")
        override fun getText(index: Int): String = error("not used")
        override fun isNull(index: Int): Boolean = error("not used")
        override fun getColumnCount(): Int = 0
        override fun getColumnName(index: Int): String = error("not used")
        override fun getColumnType(index: Int): Int = error("not used")
        override fun step(): Boolean = true
        override fun reset() = Unit
        override fun clearBindings() = Unit
        override fun close() = Unit
    }

    private fun migrate(): List<String> {
        val connection = RecordingConnection()
        runBlocking { MIGRATION_9_10.migrate(connection) }
        return connection.statements
    }

    @Test
    fun `migration adds the place classification columns`() {
        val expected = listOf(
            "primaryCategory",
            "secondaryCategories",
            "typologies",
            "attributes",
            "tags",
            "locality",
            "relatedPlaceIds",
            "relatedEventIds",
        )

        val statements = migrate()

        expected.forEach { column ->
            assertTrue(
                "places.$column was not added",
                statements.any { it.startsWith("ALTER TABLE places ADD COLUMN $column ") },
            )
        }
    }

    @Test
    fun `migration adds the event presentation columns`() {
        val expected = listOf(
            "priceBasis",
            "participation",
            "format",
            "secondaryCategories",
            "tags",
            "relatedPlaceIds",
            "relatedEventIds",
        )

        val statements = migrate()

        expected.forEach { column ->
            assertTrue(
                "events.$column was not added",
                statements.any { it.startsWith("ALTER TABLE events ADD COLUMN $column ") },
            )
        }
    }

    @Test
    fun `migration adds the story editorial columns`() {
        val expected = listOf(
            "primaryCategory",
            "secondaryCategories",
            "typologies",
            "tags",
            "relatedEventIds",
            "period",
        )

        val statements = migrate()

        expected.forEach { column ->
            assertTrue(
                "stories.$column was not added",
                statements.any { it.startsWith("ALTER TABLE stories ADD COLUMN $column ") },
            )
        }
    }

    @Test
    fun `every added column is non-null with a safe empty default`() {
        val statements = migrate()

        assertTrue(statements.isNotEmpty())
        statements.forEach { sql ->
            assertTrue(
                "unexpected destructive SQL: $sql",
                sql.startsWith("ALTER TABLE places ADD COLUMN") ||
                    sql.startsWith("ALTER TABLE events ADD COLUMN") ||
                    sql.startsWith("ALTER TABLE stories ADD COLUMN"),
            )
            assertTrue("column must be NOT NULL: $sql", sql.contains("NOT NULL"))
            assertTrue("column must default to empty text: $sql", sql.contains("DEFAULT ''"))
        }
    }

    @Test
    fun `migration never drops or recreates the cached tables`() {
        val statements = migrate()
        val destructive = listOf("DROP TABLE", "DELETE FROM", "CREATE TABLE")

        assertTrue(statements.isNotEmpty())
        destructive.forEach { keyword ->
            assertTrue(
                "migration must preserve cached content, found $keyword",
                statements.none { it.contains(keyword, ignoreCase = true) },
            )
        }
    }

    @Test
    fun `migration targets exactly version 9 to 10`() {
        assertEquals(9, MIGRATION_9_10.startVersion)
        assertEquals(10, MIGRATION_9_10.endVersion)
    }
}
