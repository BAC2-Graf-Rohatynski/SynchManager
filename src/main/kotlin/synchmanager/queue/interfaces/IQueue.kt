package synchmanager.queue.interfaces


interface IQueue {
    fun putIntoQueue(message: String)
    fun takeFromQueue(): String
}