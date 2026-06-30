package com.amonteiro.a06_ynov_kmp.data.remote

import com.amonteiro.a06_ynov_kmp.BuildConfig
import com.amonteiro.a06_ynov_kmp.di.initKoin
import com.amonteiro.a06_ynov_kmp.domain.WeatherRepository
import com.amonteiro.a06_ynov_kmp.domain.model.Weather
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import org.koin.mp.KoinPlatform

//Suspend sera expliqué dans le chapitre des coroutines
suspend fun main() {

    initKoin()

    val apiDataSource = KoinPlatform.getKoin().get<WeatherApiDataSource>()
    val weathers = apiDataSource.loadWeathers("Nice")
    for (w in weathers) {
        println(w.getResume())
    }

}

class WeatherApiDataSource(val client : HttpClient) : WeatherRepository {

    companion object {
        private const val API_URL =
            "https://www.amonteiro.fr/api/weather?cityname="
    }

    override suspend fun loadWeathers(cityName: String): List<Weather> {
        val response = client.get("https://api.openweathermap.org/data/2.5/find?q=$cityName&appid=${BuildConfig.WEATHER_API_KEY}&units=metric&lang=fr")

        if (!response.status.isSuccess()) {
            throw Exception("Erreur API: ${response.status} - ${response.bodyAsText()}")
        }

        var result = response.body<WeatherResultDTO>()

        return result.list.map { w ->
            Weather(
                id = w.id,
                name = w.name,
                temp = w.main.temp,
                description = w.weather.firstOrNull()?.description ?: "-",
                speed = w.wind.speed,
                icon = "https://openweathermap.org/img/wn/${w.weather.firstOrNull()?.icon}@4x.png"
            )
        }
    }

    suspend fun loadWeathersVFacile(cityName: String): List<Weather> {
        val response = client.get(API_URL + cityName)
        if (!response.status.isSuccess()) {
            throw Exception("Erreur API: ${response.status} - ${response.bodyAsText()}")
        }

        var list = response.body<List<Weather>>()

        //Si je devais faire un Wrapper
        val listSortie = ArrayList<Weather>()
        for (w in list) {
            listSortie.add(
                Weather(
                    id = w.id,
                    name = w.name,
                    temp = w.temp,
                    description = w.description,
                    speed = w.speed,
                    icon = w.icon
                )
            )
        }

        return listSortie
    }
}

/* -------------------------------- */
// DTO
/* -------------------------------- */
@Serializable
data class WeatherResultDTO(
    val list: List<WeatherDTO>
)

@Serializable
data class WeatherDTO(
    val id: Int,
    val name: String,
    val main: TempDTO,
    val wind: WindDTO,
    val weather: List<DescriptionDTO>
)

@Serializable
data class TempDTO(val temp: Double)

@Serializable
data class WindDTO(var speed: Double)

@Serializable
data class DescriptionDTO(val icon: String, val description: String)