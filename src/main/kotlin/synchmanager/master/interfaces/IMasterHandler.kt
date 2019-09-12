package synchmanager.master.interfaces

import apibuilder.synch.header.Header
import synchmanager.master.Master

interface IMasterHandler {
    fun addMaster(header: Header): Master?
    fun updateTimestamp(header: Header)
    fun switchMasterOnline(header: Header)
    fun switchMasterOffline(identification: String)
    fun checkIfDeviceIsSlave(ipAddress: String): Boolean
    fun addSlaveToNotConnectList(ipAddress: String)
    fun getAllMasters(): MutableList<Master>
    fun isThisAddressAlreadyKnownMaster(ipAddress: String): Boolean
}