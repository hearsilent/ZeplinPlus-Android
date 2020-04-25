package hearsilent.zeplin.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectModel(
    val id: String,
    val name: String,
    val thumbnail: String,
    val platform: String,
    val status: String,
    val created: Long,
    val updated: Long,
    val number_of_screens: Int,
    val number_of_members: Int
)