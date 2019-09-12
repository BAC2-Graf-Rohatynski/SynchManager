package synchmanager

import error.ErrorClientRunner
import enumstorage.update.ApplicationName
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import synchmanager.command.CommandSocketHandler
import synchmanager.database.DatabaseManagerSocketHandler
import synchmanager.error.ErrorManagerSocketHandler
import synchmanager.messagehandler.MessageParser
import synchmanager.watchdog.AliveWatchdog
import synchmanager.watchdog.ConnectWatchdog

object SynchManagerRunner {
    private val logger: Logger = LoggerFactory.getLogger(SynchManagerRunner::class.java)

    @Volatile
    private var runApplication = true

    fun start() {
        logger.info("Starting application")
        ErrorClientRunner
        CommandSocketHandler
        DatabaseManagerSocketHandler
        ErrorManagerSocketHandler
        MessageParser
        ConnectWatchdog
        AliveWatchdog
    }

    @Synchronized
    fun isRunnable(): Boolean = runApplication

    fun stop() {
        logger.info("Stopping application")
        runApplication = false

        CommandSocketHandler.closeSockets()
        ErrorClientRunner.stop()
    }

    fun getUpdateInformation(): JSONObject = UpdateInformation.getAsJson(applicationName = ApplicationName.Synchronisation.name)
}