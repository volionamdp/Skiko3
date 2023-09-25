package com.volio.vn.models

interface RemoteData {
    val id: Long
    val idCategory: Long
    val name: String
    val remotePath: String
    val localPath: String
    val thumb: String
}