package synchmanager.messagehandler.imAlive

import apibuilder.synch.ImAliveItem
import interfacehelper.MyIpAddress
import propertystorage.MasterProperties
import synchmanager.messagehandler.imAlive.interfaces.IImAlive
import synchmanager.udp.UdpHandler

class ImAlive: IImAlive {
    @Synchronized
    override fun message(): String = ImAliveItem()
            .create(
                    ipAddress = MyIpAddress.getAsString()!!,
                    identification = MasterProperties.getIdentification(),
                    port = UdpHandler.getPort())
            .toJson()
}