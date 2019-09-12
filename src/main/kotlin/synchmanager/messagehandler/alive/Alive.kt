package synchmanager.messagehandler.alive

import apibuilder.synch.AliveRequestItem
import interfacehelper.MyIpAddress
import propertystorage.MasterProperties
import synchmanager.messagehandler.alive.interfaces.IAlive
import synchmanager.udp.UdpHandler

class Alive: IAlive {
    @Synchronized
    override fun message(): String = AliveRequestItem()
            .create(
                    ipAddress = MyIpAddress.getAsString()!!,
                    identification = MasterProperties.getIdentification(),
                    port = UdpHandler.getPort())
            .toJson()
}