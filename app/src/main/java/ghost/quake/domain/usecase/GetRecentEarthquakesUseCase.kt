package ghost.quake.domain.usecase

import ghost.quake.domain.model.Earthquake
import ghost.quake.domain.repository.EarthquakeRepository
import javax.inject.Inject

class GetRecentEarthquakesUseCase @Inject constructor(
    private val repository: EarthquakeRepository
) {
    suspend operator fun invoke(): Result<List<Earthquake>> =
        repository.getRecentEarthquakes()
}