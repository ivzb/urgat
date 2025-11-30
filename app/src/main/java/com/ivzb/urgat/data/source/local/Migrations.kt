package com.ivzb.urgat.data.source.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `investments` ADD COLUMN `cost` TEXT NOT NULL DEFAULT '0';")
        db.execSQL("ALTER TABLE `investments` ADD COLUMN `date` INTEGER NOT NULL DEFAULT 0;")
        db.execSQL("UPDATE `investments` SET `date` = `createdAt` WHERE `date` = 0;")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `weights` ADD COLUMN `date` INTEGER NOT NULL DEFAULT 0;")
        db.execSQL("UPDATE `weights` SET `date` = `createdAt` WHERE `date` = 0;")
    }
}

val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3)
