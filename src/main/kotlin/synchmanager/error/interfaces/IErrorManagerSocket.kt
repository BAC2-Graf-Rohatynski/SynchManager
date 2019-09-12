package synchmanager.error.interfaces

import org.json.JSONArray

interface IErrorManagerSocket {
    fun send(message: JSONArray)
}