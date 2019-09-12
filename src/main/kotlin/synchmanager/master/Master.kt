package synchmanager.master

class Master {
    private var ipAddress: String = String()
    private var identification: String = String()
    private var timestamp: Long = System.currentTimeMillis()
    private var port: Int = 0
    private var isOnline = false

    fun getIpAddress(): String = ipAddress
    fun setIpAddress(ipAddress: String): Master {
        this.ipAddress = ipAddress
        return this
    }

    fun getPort(): Int = port
    fun setPort(port: Int): Master {
        this.port = port
        return this
    }

    fun getIdentification(): String = identification
    fun setIdentification(identification: String): Master {
        this.identification = identification
        return this
    }

    fun getTimestamp(): Long = timestamp
    fun setTimestamp(): Master {
        this.timestamp = System.currentTimeMillis()
        return this
    }

    fun getOnlineState(): Boolean = isOnline
    fun setOnlineState(isOnline: Boolean): Master {
        this.isOnline = isOnline
        return this
    }
}