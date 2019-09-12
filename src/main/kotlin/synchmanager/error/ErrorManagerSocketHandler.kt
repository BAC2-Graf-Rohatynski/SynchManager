package synchmanager.error

import apibuilder.error.response.ResponseItem
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import synchmanager.error.interfaces.IErrorManagerSocketHandler

/**
 * This handler manages the error manager
 *
 * @author      Markus Graf
 * @see         java.net.ServerSocket
 */
object ErrorManagerSocketHandler: IErrorManagerSocketHandler {
    private lateinit var commandSocket: ErrorManagerSocket
    private val logger: Logger = LoggerFactory.getLogger(ErrorManagerSocketHandler::class.java)

    init {
        connect()
    }

    @Synchronized
    override fun send(response: ResponseItem) {
        if (ErrorManagerSocketHandler::commandSocket.isInitialized) {
            commandSocket.send(message = response.getOriginalMessage())
        } else {
            logger.error("Socket isn't initialized! Message '${response.getOriginalMessage()}' cannot be send!")
        }
    }

    private fun connect() {
        try {
            logger.info("Connecting ...")
            commandSocket = ErrorManagerSocket()
            logger.info("Connected")
        } catch (ex: Exception) {
            logger.error("Error occurred while connecting!\n${ex.message}")
        }
    }
}