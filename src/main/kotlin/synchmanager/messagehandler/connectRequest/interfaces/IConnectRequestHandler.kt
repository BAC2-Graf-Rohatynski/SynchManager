package synchmanager.messagehandler.connectRequest.interfaces

import apibuilder.synch.header.Header
import org.json.JSONArray

interface IConnectRequestHandler {
    fun parse(header: Header)
}