package synchmanager.master

import apibuilder.synch.header.Header
import enumstorage.master.MasterCommand
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.MasterProperties
import synchmanager.command.CommandSocketHandler
import synchmanager.master.interfaces.IMasterHandler
import java.lang.Exception

object MasterHandler: IMasterHandler {
    private val logger: Logger = LoggerFactory.getLogger(MasterHandler::class.java)
    private val masters = mutableListOf<Master>()
    private val slaves = mutableListOf<String>()

    @Synchronized
    override fun addMaster(header: Header): Master? {
        return try {
            if (header.identification != MasterProperties.getIdentification()) {
                if (isMasterAlreadyKnown(header = header)) {
                    add(header = header)
                } else {
                    throw Exception("Master ${header.identification} already added!")
                }
            } else {
                null
            }
        } catch (ex: Exception) {
            logger.error("Error occurred while adding remote master!\n${ex.message}")
            null
        }
    }

    @Synchronized
    override fun updateTimestamp(header: Header) {
        try {
            update(header = header)
        } catch (ex: Exception) {
            logger.error("Error occurred while removing remote master!\n${ex.message}")
        }
    }

    @Synchronized
    override fun switchMasterOnline(header: Header) {
        return try {
            masters.forEach { master ->
                if (master.getIdentification() == header.identification) {
                    if (!master.getOnlineState()) {
                        master.setOnlineState(isOnline = true)
                        logger.info("Master ${header.identification} set to online after a timeout")
                        sendResponseToUserUi(ipAddress = master.getIpAddress(), command = MasterCommand.OnlineAfterTimeout, identification = master.getIdentification())
                    }
                }
            }
        } catch (ex: Exception) {
            logger.error("Error occurred while switching remote master online!\n${ex.message}")
        }
    }

    @Synchronized
    override fun switchMasterOffline(identification: String) {
        return try {
            masters.forEach { master ->
                if (master.getIdentification() == identification) {
                    if (master.getOnlineState()) {
                        master.setOnlineState(isOnline = false)
                        sendResponseToUserUi(ipAddress = master.getIpAddress(), command = MasterCommand.Timeout, identification = master.getIdentification())
                        logger.info("Master $identification set to offline")
                    }
                }
            }
        } catch (ex: Exception) {
            logger.error("Error occurred while switching remote master offline!\n${ex.message}")
        }
    }

    @Synchronized
    override fun checkIfDeviceIsSlave(ipAddress: String): Boolean = slaves.contains(ipAddress)

    @Synchronized
    override fun addSlaveToNotConnectList(ipAddress: String) {
        if (!slaves.contains(ipAddress)) {
            slaves.add(ipAddress)
        }
    }

    @Synchronized
    override fun getAllMasters(): MutableList<Master> = masters

    @Synchronized
    override fun isThisAddressAlreadyKnownMaster(ipAddress: String): Boolean {
        masters.forEach { master ->
            if (master.getIpAddress() == ipAddress) {
                return true
            }
        }
        return false
    }

    private fun sendResponseToUserUi(ipAddress: String, command: MasterCommand, identification: String) {
        CommandSocketHandler.send(
                command = command,
                ipAddress = ipAddress,
                identification = identification
        )
    }

    private fun update(header: Header) {
        masters.forEach { master ->
            if (master.getIdentification() == header.identification) {
                master.setTimestamp()
                logger.info("Master '${header.identification}' updated!")
                return
            }
        }
    }

    private fun isMasterAlreadyKnown(header: Header): Boolean {
        masters.forEach { master ->
            if (master.getIdentification() == header.identification) {
                return true
            }
        }

        logger.error("Master '${header.identification}' already added!")
        return false
    }

    private fun add(header: Header): Master {
        val master = createMasterInstance(header = header)

        return if (masters.add(master)) {
            logger.info("Master '${header.identification}' is registered now")
            master
        } else {
            logger.error("Master '${header.identification}' CANNOT be registered!")
            master
        }
    }
    
    private fun createMasterInstance(header: Header): Master = Master()
            .setIdentification(header.identification!!)
            .setIpAddress(header.ipAddress)
            .setPort(header.port!!)
            .setTimestamp()
}