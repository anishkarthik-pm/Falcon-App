package com.kpn.falcon.di

import com.kpn.falcon.data.api.*
import com.kpn.falcon.data.repository.PropertyRepository
import com.kpn.falcon.data.repository.PropertyRepositoryImpl
import com.kpn.falcon.domain.usecase.*
import com.kpn.falcon.presentation.viewmodels.*
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

private const val BASE_URL = "https://api.kpnfalcon.com/v1"

val networkModule = module {
    single { KtorClientFactory.createKtorClient { get<SessionManager>().getToken() } }
    single<PropertyApiService> { PropertyApiServiceImpl(get(), BASE_URL) }
    single<AuthApiService> { AuthApiServiceImpl(get(), BASE_URL) }
}

// NetworkMonitor is platform-specific (see platformModule in androidMain/iosMain)

val repositoryModule = module {
    single<PropertyRepository> { PropertyRepositoryImpl(get()) }
}

val useCaseModule = module {
    factory { CalculateRentMetricsUseCase() }
    factory { AutoSuggestScoringUseCase() }
    factory { CalculateCompositeScoreUseCase() }
    factory { SLACountdownUseCase() }
    factory { PropertyIdGeneratorUseCase() }
    factory { KPNProximityCheckUseCase() }
    factory { DeviationCheckUseCase() }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { PropertiesViewModel(get()) }
    viewModel { AddPropertyViewModel(get(), get(), get(), get()) }
    viewModel { (propertyId: String) -> PropertyDetailViewModel(propertyId, get(), get(), get(), get()) }
    viewModel { NotificationsViewModel(get()) }
}

val sessionModule = module {
    single { SessionManager() }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            sessionModule,
            platformModule,
            networkModule,
            repositoryModule,
            useCaseModule,
            viewModelModule
        )
    }
}
