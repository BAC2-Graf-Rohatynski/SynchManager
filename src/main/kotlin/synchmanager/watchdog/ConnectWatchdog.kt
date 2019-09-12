package synchmanager.watchdog

import interfacehelper.GetAllIpAddresses
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.PortProperties
import propertystorage.WatchdogProperties
import synchmanager.SynchManagerRunner
import synchmanager.master.MasterHandler
import synchmanager.messagehandler.connectRequest.ConnectRequest
import synchmanager.udp.UdpHandler
import kotlin.concurrent.thread

object ConnectWatchdog {
    private val logger: Logger = LoggerFactory.getLogger(ConnectWatchdog::class.java)
    private val timeout = WatchdogProperties.getConnectWatchdogTimeout()
    private val port = PortProperties.getUdpPort()

    init {
        watchdogSender()
    }

    private fun watchdogSender() {
        thread {
            logger.info("Starting CONNECT watchdog sender ...")

            while (SynchManagerRunner.isRunnable()) {
                try {
                    logger.info("CONNECT Watchdog sending alive messages to all non-connected devices in network ...")
                    GetAllIpAddresses.get().forEach { host ->
                        if (!MasterHandler.isThisAddressAlreadyKnownMaster(ipAddress = host.hostAddress) &&
                                !MasterHandler.checkIfDeviceIsSlave(ipAddress = host.hostAddress)) {

                            UdpHandler.sendSingleUdpMessage(
                                    buffer = ConnectRequest().message().toByteArray(),
                                    ipAddress = host.hostAddress,
                                    port = port)
                        }
                    }

                    Thread.sleep(timeout)
                } catch (ex: Exception) {
                    logger.error("Error occurred in CONNECT watchdog (Sender)\n${ex.message}")
                }
            }
        }
    }
}