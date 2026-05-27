package com.example.appsearchsender.data

import androidx.appsearch.annotation.Document
import androidx.appsearch.app.AppSearchSchema

@Document
data class BaseItem(
    @Document.Namespace val namespace: String = "shared_data",
    @Document.Id val id: String,
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val title: String,
    @Document.StringProperty val type: String,
    @Document.Score val score: Int = 0
)