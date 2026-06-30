package com.amonteiro.a06_ynov_kmp.di

import com.amonteiro.a06_ynov_kmp.data.fake.WeatherApiFakeDataSource
import com.amonteiro.a06_ynov_kmp.data.remote.WeatherApiDataSource
import com.amonteiro.a06_ynov_kmp.domain.WeatherRepository
import com.amonteiro.a06_ynov_kmp.presentation.viewmodel.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module


fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(apiModule, viewModelModule)
    }
}
// Version pour iOS et Desktop
fun initKoin() = initKoin {}


val apiModule = module {

    single {
        HttpClient {
            install(Logging) {
                //(import io.ktor.client.plugins.logging.Logger)
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.INFO  // TRACE, HEADERS, BODY, etc.
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
            //engine { proxy = ProxyBuilder.http("monproxy:1234") }
        }
    }

   // single<WeatherRepository> { WeatherApiDataSource(get()) }
    singleOf(::WeatherApiDataSource) bind WeatherRepository::class
}

val apiFakeModule = module {
    singleOf(::WeatherApiFakeDataSource) bind WeatherRepository::class
}

val viewModelModule = module {
    viewModelOf(::MainViewModel)
}