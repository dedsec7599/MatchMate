package com.harshvardhan.matchmate

import kotlin.jvm.java
import android.app.Application
import androidx.room.Room
import com.harshvardhan.matchmate.data.local.MatchDatabase
import com.harshvardhan.matchmate.data.remote.RandomUserApi
import com.harshvardhan.matchmate.data.repos.MatchRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MatchMateApp : Application() {

    lateinit var database: MatchDatabase
        private set

    lateinit var repository: MatchRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            MatchDatabase::class.java,
            "matchmate.db"
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://randomuser.me/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

        val api =
            retrofit.create(
                RandomUserApi::class.java
            )

        repository = MatchRepository(
            api = api,
            database = database
        )
    }
}