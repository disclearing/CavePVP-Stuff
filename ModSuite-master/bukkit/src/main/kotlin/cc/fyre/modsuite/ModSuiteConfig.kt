package cc.fyre.modsuite

import cc.fyre.modsuite.util.JsonConfig
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ModSuiteConfig : JsonConfig() {

    var muted = false
    var slowTime = 0L
    var defaultSlowTime = 5000L

    var modModeOnJoin = true
    var modModeDisabled = false

}
