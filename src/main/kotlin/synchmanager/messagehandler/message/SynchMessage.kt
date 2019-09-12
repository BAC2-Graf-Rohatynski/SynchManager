package synchmanager.messagehandler.message

import apibuilder.synch.SynchMessageItem
import apibuilder.synch.header.Header
import synchmanager.database.DatabaseManagerSocketHandler
import synchmanager.messagehandler.message.interfaces.ISynchMessage

object SynchMessage: ISynchMessage {
    @Synchronized
    override fun parse(message: String, header: Header) {
        val databaseMessage = SynchMessageItem().toObject(message = message)
        DatabaseManagerSocketHandler.send(message = databaseMessage.databaseMessage)
    }
}