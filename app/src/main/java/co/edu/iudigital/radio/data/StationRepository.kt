package co.edu.iudigital.radio.data

import co.edu.iudigital.radio.model.Station

interface StationRepository {
    fun getStations(): List<Station>
}

class InMemoryStationRepository : StationRepository {
    override fun getStations(): List<Station> = listOf(
        Station(1, "IU Digital Stereo", "Institucional & Noticias", "http://174.142.111.104:9974/;"),
        Station(2, "Emisora Cultural Antioquia", "Música & Cultura", "http://stream.cultural.co/live"),
        Station(3, "Radio Académica FM", "Conferencias & Podcast", "http://stream.academica.edu/fm"),
        Station(4, "UdeA Radio 100.9 FM", "Universitaria", "http://emisora.udea.edu.co/live")
    )
}
