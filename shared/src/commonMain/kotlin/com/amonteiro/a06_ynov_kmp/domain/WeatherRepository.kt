package com.amonteiro.a06_ynov_kmp.domain

import com.amonteiro.a06_ynov_kmp.domain.model.Weather

interface WeatherRepository {

    suspend fun loadWeathers(cityName: String): List<Weather>
}