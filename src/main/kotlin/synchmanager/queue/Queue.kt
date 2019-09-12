package synchmanager.queue

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import synchmanager.SynchManagerRunner
import synchmanager.queue.interfaces.IQueue
import java.lang.Exception

object Queue: IQueue {
    private val receivingQueue = mutableListOf<String>()
    private val logger: Logger = LoggerFactory.getLogger(Queue::class.java)
    private var isBlocked = false

    override fun putIntoQueue(message: String) {
        try {
            logger.info("Pushing message into queue ...")
            while (isBlocked) {
                Thread.sleep(5)
            }
            isBlocked = true
            receivingQueue.add(message)
            isBlocked = false
            logger.info("Put into queue")
        } catch (ex: Exception) {
            logger.error("Command queue error occurred while pushing! Clearing queue ...\n${ex.message}")
            receivingQueue.clear()
            isBlocked = false
        }
    }

    override fun takeFromQueue(): String {
        try {
            while (SynchManagerRunner.isRunnable()) {
                if (!isBlocked) {
                    if (receivingQueue.size > 0) {
                        receivingQueue.forEach { result ->
                            logger.info("Taking from queue: $result")
                            isBlocked = true
                            receivingQueue.remove(result)
                            isBlocked = false
                            return result
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            logger.error("Command queue error occurred while pulling! Clearing queue ...\n${ex.message}")
            receivingQueue.clear()
            isBlocked = false
        }

        return String()
    }
}