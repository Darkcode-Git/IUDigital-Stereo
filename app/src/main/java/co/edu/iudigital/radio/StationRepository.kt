package co.edu.iudigital.radio.data.repository

import co.edu.iudigital.radio.data.model.Station
import kotlinx.coroutines.flow.Flow

interface StationRepository {
    fun getStations(): Flow<List<Station>>
    suspend fun getStationById(id: String): Station?
}

class StationRepositoryImpl : StationRepository {
    override fun getStations(): Flow<List<Station>> {
        // In a real app, this would fetch from a remote API or local database
        return kotlinx.coroutines.flow.flow {
            emit(
                listOf(
                    Station(
                        id = "1",
                        name = "IU Digital Radio",
                        streamUrl = "https://example.com/stream1",
                        imageUrl = null,
                        description = "La mejor msica digital"
                    ),
                    Station(
                        id = "2",
                        name = "IU Rock",
                        streamUrl = "https://example.com/stream2",
                        imageUrl = null,
                        description = "Rock clsico y moderno"
                    ),
                    Station(
                        id = "3",
                        name = "IU Jazz",
                        streamUrl = "https://example.com/stream3",
                        imageUrl = null,
                        description = "Jazz contemporneo"
                    )
                )
            )
        }
    }

    override suspend fun getStationById(id: String): Station? {
        return getStations().firstOrNull()?.find { it.id == id }
    }
}
