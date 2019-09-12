package synchmanager.database.interfaces

import org.json.JSONArray

interface IDatabaseManagerSocketHandler {
    fun send(message: JSONArray)
}