package synchmanager.messagehandler.connectResponse.interfaces

import apibuilder.synch.header.Header

interface IConnectResponseHandler {
    fun parse(header: Header)
}