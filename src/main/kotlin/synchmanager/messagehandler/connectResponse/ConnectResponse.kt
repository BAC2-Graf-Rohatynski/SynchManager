package synchmanager.messagehandler.connectResponse

import apibuilder.synch.ConnectResponseItem
import interfacehelper.MyIpAddress
import propertystorage.MasterProperties
import synchmanager.messagehandler.connectResponse.interfaces.IConnectResponse
import synchmanager.udp.UdpHandler

class ConnectResponse: IConnectResponse {
    @Synchronized
    override fun message(): String = ConnectResponseItem()
            .create(
                    ipAddress = MyIpAddress.getAsString()!!,
                    identification = MasterProperties.getIdentification(),
                    port = UdpHandler.getPort())
            .toJson()
}