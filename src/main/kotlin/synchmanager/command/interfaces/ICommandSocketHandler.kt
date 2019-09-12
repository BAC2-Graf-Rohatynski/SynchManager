package synchmanager.command.interfaces

import enumstorage.master.MasterCommand

interface ICommandSocketHandler {
    fun send(command: MasterCommand, ipAddress: String, identification: String)
    fun closeSockets()
}