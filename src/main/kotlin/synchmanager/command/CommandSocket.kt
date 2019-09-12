package synchmanager.command

import apibuilder.synch.SynchUiItem
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import synchmanager.command.interfaces.ICommandSocket
import java.io.PrintWriter
import java.net.Socket
import java.lang.Exception

class CommandSocket(private val clientSocket: Socket): Thread(), ICommandSocket {
    private lateinit var printWriter: PrintWriter
    private val logger: Logger = LoggerFactory.getLogger(CommandSocket::class.java)

    /**
        TODO: Connect on master network manager and do the same
     */

    override fun run() {
        try {
            openSockets()
        } catch (ex: Exception) {
            logger.error("Error socket failure while running socket!\n${ex.message}")
            closeSockets()
        }
    }

    private fun openSockets() {
        logger.info("Opening sockets ...")
        printWriter = PrintWriter(clientSocket.getOutputStream(), true)
        logger.info("Sockets opened")
    }

    @Synchronized
    override fun send(message: SynchUiItem) {
        try {
            if (::printWriter.isInitialized) {
                printWriter.println(message.toJson())
                logger.info("Message '${message.toJson()}' sent")
            } else {
                throw Exception("Print writer isn't initialized yet!")
            }
        } catch (ex: Exception) {
            logger.error("Error while sending message: ${ex.message}")
        }
    }

    private fun closeSockets() {
        try {
            logger.info("Closing sockets ...")

            if (::printWriter.isInitialized) {
                printWriter.close()
            }

            if (!clientSocket.isClosed) {
                clientSocket.close()
            }
            logger.info("Sockets closed")
        } catch (ex: Exception) {
            logger.error("Error occurred while closing socketS!\n${ex.message}")
        }
    }
}