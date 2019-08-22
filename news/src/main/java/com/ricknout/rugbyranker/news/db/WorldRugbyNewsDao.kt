package com.ricknout.rugbyranker.news.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ricknout.rugbyranker.news.vo.WorldRugbyArticle

@Dao
interface WorldRugbyNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(worldRugbyArticles: List<WorldRugbyArticle>)

    @Query("SELECT * FROM world_rugby_articles ORDER BY timeMillis DESC")
    fun load(): DataSource.Factory<Int, WorldRugbyArticle>
}
