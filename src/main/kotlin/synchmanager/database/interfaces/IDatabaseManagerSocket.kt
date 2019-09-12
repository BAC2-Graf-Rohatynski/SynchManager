package synchmanager.database.interfaces

import org.json.JSONArray

interface IDatabaseManagerSocket {
    fun send(message: JSONArray)
}