package synchmanager.messagehandler.imAlive.interfaces

import apibuilder.synch.header.Header
import org.json.JSONArray

interface IImAliveHandler {
    fun parse(header: Header)
}