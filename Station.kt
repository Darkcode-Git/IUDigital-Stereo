package co.edu.iudigital.radio.data.model

data class Station(
    val id: String,
    val name: String,
    val streamUrl: String,
    val imageUrl: String?,
    val description: String
)
