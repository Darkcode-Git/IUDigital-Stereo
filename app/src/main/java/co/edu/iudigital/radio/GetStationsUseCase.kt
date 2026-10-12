package co.edu.iudigital.radio.domain.usecase

import co.edu.iudigital.radio.data.model.Station
import co.edu.iudigital.radio.data.repository.StationRepository
import kotlinx.coroutines.flow.Flow

class GetStationsUseCase(private val repository: StationRepository) {
    operator fun invoke(): Flow<List<Station>> = repository.getStations()
}
