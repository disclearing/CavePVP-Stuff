package cc.fyre.modsuite.mod.packet

import cc.fyre.proton.pidgin.packet.Packet
import com.google.gson.JsonObject
import org.bukkit.entity.Player
import java.util.UUID

class PidginNiggerPacket() : Packet {

    var port = 0
    lateinit var player: UUID
    lateinit var sender: UUID

    constructor(port: Int,player: UUID,sender: UUID):this() {
        this.port = port
        this.player = player
        this.sender = sender
    }

    override fun id(): Int {
        return 182341283
    }

    override fun serialize(): JsonObject {
        return JsonObject().apply{
            this.addProperty("port",this@PidginNiggerPacket.port)
            this.addProperty("player",this@PidginNiggerPacket.player.toString())
            this.addProperty("sender",this@PidginNiggerPacket.sender.toString())
        }
    }

    override fun deserialize(data: JsonObject) {
        this.port = data.get("player").asInt
        this.player = UUID.fromString(data.get("player").asString)
        this.sender = UUID.fromString(data.get("sender").asString)
    }

}