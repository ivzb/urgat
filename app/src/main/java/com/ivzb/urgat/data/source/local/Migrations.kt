package com.ivzb.urgat.data.source.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE investments ADD COLUMN `cost` TEXT NOT NULL DEFAULT '0';")
        db.execSQL("ALTER TABLE investments ADD COLUMN `date` INTEGER NOT NULL DEFAULT 0;")
    }
}
