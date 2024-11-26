package ghost.quake.data.remote.mapper

import ghost.quake.data.remote.dto.EarthquakeDto
import ghost.quake.domain.model.Earthquake
import javax.inject.Inject

class EarthquakeMapper @Inject constructor() {
    fun toDomain(dto: EarthquakeDto): Earthquake {
        return Earthquake(
            id = "${dto.date}${dto.hour}${dto.magnitude}",
            date = dto.date,
            hour = dto.hour,
            place = dto.place,
            magnitude = dto.magnitude.toDoubleOrNull() ?: 0.0,
            depth = dto.depth.replace(" km", "").toIntOrNull() ?: 0,
            latitude = dto.latitude.toDoubleOrNull() ?: 0.0,
            longitude = dto.longitude.toDoubleOrNull() ?: 0.0,
            image = dto.image,
            info = dto.info
        )
    }
}