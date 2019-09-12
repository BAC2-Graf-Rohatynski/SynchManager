package synchmanager.messagehandler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import synchmanager.messagehandler.connectResponse.ConnectResponseHandler
import synchmanager.messagehandler.imAlive.ImAliveHandler
import synchmanager.queue.Queue
import apibuilder.synch.header.HeaderBuilder
import enumstorage.synch.SynchRequest
import enumstorage.synch.SynchResponse
import synchmanager.SynchManagerRunner
import synchmanager.messagehandler.alive.AliveHandler
import synchmanager.messagehandler.connectRequest.ConnectRequestHandler
import synchmanager.messagehandler.message.SynchMessage
import java.lang.Exception
import kotlin.concurrent.thread

object MessageParser {
    private val logger: Logger = LoggerFactory.getLogger(MessageParser::class.java)

    init {
        parseIncomingRequests()
    }

    private fun parseIncomingRequests() {
        thread {
            while (SynchManagerRunner.isRunnable()) {
                try {
                    val message = Queue.takeFromQueue()
                    val header = HeaderBuilder().getHeader(message = message)
                    logger.info("Message '$message' taken from queue ...")

                    when (header.command) {
                        SynchRequest.ConnectRequest.name -> ConnectRequestHandler.parse(header = header)
                        SynchRequest.Alive.name -> AliveHandler.parse(header = header)
                        SynchResponse.ConnectResponse.name -> ConnectResponseHandler.parse(header = header)
                        SynchResponse.ImAlive.name -> ImAliveHandler.parse(header = header)
                        SynchRequest.SendDatabaseMessage.name -> SynchMessage.parse(message = message, header = header)
                        else -> logger.error("Invalid queue command '${header.command}' received!")
                    }
                } catch (ex: Exception) {
                    logger.error("Error occurred while parsing incoming message!\n${ex.message}")
                }
            }
        }
    }
}