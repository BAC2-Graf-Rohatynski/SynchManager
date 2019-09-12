package synchmanager.udp

import apibuilder.synch.SynchMessageItem
import interfacehelper.GetAllIpAddresses
import interfacehelper.MyIpAddress
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.PortProperties
import synchmanager.SynchManagerRunner
import synchmanager.master.MasterHandler
import synchmanager.queue.Queue
import synchmanager.udp.interfaces.IUdpHandler
import java.lang.Exception
import java.net.*
import kotlin.concurrent.thread

object UdpHandler: IUdpHandler {
    private val socket = DatagramSocket()
    private val logger: Logger = LoggerFactory.getLogger(UdpHandler::class.java)
    private val outgoingInterface = MyIpAddress.getMyInterface()
    private val outgoingPort = PortProperties.getUdpPort()

    init {
        try {
            logger.info("Starting UDP handler ...")
            hearOnUdpSocketForMasters()
        } catch (ex: Exception) {
            logger.error("Error occurred while running UDP handler!\n${ex.message}")
        }
    }

    @Synchronized
    override fun getPort(): Int = outgoingPort

    @Synchronized
    override fun passMessageToAllMasters(message: JSONArray) {
        MasterHandler.getAllMasters().forEach { master ->
            val buffer = SynchMessageItem().create(ipAddress = MyIpAddress.getAsString()!!, message = message).toJson()

            sendSingleUdpMessage(
                    ipAddress = master.getIpAddress(),
                    port = master.getPort(),
                    buffer = buffer.toByteArray())
        }
    }

    @Synchronized
    override fun sendSingleUdpMessage(buffer: ByteArray, ipAddress: String, port: Int) {
        logger.info("Trying to send watchdog message to $ipAddress:$port ...")
        val inetAddress = getIpAddressByHost(host = ipAddress)

        if (inetAddress != null) {
            sendUdpPacket(buffer = buffer, inetAddress = inetAddress, port = port)
        } else {
            logger.error("Message '$buffer' CANNOT be sent due to InetAddress cannot be requested!")
        }
    }

    @Synchronized
    override fun sendBroadcastMessageOverUdp(buffer: ByteArray) {
        GetAllIpAddresses.get().forEach { ipAddress ->
            sendUdpPacket(buffer = buffer, inetAddress = ipAddress, port = outgoingPort)
        }
    }

    private fun sendUdpPacket(buffer: ByteArray, inetAddress: InetAddress, port: Int) {
        try {
            logger.info("Sending message to ${inetAddress.hostName}")
            val packet = DatagramPacket(buffer, buffer.size, inetAddress, port)
            socket.send(packet)
        } catch (ex: Exception) {
            logger.error("Error occurred while sending UDP packet!\n${ex.message}")
        }
    }

    private fun getIpAddressByHost(host: String): InetAddress? {
        return try {
            InetAddress.getByName(host)
        } catch (ex: Exception) {
            logger.error("Error occurred while getting host IP address!\n${ex.message}")
            null
        }
    }

    private fun decodeReceivedMessage(packet: DatagramPacket): String = String(bytes = packet.data, offset = 0, length = packet.length)

    private fun hearOnUdpSocketForMasters() {
        val myIpAddress = MyIpAddress.getAsInetAddress() ?: throw Exception("Interface for network $outgoingInterface not found!")

        thread {
            val buffer = ByteArray(size = 1024)
            val socket = DatagramSocket(outgoingPort, myIpAddress)
            val packet = DatagramPacket(buffer, buffer.size)
            logger.info("Hearing for other masters ...")

            while (SynchManagerRunner.isRunnable()) {
                try {
                    socket.receive(packet)
                    val message = decodeReceivedMessage(packet = packet)
                    logger.info("Synch message $message received")
                    Queue.putIntoQueue(message = message)
                } catch (ex: Exception) {
                    logger.error("Error occurred while hearing for other masters!\n${ex.message}")
                }
            }
        }
    }
}