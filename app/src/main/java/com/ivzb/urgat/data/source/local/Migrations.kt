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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE `investments_new` (
                `id` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `shares` TEXT NOT NULL,
                `cost` TEXT NOT NULL,
                `category` TEXT NOT NULL,
                `currency` TEXT NOT NULL,
                `date` INTEGER NOT NULL,
                `isDeleted` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)

        db.execSQL("""
            INSERT INTO `investments_new` (`id`, `name`, `shares`, `cost`, `category`, `currency`, `date`, `isDeleted`, `createdAt`)
            SELECT `id`, `name`, `amount`, `cost`, `category`, `currency`, `date`, `isDeleted`, `createdAt` FROM `investments`
        """)

        db.execSQL("DROP TABLE `investments`")

        db.execSQL("ALTER TABLE `investments_new` RENAME TO `investments`")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `expenses` ADD COLUMN `excludeFromAnalytics` INTEGER NOT NULL DEFAULT 0;")
    }
}

val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
