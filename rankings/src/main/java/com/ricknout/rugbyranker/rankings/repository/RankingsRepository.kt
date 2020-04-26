package com.ricknout.rugbyranker.rankings.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.core.util.DateUtils
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.rankings.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.rankings.prefs.RankingsSharedPreferences
import com.ricknout.rugbyranker.rankings.vo.RankingsDataConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankingsRepository(
    private val worldRugbyService: WorldRugbyService,
    private val worldRugbyRankingDao: WorldRugbyRankingDao,
    private val rankingsSharedPreferences: RankingsSharedPreferences
) {

    fun loadLatestWorldRugbyRankings(sport: Sport) = worldRugbyRankingDao.load(sport)

    fun loadLatestWorldRugbyRankingsTeamIds(sport: Sport) = worldRugbyRankingDao.loadTeamIds(sport)

    fun isInitialRankingsFetched(sport: Sport) =
            rankingsSharedPreferences.isInitialRankingsFetched(sport)

    suspend fun fetchAndCacheLatestWorldRugbyRankingsSync(sport: Sport): Boolean {
        val sports = when (sport) {
            Sport.MENS -> WorldRugbyService.SPORT_MENS
            Sport.WOMENS -> WorldRugbyService.SPORT_WOMENS
        }
        val date = getCurrentDate()
        return try {
            val worldRugbyRankingsResponse = worldRugbyService.getRankings(sports, date)
            val worldRugbyRankings = RankingsDataConverter.getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, sport)
            worldRugbyRankingDao.insert(worldRugbyRankings)
            rankingsSharedPreferences.setLatestWorldRugbyRankingsEffectiveTimeMillis(worldRugbyRankingsResponse.effective.millis, sport)
            rankingsSharedPreferences.setInitialRankingsFetched(sport, true)
            true
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false
        }
    }

    fun fetchAndCacheLatestWorldRugbyRankingsAsync(sport: Sport, coroutineScope: CoroutineScope, onComplete: (success: Boolean) -> Unit) {
        coroutineScope.launch {
            val success = withContext(Dispatchers.IO) { fetchAndCacheLatestWorldRugbyRankingsSync(sport) }
            onComplete(success)
        }
    }

    private fun getCurrentDate() = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT_YYYY_MM_DD)

    fun getLatestWorldRugbyRankingsEffectiveTimeMillis(sport: Sport): Long {
        return rankingsSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeMillis(sport)
    }

    fun getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport: Sport): LiveData<Long> {
        return rankingsSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport)
    }

    companion object {
        private const val TAG = "RankingsRepository"
    }
}
