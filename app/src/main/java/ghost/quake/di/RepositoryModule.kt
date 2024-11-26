package ghost.quake.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ghost.quake.data.remote.api.EarthquakeApi
import ghost.quake.data.remote.mapper.EarthquakeMapper
import ghost.quake.data.repository.EarthquakeRepositoryImpl
import ghost.quake.domain.repository.EarthquakeRepository
import ghost.quake.domain.usecase.GetRecentEarthquakesUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideEarthquakeRepository(
        api: EarthquakeApi,
        mapper: EarthquakeMapper
    ): EarthquakeRepository {
        return EarthquakeRepositoryImpl(api, mapper)
    }

    @Provides
    @Singleton
    fun provideGetRecentEarthquakesUseCase(
        repository: EarthquakeRepository
    ): GetRecentEarthquakesUseCase {
        return GetRecentEarthquakesUseCase(repository)
    }
}