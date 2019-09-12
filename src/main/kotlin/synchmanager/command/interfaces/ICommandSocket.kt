package synchmanager.command.interfaces

import apibuilder.synch.SynchUiItem

interface ICommandSocket {
    fun send(message: SynchUiItem)
}