package synchmanager.messagehandler.message.interfaces

import apibuilder.synch.header.Header

interface ISynchMessage {
    fun parse(message: String, header: Header)
}