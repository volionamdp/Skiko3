package com.volio.vn.usecases

import com.volio.vn.models.BitcoinModel
import com.volio.vn.repositories.BitcoinRepository
import javax.inject.Inject

class BitcoinUseCase @Inject constructor(
    private val repo: BitcoinRepository
) : BaseUseCase() {

    suspend fun getBitcoinPrices(): List<BitcoinModel> {
        return runWithCheckException {
            if (repo.getBitcoinHistoryRemote().isEmpty()) {
                repo.getBitcoinHistoryInLocal()
            } else {
                repo.getBitcoinHistoryRemote()
            }
        } ?: emptyList()
    }

    suspend fun getBitcoinPricesByResult(): Result<List<BitcoinModel>> {
        return runWithCheckExceptionByResult {
            if (repo.getBitcoinHistoryRemote().isEmpty()) {
                repo.getBitcoinHistoryInLocal()
            } else {
                repo.getBitcoinHistoryRemote()
            }
        }
    }
}


