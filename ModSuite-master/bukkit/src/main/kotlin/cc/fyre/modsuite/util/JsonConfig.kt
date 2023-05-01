package cc.fyre.modsuite.util

import com.squareup.moshi.JsonClass
import java.io.File

@JsonClass(generateAdapter = true)
open class JsonConfig {

    @Transient lateinit var file: File

}