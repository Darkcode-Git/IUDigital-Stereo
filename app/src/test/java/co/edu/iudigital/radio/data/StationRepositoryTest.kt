package co.edu.iudigital.radio.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StationRepositoryTest {

    private val repository = InMemoryStationRepository()

    @Test
    fun getStations_returnsExpectedDefaultStations() {
        val stations = repository.getStations()

        assertEquals(4, stations.size)
        assertEquals("IU Digital Stereo", stations.first().name)
        assertTrue(stations.all { it.streamUrl.isNotBlank() })
    }
}
