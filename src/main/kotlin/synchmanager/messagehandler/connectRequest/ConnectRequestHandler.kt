package synchmanager.messagehandler.connectRequest

import apibuilder.synch.header.Header
import synchmanager.messagehandler.connectRequest.interfaces.IConnectRequestHandler
import synchmanager.messagehandler.connectResponse.ConnectResponse
import synchmanager.udp.UdpHandler

object ConnectRequestHandler: IConnectRequestHandler {
    @Synchronized
    override fun parse(header: Header) = UdpHandler.sendSingleUdpMessage(
            buffer = ConnectResponse().message().toString().toByteArray(),
            ipAddress = header.ipAddress,
            port = header.port!!)
}