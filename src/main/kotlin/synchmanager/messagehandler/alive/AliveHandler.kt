package synchmanager.messagehandler.alive

import apibuilder.synch.header.Header
import enumstorage.api.ApiValue
import enumstorage.master.MasterInformation
import org.json.JSONArray
import org.json.JSONObject
import synchmanager.messagehandler.alive.interfaces.IAliveHandler
import synchmanager.messagehandler.imAlive.ImAlive
import synchmanager.udp.UdpHandler

object AliveHandler: IAliveHandler {
    @Synchronized
    override fun parse(header: Header) = UdpHandler.sendSingleUdpMessage(
                buffer = ImAlive().message().toString().toByteArray(),
                ipAddress = header.ipAddress,
                port = header.port!!)
}