package synchmanager.error.interfaces

import apibuilder.error.response.ResponseItem

interface IErrorManagerSocketHandler {
    fun send(response: ResponseItem)
}