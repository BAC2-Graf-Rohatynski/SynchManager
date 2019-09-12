package synchmanager.messagehandler.connectRequest

import apibuilder.synch.ConnectRequestItem
import interfacehelper.MyIpAddress
import propertystorage.MasterProperties
import synchmanager.messagehandler.connectRequest.interfaces.IConnectRequest
import synchmanager.udp.UdpHandler

class ConnectRequest: IConnectRequest {
    @Synchronized
    override fun message(): String = ConnectRequestItem()
            .create(
                    ipAddress = MyIpAddress.getAsString()!!,
                    identification = MasterProperties.getIdentification(),
                    port = UdpHandler.getPort())
            .toJson()
}