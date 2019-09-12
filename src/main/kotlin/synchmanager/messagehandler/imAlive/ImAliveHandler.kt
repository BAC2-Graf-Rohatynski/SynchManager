package synchmanager.messagehandler.imAlive

import apibuilder.synch.header.Header
import synchmanager.master.MasterHandler
import synchmanager.messagehandler.imAlive.interfaces.IImAliveHandler

object ImAliveHandler: IImAliveHandler {
    @Synchronized
    override fun parse(header: Header) {
        MasterHandler.updateTimestamp(header = header)
        MasterHandler.switchMasterOnline(header = header)
    }
}