package com.ivzb.urgat.data.carMaintenance

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

data class CarMaintenance(
    val id: String,
    val description: String,
    val partNumber: String?,
    val url: String?,
    val carName: String,
    val odometer: Int,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    val summary: AnnotatedString =
        buildAnnotatedString {
            append(description)
        }

    fun formatCsv(): String =
        "\"$id\", \"$description\", \"$partNumber\", \"$url\", \"$carName\", $odometer, $date, $createdAt"

}

