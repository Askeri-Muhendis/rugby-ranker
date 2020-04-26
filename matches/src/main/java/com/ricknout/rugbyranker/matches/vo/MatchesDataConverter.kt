package com.ricknout.rugbyranker.matches.vo

import com.ricknout.rugbyranker.core.api.Match
import com.ricknout.rugbyranker.core.api.WorldRugbyMatchSummaryResponse
import com.ricknout.rugbyranker.core.api.WorldRugbyMatchesResponse
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.core.util.DateUtils
import com.ricknout.rugbyranker.core.vo.Sport
import java.lang.IllegalArgumentException

object MatchesDataConverter {

    fun getWorldRugbyMatchesFromWorldRugbyMatchesResponse(worldRugbyMatchesResponse: WorldRugbyMatchesResponse, sport: Sport): List<WorldRugbyMatch> {
        return worldRugbyMatchesResponse.content.map { match ->
            WorldRugbyMatch(
                    matchId = match.matchId,
                    description = match.description,
                    status = getMatchStatusFromMatch(match),
                    attendance = match.attendance,
                    firstTeamId = match.teams[0].id,
                    firstTeamName = match.teams[0].name,
                    firstTeamAbbreviation = match.teams[0].abbreviation,
                    firstTeamScore = match.scores[0],
                    secondTeamId = match.teams[1].id,
                    secondTeamName = match.teams[1].name,
                    secondTeamAbbreviation = match.teams[1].abbreviation,
                    secondTeamScore = match.scores[1],
                    timeLabel = match.time.label,
                    timeMillis = match.time.millis,
                    timeGmtOffset = match.time.gmtOffset.toInt(),
                    venueId = match.venue?.id,
                    venueName = match.venue?.name,
                    venueCity = match.venue?.city,
                    venueCountry = match.venue?.country,
                    eventId = match.events.firstOrNull()?.id,
                    eventLabel = match.events.firstOrNull()?.label,
                    eventSport = sport,
                    eventRankingsWeight = match.events.firstOrNull()?.rankingsWeight,
                    eventStartTimeLabel = match.events.firstOrNull()?.start?.label,
                    eventStartTimeMillis = match.events.firstOrNull()?.start?.millis,
                    eventStartTimeGmtOffset = match.events.firstOrNull()?.start?.gmtOffset?.toInt(),
                    eventEndTimeLabel = match.events.firstOrNull()?.end?.label,
                    eventEndTimeMillis = match.events.firstOrNull()?.end?.millis,
                    eventEndTimeGmtOffset = match.events.firstOrNull()?.end?.gmtOffset?.toInt(),
                    half = getMatchHalfFromMatch(match),
                    minute = null
            )
        }
    }

    private fun getMatchStatusFromMatch(match: Match): MatchStatus {
        return when (match.status) {
            WorldRugbyService.STATE_UNPLAYED, WorldRugbyService.STATE_UNPLAYED_2 -> MatchStatus.UNPLAYED
            WorldRugbyService.STATE_COMPLETE, WorldRugbyService.STATE_COMPLETE_2 -> MatchStatus.COMPLETE
            WorldRugbyService.STATE_LIVE_1ST_HALF, WorldRugbyService.STATE_LIVE_2ND_HALF, WorldRugbyService.STATE_LIVE_HALF_TIME -> MatchStatus.LIVE
            else -> throw IllegalArgumentException("Unknown match status ${match.status}")
        }
    }

    private fun getMatchHalfFromMatch(match: Match): MatchHalf? {
        return when (match.status) {
            WorldRugbyService.STATE_LIVE_1ST_HALF -> MatchHalf.FIRST_HALF
            WorldRugbyService.STATE_LIVE_2ND_HALF -> MatchHalf.SECOND_HALF
            WorldRugbyService.STATE_LIVE_HALF_TIME -> MatchHalf.HALF_TIME
            else -> null
        }
    }

    fun getMinuteFromWorldRugbyMatchSummaryResponse(worldRugbyMatchSummaryResponse: WorldRugbyMatchSummaryResponse): Int? {
        return if (worldRugbyMatchSummaryResponse.match.clock != null) {
            worldRugbyMatchSummaryResponse.match.clock!!.secs / DateUtils.MINUTE_SECS
        } else {
            null
        }
    }
}
