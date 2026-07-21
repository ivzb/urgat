package com.ivzb.urgat.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ivzb.urgat.data.budget.BudgetDao
import com.ivzb.urgat.data.budget.BudgetEntity
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.expense.ExpenseEntity
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceDao
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceEntity
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionDao
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionEntity
import com.ivzb.urgat.data.investment.InvestmentDao
import com.ivzb.urgat.data.investment.InvestmentEntity
import com.ivzb.urgat.data.note.NoteDao
import com.ivzb.urgat.data.note.NoteEntity
import com.ivzb.urgat.data.saving.SavingDao
import com.ivzb.urgat.data.saving.SavingEntity
import com.ivzb.urgat.data.todo.TodoDao
import com.ivzb.urgat.data.todo.TodoEntity
import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.trade.TradeEntity
import com.ivzb.urgat.data.watchlist.WatchlistAssetDao
import com.ivzb.urgat.data.watchlist.WatchlistAssetEntity
import com.ivzb.urgat.data.weight.WeightDao
import com.ivzb.urgat.data.weight.WeightEntity

@Database(entities = [
    TodoEntity::class,
    ExpenseEntity::class,
    NoteEntity::class,
    WeightEntity::class,
    InvestmentEntity::class,
    SavingEntity::class,
    BudgetEntity::class,
    WatchlistAssetEntity::class,
    FuelConsumptionEntity::class,
    CarMaintenanceEntity::class,
    TradeEntity::class,
 ], version = 19)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    abstract fun expenseDao(): ExpenseDao

    abstract fun noteDao(): NoteDao

    abstract fun weightDao(): WeightDao

    abstract fun investmentDao(): InvestmentDao

    abstract fun savingDao(): SavingDao

    abstract fun budgetDao(): BudgetDao

    abstract fun watchlistAssetDao(): WatchlistAssetDao

    abstract fun fuelConsumptionDao(): FuelConsumptionDao

    abstract fun carMaintenanceDao(): CarMaintenanceDao

    abstract fun tradeDao(): TradeDao

}