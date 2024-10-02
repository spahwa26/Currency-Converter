package com.app.currencyconverter.data.network

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.currencyconverter.data.repository.LocalRepository
import com.app.currencyconverter.data.repository.RemoteRepository
import com.app.currencyconverter.utils.ResultWrapper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CurrencyUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        var updatesCount = localRepository.getUpdatesCount()

        /*
         * This implementation calls the currency names API every 1-2 months, depending on network availability.
         * The rates API is called hourly, and the names API is triggered every 720 rate API calls (approximately 30 days).
         * Since network may not always be available, the names API may actually run every 1-2 months, which is acceptable
         * as currency names don't change frequently.
         */
        val res =
            if ((updatesCount ?: 0) >= 720) {
                updatesCount = 1
                remoteRepository.updateCurrencyData()
            } else {
                updatesCount = (updatesCount ?: 0) + 1
                remoteRepository.updateCurrencyData(false)
            }

        return when (res) {
            is ResultWrapper.Success -> {
                localRepository.updateTheCount(updatesCount)
                Result.success()
            }

            is ResultWrapper.Error -> {
                Result.retry()
            }
        }

    }
}