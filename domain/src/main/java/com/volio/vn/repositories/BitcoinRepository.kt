package com.volio.vn.repositories

import com.volio.vn.models.BitcoinModel

interface BitcoinRepository {

    suspend fun getBitcoinHistoryInLocal(): List<BitcoinModel>

    suspend fun getBitcoinHistoryRemote(): List<BitcoinModel>
}