package com.ifpr.androidapptemplate.baseclasses

import java.time.LocalDate

data class Item(
    var endereco: String? = null,
    var titulo: String? = null,
    var descricao: String? = null,
    var data: String? = null,
    var categoria: String? = null,
    var quantidade: Int? = null,
    var autor: String? = null,
    var editora: String? = null,
    var preco: Double? = null,
    val base64Image: String? = null,
    val imageUrl: String? = null
)

