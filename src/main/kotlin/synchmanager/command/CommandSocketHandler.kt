package synchmanager.command

import apibuilder.synch.SynchUiItem
import enumstorage.master.MasterCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.PortProperties
import synchmanager.SynchManagerRunner
import synchmanager.command.interfaces.ICommandSocketHandler
import java.net.ServerSocket
import kotlin.concurrent.thread

object CommandSocketHandler: ICommandSocketHandler {
    private lateinit var serverSocket: ServerSocket
    private lateinit var commandSocket: CommandSocket
    private val port: Int = PortProperties.getSynchPort()
    private val logger: Logger = LoggerFactory.getLogger(CommandSocketHandler::class.java)

    /**
        TODO: Connect on master network manager and do the same
     */

    init {
        thread {
            try {
                openSockets()
                acceptClients()
            } catch (ex: Exception) {
                logger.error("Error occurred while running socket handler!\n${ex.message}")
            } finally {
                closeSockets()
            }
        }
    }

    @Synchronized
    override fun send(command: MasterCommand, ipAddress: String, identification: String) {
        if (::commandSocket.isInitialized) {
            val message = SynchUiItem().create(command = command.toString(), ipAddress = ipAddress, identification = identification)
            commandSocket.send(message = message)
        } else {
            logger.warn("Command socket not initialized yet! Cannot send message!")
        }
    }

    private fun acceptClients() {
        while (SynchManagerRunner.isRunnable()) {
            try {
                logger.info("Waiting for clients ...")
                commandSocket = CommandSocket(clientSocket = serverSocket.accept())
                commandSocket.start()
                logger.info("Client added")
            } catch (ex: Exception) {
                logger.error("Error occurred while waiting for new clients!\n${ex.message}")
            }
        }
    }

    private fun openSockets() {
        logger.info("Starting server socket on port '$port'")
        serverSocket = ServerSocket(port)
        logger.info("Socket opened")
    }

    @Synchronized
    override fun closeSockets() {
        try {
            logger.info("Closing sockets ...")

            if (!serverSocket.isClosed) {
                serverSocket.close()
            }

            logger.info("Sockets closed")
        } catch (ex: Exception) {
            logger.error("Error occurred while closing socket!\n${ex.message}")
        }
    }
}