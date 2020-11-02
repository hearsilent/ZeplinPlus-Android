package hearsilent.zeplin.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageModel(val width: Int, val height: Int, val original_url: String)