package synchmanager.error

import apibuilder.error.response.ResponseItem
import apibuilder.json.Json
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.PortProperties
import synchmanager.SynchManagerRunner
import synchmanager.error.interfaces.IErrorManagerSocket
import synchmanager.udp.UdpHandler
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.lang.Exception
import kotlin.concurrent.thread

class ErrorManagerSocket: IErrorManagerSocket {
    private lateinit var clientSocket: Socket
    private lateinit var printWriter: PrintWriter
    private lateinit var bufferedReader: BufferedReader
    private val logger: Logger = LoggerFactory.getLogger(ErrorManagerSocket::class.java)

    init {
        try {
            openSockets()
            receive()
        } catch (ex: Exception) {
            logger.error("Error occurred while running socket!\n${ex.message}")
            closeSockets()
        }
    }

    private fun openSockets() {
        logger.info("Opening sockets ...")
        clientSocket = Socket("127.0.0.1", PortProperties.getErrorPort())
        printWriter = PrintWriter(clientSocket.getOutputStream(), true)
        bufferedReader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        logger.info("Sockets opened")
    }

    @Synchronized
    override fun send(message: JSONArray) {
        try {
            if (::printWriter.isInitialized) {
                printWriter.println(Json.replaceUnwantedChars(stringObject = message.toString()))
                logger.info("Message '$message' sent")
            } else {
                throw Exception("Print writer isn't initialized yet!")
            }
        } catch (ex: Exception) {
            logger.error("Error while sending message!\n${ex.message}")
        }
    }

    private fun receive() {
        thread {
            bufferedReader.use {
                try {
                    while (SynchManagerRunner.isRunnable()) {
                        val message = bufferedReader.readLine()
                        logger.info("Message '$message' received")
                        val response = ResponseItem().toObject(message = message)

                        if (!response.isGetMessage && !response.isResponse) {
                            UdpHandler.passMessageToAllMasters(message = response.getOriginalMessage())
                        }
                    }
                } catch (ex: Exception) {
                    logger.error("Error while receiving messages!\n${ex.message}")
                }
            }
        }
    }

    private fun closeSockets() {
        try {
            logger.info("Closing sockets ...")

            if (::printWriter.isInitialized) {
                printWriter.close()
            }

            if (::clientSocket.isInitialized) {
                clientSocket.close()
            }

            if (::bufferedReader.isInitialized) {
                bufferedReader.close()
            }

            logger.info("Sockets closed")
        } catch (ex: Exception) {
            logger.error("Error occurred while closing sockets!\n${ex.message}")
        }
    }
}