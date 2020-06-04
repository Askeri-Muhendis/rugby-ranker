package dev.ricknout.rugbyranker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.ricknout.rugbyranker.core.api.WorldRugbyService
import dev.ricknout.rugbyranker.core.db.RankingDao
import dev.ricknout.rugbyranker.db.RugbyRankerDatabase
import dev.ricknout.rugbyranker.match.data.MatchRepository
import dev.ricknout.rugbyranker.news.data.NewsRepository
import dev.ricknout.rugbyranker.prediction.data.PredictionRepository
import dev.ricknout.rugbyranker.ranking.data.RankingRepository
import dev.ricknout.rugbyranker.ranking.prefs.RankingSharedPreferences
import dev.ricknout.rugbyranker.ranking.work.RankingWorkManager
import dev.ricknout.rugbyranker.theme.data.ThemeRepository
import dev.ricknout.rugbyranker.theme.prefs.ThemeSharedPreferences
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(WorldRugbyService.BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideService(retrofit: Retrofit): WorldRugbyService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RugbyRankerDatabase {
        return Room.databaseBuilder(
            context,
            RugbyRankerDatabase::class.java,
            RugbyRankerDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRankingDao(database: RugbyRankerDatabase): RankingDao {
        return database.rankingDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideRankingSharedPreferences(sharedPreferences: SharedPreferences): RankingSharedPreferences {
        return RankingSharedPreferences(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideThemeSharedPreferences(sharedPreferences: SharedPreferences): ThemeSharedPreferences {
        return ThemeSharedPreferences(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideRankingRepository(
        service: WorldRugbyService,
        dao: RankingDao,
        sharedPreferences: RankingSharedPreferences
    ): RankingRepository {
        return RankingRepository(service, dao, sharedPreferences)
    }

    @Provides
    @Singleton
    fun providePredictionRepository(dao: RankingDao): PredictionRepository {
        return PredictionRepository(dao)
    }

    @Provides
    @Singleton
    fun provideMatchRepository(
        service: WorldRugbyService,
        dao: RankingDao
    ): MatchRepository {
        return MatchRepository(service, dao)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(service: WorldRugbyService): NewsRepository {
        return NewsRepository(service)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(sharedPreferences: ThemeSharedPreferences): ThemeRepository {
        return ThemeRepository(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideRankingWorkManager(workManager: WorkManager): RankingWorkManager {
        return RankingWorkManager(workManager)
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "rugby_ranker_shared_preferences"
    }
}