package synchmanager.messagehandler.alive.interfaces

import apibuilder.synch.header.Header
import org.json.JSONArray

interface IAliveHandler {
    fun parse(header: Header)
}