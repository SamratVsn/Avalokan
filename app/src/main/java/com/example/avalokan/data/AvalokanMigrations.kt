package com.example.avalokan.data

import androidx.room3.migration.Migration
import androidx.sqlite.execSQL

/**
 * v9 -> v10 adds the multi-dimensional classification columns.
 *
 * These tables are a disposable Firestore cache, but the migration still keeps
 * the cached rows so an offline launch after an app update does not flash an
 * empty Discover/Events screen. Fresh installs create the same columns from the
 * entity definitions.
 */
internal val MIGRATION_9_10 = Migration(9, 10) { connection ->
    val placeColumns = listOf(
        "primaryCategory",
        "secondaryCategories",
        "typologies",
        "attributes",
        "tags",
        "locality",
        "relatedPlaceIds",
        "relatedEventIds",
    )
    placeColumns.forEach { column ->
        connection.execSQL("ALTER TABLE places ADD COLUMN $column TEXT NOT NULL DEFAULT ''")
    }

    val eventColumns = listOf(
        "priceBasis",
        "participation",
        "format",
        "secondaryCategories",
        "tags",
        "relatedPlaceIds",
        "relatedEventIds",
    )
    eventColumns.forEach { column ->
        connection.execSQL("ALTER TABLE events ADD COLUMN $column TEXT NOT NULL DEFAULT ''")
    }

    val storyColumns = listOf(
        "primaryCategory",
        "secondaryCategories",
        "typologies",
        "tags",
        "relatedEventIds",
        "period",
    )
    storyColumns.forEach { column ->
        connection.execSQL("ALTER TABLE stories ADD COLUMN $column TEXT NOT NULL DEFAULT ''")
    }
}
