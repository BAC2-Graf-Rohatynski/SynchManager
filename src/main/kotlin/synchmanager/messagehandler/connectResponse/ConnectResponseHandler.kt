package synchmanager.messagehandler.connectResponse

import apibuilder.synch.header.Header
import enumstorage.master.MasterCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.MasterProperties
import synchmanager.command.CommandSocketHandler
import synchmanager.master.Master
import synchmanager.master.MasterHandler
import synchmanager.messagehandler.connectResponse.interfaces.IConnectResponseHandler

object ConnectResponseHandler: IConnectResponseHandler {
    private val logger: Logger = LoggerFactory.getLogger(ConnectResponseHandler::class.java)

    @Synchronized
    override fun parse(header: Header) {
        if (hasCorrectIdentification(header = header)) {
            val master = MasterHandler.addMaster(header = header) ?: return logger.info("Master '${header.identification}' added")
            sendResponseToUserUi(master = master)
        }
    }

    private fun hasCorrectIdentification(header: Header): Boolean = header.identification == MasterProperties.getIdentification()

    private fun sendResponseToUserUi(master: Master) {
        CommandSocketHandler.send(
                command = MasterCommand.Online,
                ipAddress = master.getIpAddress(),
                identification = master.getIdentification()
        )
    }
}