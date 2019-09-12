package synchmanager.watchdog

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.WatchdogProperties
import synchmanager.SynchManagerRunner
import synchmanager.master.Master
import synchmanager.master.MasterHandler
import synchmanager.messagehandler.alive.Alive
import synchmanager.udp.UdpHandler
import kotlin.concurrent.thread

object AliveWatchdog {
    private val logger: Logger = LoggerFactory.getLogger(AliveWatchdog::class.java)
    private val timeout = WatchdogProperties.getAliveWatchdogTimeout()
    private val threshold = WatchdogProperties.getAliveWatchdogThreshold()

    init {
        watchdogSender()
        watchdogOverseer()
    }

    private fun watchdogSender() {
        thread {
            logger.info("Starting ALIVE watchdog sender ...")

            while (SynchManagerRunner.isRunnable()) {
                try {
                    logger.info("ALIVE Watchdog sending alive messages to all connected masters")
                    MasterHandler.getAllMasters().forEach { master ->

                        UdpHandler.sendSingleUdpMessage(
                                buffer = Alive().message().toByteArray(),
                                ipAddress = master.getIpAddress(),
                                port = master.getPort())
                    }
                    Thread.sleep(timeout)
                } catch (ex: Exception) {
                    logger.error("Error occurred in ALIVE Watchdog (Sender)\n${ex.message}")
                }
            }
        }
    }

    private fun watchdogOverseer() {
        thread {
            logger.info("Starting ALIVE watchdog overseer in ${threshold / 1000}s ...")
            Thread.sleep(threshold)
            logger.info("ALIVE Watchdog overseer started")

            while (SynchManagerRunner.isRunnable()) {
                try {
                    logger.info("Watchdog checking all connected masters")
                    val masterToSwitchOffline = mutableListOf<Master>()

                    MasterHandler.getAllMasters().forEach { master ->
                        if (master.getOnlineState()) {
                            if ((System.currentTimeMillis() - master.getTimestamp()) > threshold) {
                                logger.warn("Master ${master.getIdentification()} is NOT reachable! Connection will be disabled!")
                                masterToSwitchOffline.add(master)
                            }
                        }
                    }

                    masterToSwitchOffline.forEach { master ->
                        MasterHandler.switchMasterOffline(identification = master.getIdentification())
                    }
                    Thread.sleep(timeout)
                } catch (ex: Exception) {
                    logger.error("Error occurred in ALIVE Watchdog (Overseer)\n${ex.message}")
                }
            }
        }
    }
}