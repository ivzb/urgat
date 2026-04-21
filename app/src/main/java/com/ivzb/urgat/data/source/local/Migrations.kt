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

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE `budgets` (
                `id` TEXT NOT NULL, 
                `income` TEXT NOT NULL, 
                `bankStart` TEXT NOT NULL, 
                `bankEnd` TEXT, 
                `date` INTEGER NOT NULL, 
                `isDeleted` INTEGER NOT NULL, 
                `createdAt` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE `savings` (
                `id` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `amount` TEXT NOT NULL, 
                `date` INTEGER NOT NULL, 
                `isDeleted` INTEGER NOT NULL, 
                `createdAt` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE `todo_new` (
                `id` TEXT NOT NULL, 
                `content` TEXT NOT NULL, 
                `isCompleted` INTEGER NOT NULL, 
                `isDeleted` INTEGER NOT NULL, 
                `createdAt` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)

        db.execSQL("""
            INSERT INTO `todo_new` (`id`, `content`, `isCompleted`, `isDeleted`, `createdAt`)
            SELECT `id`, `title` || CHAR(10) || `description`, `isCompleted`, `isDeleted`, `createdAt` FROM `todo`
        """)

        db.execSQL("DROP TABLE `todo`")

        db.execSQL("ALTER TABLE `todo_new` RENAME TO `todo`")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `budgets` ADD COLUMN `currency` TEXT NOT NULL DEFAULT 'EUR';")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE `watchlist_assets` (
                `id` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `isDeleted` INTEGER NOT NULL, 
                `createdAt` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `savings` ADD COLUMN `currency` TEXT NOT NULL DEFAULT 'EUR';")
    }
}

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `expenses` ADD COLUMN `currency` TEXT NOT NULL DEFAULT 'EUR';")
    }
}

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // savings
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_savings_isDeleted_createdAt` ON `savings` (`isDeleted`, `createdAt`);")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_savings_isDeleted_date` ON `savings` (`isDeleted`, `date`);")
        // expenses
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_isDeleted_createdAt` ON `expenses` (`isDeleted`, `createdAt`);")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_isDeleted_date` ON `expenses` (`isDeleted`, `date`);")
        // investments
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_investments_isDeleted_createdAt` ON `investments` (`isDeleted`, `createdAt`);")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_investments_isDeleted_date` ON `investments` (`isDeleted`, `date`);")
        // budgets
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_budgets_isDeleted_createdAt` ON `budgets` (`isDeleted`, `createdAt`);")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_budgets_isDeleted_date` ON `budgets` (`isDeleted`, `date`);")
    }
}

val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_weights_isDeleted_date` ON `weights` (`isDeleted`, `date`);")
    }
}

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `fuel_consumptions` (`id` TEXT NOT NULL, `amount` TEXT NOT NULL, `price` TEXT NOT NULL, `currency` TEXT NOT NULL, `type` TEXT NOT NULL, `carName` TEXT NOT NULL, `odometer` INTEGER NOT NULL, `date` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_fuel_consumptions_isDeleted_date` ON `fuel_consumptions` (`isDeleted`, `date`);")
    }
}

val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `car_maintenance` (`id` TEXT NOT NULL, `description` TEXT NOT NULL, `partNumber` TEXT, `url` TEXT, `carName` TEXT NOT NULL, `odometer` INTEGER NOT NULL, `date` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_car_maintenance_isDeleted_date` ON `car_maintenance` (`isDeleted`, `date`);")
    }
}

val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `trading` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `shares` TEXT NOT NULL, `marketPrice` TEXT NOT NULL, `fee` TEXT NOT NULL, `currency` TEXT NOT NULL, `orderType` TEXT NOT NULL, `date` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trading_isDeleted_createdAt` ON `trading` (`isDeleted`, `createdAt`);")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trading_isDeleted_date` ON `trading` (`isDeleted`, `date`);")
    }
}

val ALL_MIGRATIONS = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4,
    MIGRATION_4_5,
    MIGRATION_5_6,
    MIGRATION_6_7,
    MIGRATION_7_8,
    MIGRATION_8_9,
    MIGRATION_9_10,
    MIGRATION_10_11,
    MIGRATION_11_12,
    MIGRATION_12_13,
    MIGRATION_13_14,
    MIGRATION_14_15,
    MIGRATION_15_16,
    MIGRATION_16_17,
)
