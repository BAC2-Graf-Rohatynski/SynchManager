package synchmanager.database

import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import synchmanager.database.interfaces.IDatabaseManagerSocketHandler

object DatabaseManagerSocketHandler: IDatabaseManagerSocketHandler {
    private lateinit var commandSocket: DatabaseManagerSocket
    private val logger: Logger = LoggerFactory.getLogger(DatabaseManagerSocketHandler::class.java)

    init {
        connect()
    }

    @Synchronized
    override fun send(message: JSONArray) {
        if (::commandSocket.isInitialized) {
            commandSocket.send(message = message)
        } else {
            logger.error("Socket isn't initialized! Message '$message' cannot be send!")
        }
    }

    private fun connect() {
        try {
            logger.info("Connecting ...")
            commandSocket = DatabaseManagerSocket()
            logger.info("Connected")
        } catch (ex: Exception) {
            logger.error("Error occurred while connecting!\n${ex.message}")
        }
    }
}