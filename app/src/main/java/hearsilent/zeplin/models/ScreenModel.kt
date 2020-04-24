package hearsilent.zeplin.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ScreenModel(
    val id: String,
    val created: Long,
    val updated: Long,
    val name: String,
    val image: ImageModel,
    val number_of_versions: Int,
    val number_of_notes: Int
)