package synchmanager.udp.interfaces

import org.json.JSONArray
import java.net.InetAddress
import java.util.ArrayList

interface IUdpHandler {
    fun getPort(): Int
    fun sendSingleUdpMessage(buffer: ByteArray, ipAddress: String, port: Int)
    fun sendBroadcastMessageOverUdp(buffer: ByteArray)
    fun passMessageToAllMasters(message: JSONArray)
}