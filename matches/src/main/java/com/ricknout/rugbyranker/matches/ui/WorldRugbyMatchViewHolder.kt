package com.ricknout.rugbyranker.matches.ui

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.core.util.DateUtils
import com.ricknout.rugbyranker.core.util.FlagUtils
import com.ricknout.rugbyranker.matches.R
import com.ricknout.rugbyranker.matches.vo.MatchHalf
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import kotlinx.android.synthetic.main.list_item_world_rugby_match.view.*

class WorldRugbyMatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(worldRugbyMatch: WorldRugbyMatch, predictable: Boolean, onPredictClick: (worldRugbyMatch: WorldRugbyMatch) -> Unit) {
        val showScores = worldRugbyMatch.status == MatchStatus.COMPLETE ||
                worldRugbyMatch.status == MatchStatus.LIVE ||
                worldRugbyMatch.status == MatchStatus.CANCELLED
        val showTime = worldRugbyMatch.status == MatchStatus.UNPLAYED ||
                worldRugbyMatch.status == MatchStatus.LIVE ||
                worldRugbyMatch.status == MatchStatus.POSTPONED ||
                worldRugbyMatch.status == MatchStatus.CANCELLED
        val showPredict = predictable && (worldRugbyMatch.status == MatchStatus.UNPLAYED ||
                worldRugbyMatch.status == MatchStatus.LIVE ||
                worldRugbyMatch.status == MatchStatus.POSTPONED)
        val firstTeamFlag = FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.firstTeamAbbreviation ?: "")
        val secondTeamFlag = FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.secondTeamAbbreviation ?: "")
        itemView.team1FlagTextView.text = firstTeamFlag
        itemView.team1TextView.text = worldRugbyMatch.firstTeamName
        itemView.team2FlagTextView.text = secondTeamFlag
        itemView.team2TextView.text = worldRugbyMatch.secondTeamName
        if (showScores) {
            itemView.team1ScoreTextView.isVisible = true
            itemView.team2ScoreTextView.isVisible = true
            itemView.team1ScoreTextView.text = worldRugbyMatch.firstTeamScore.toString()
            itemView.team2ScoreTextView.text = worldRugbyMatch.secondTeamScore.toString()
        } else {
            itemView.team1ScoreTextView.isVisible = false
            itemView.team2ScoreTextView.isVisible = false
        }
        if (showTime) {
            when(worldRugbyMatch.status) {
                MatchStatus.LIVE -> {
                    val minute = worldRugbyMatch.minute
                    val half = when (worldRugbyMatch.half) {
                        MatchHalf.FIRST_HALF -> itemView.context.getString(R.string.text_match_first_half)
                        MatchHalf.SECOND_HALF -> itemView.context.getString(R.string.text_match_second_half)
                        MatchHalf.HALF_TIME -> itemView.context.getString(R.string.text_match_half_time)
                        else -> null
                    }
                    itemView.timeTextView.text = if (minute != null && half != null) {
                        itemView.context.getString(R.string.text_match_minute_half, minute, half)
                    } else {
                        half
                    }
                    itemView.timeTextView.isVisible = true
                }
                MatchStatus.UNPLAYED -> {
                    val time = DateUtils.getDate(DateUtils.DATE_FORMAT_HH_MM, worldRugbyMatch.timeMillis)
                    itemView.timeTextView.text = time
                    itemView.timeTextView.isVisible = true
                }
                MatchStatus.POSTPONED -> {
                    val postponed = itemView.context.getString(R.string.text_postponed)
                    itemView.timeTextView.text = postponed
                    itemView.timeTextView.isVisible = true
                }
                MatchStatus.CANCELLED -> {
                    val cancelled = itemView.context.getString(R.string.text_cancelled)
                    itemView.timeTextView.text = cancelled
                    itemView.timeTextView.isVisible = true
                }
                else -> {
                    // Do nothing
                }
            }
        } else {
            itemView.timeTextView.text = null
            itemView.timeTextView.isVisible = false
        }
        itemView.eventTextView.text = worldRugbyMatch.eventLabel
        itemView.eventTextView.isVisible = worldRugbyMatch.eventLabel != null
        itemView.venueTextView.text = when {
            worldRugbyMatch.venueName != null && worldRugbyMatch.venueCity != null && worldRugbyMatch.venueCountry != null -> {
                itemView.context.getString(R.string.text_match_venue_three,
                        worldRugbyMatch.venueName, worldRugbyMatch.venueCity, worldRugbyMatch.venueCountry)
            }
            worldRugbyMatch.venueName != null && worldRugbyMatch.venueCity != null -> {
                itemView.context.getString(R.string.text_match_venue_two, worldRugbyMatch.venueName, worldRugbyMatch.venueCity)
            }
            worldRugbyMatch.venueName != null && worldRugbyMatch.venueCountry != null -> {
                itemView.context.getString(R.string.text_match_venue_two, worldRugbyMatch.venueName, worldRugbyMatch.venueCountry)
            }
            worldRugbyMatch.venueCity != null && worldRugbyMatch.venueCountry != null -> {
                itemView.context.getString(R.string.text_match_venue_two, worldRugbyMatch.venueCity, worldRugbyMatch.venueCountry)
            }
            worldRugbyMatch.venueName != null -> worldRugbyMatch.venueName
            worldRugbyMatch.venueCity != null -> worldRugbyMatch.venueCity
            worldRugbyMatch.venueCountry != null -> worldRugbyMatch.venueCountry
            else -> null
        }
        itemView.venueTextView.isVisible = worldRugbyMatch.venueName != null || worldRugbyMatch.venueCity != null || worldRugbyMatch.venueCountry != null
        if (showPredict) {
            itemView.predictButton.setOnClickListener {
                onPredictClick.invoke(worldRugbyMatch)
            }
            itemView.predictButton.isVisible = true
            itemView.constraintLayout.updatePaddingRelative(bottom = 0)
        } else {
            itemView.predictButton.setOnClickListener(null)
            if (showTime) {
                itemView.predictButton.isInvisible = true
                itemView.constraintLayout.updatePaddingRelative(bottom = 0)
            } else {
                itemView.predictButton.isVisible = false
                itemView.constraintLayout.updatePaddingRelative(bottom = itemView.context.resources.getDimensionPixelSize(R.dimen.spacing_double))
            }
        }
    }
}
