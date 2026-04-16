package com.ivzb.urgat.data.base

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

object SearchQueryBuilder {

    fun build(
        tableName: String,
        searchColumns: List<String>,
        dateColumn: String,
        orderBy: String,
        limit: Int,
        dateStart: Long,
        dateEnd: Long,
        searchQuery: String,
    ): SupportSQLiteQuery {
        val words = searchQuery.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        val args = mutableListOf<Any>(dateStart, dateEnd)

        val wordClauses = words.map { word ->
            val columnClauses = searchColumns.map { column ->
                args.add("%$word%")
                "$column LIKE ?"
            }
            "(${columnClauses.joinToString(" OR ")})"
        }

        val searchClause = if (wordClauses.isNotEmpty()) {
            "AND ${wordClauses.joinToString(" AND ")}"
        } else ""

        args.add(limit)

        val sql = "SELECT * FROM $tableName " +
                "WHERE isDeleted = 0 AND $dateColumn BETWEEN ? AND ? $searchClause " +
                "ORDER BY $orderBy " +
                "LIMIT ?"

        return SimpleSQLiteQuery(sql, args.toTypedArray())
    }
}

