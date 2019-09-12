package synchmanager.database.interfaces

import org.json.JSONArray

interface IDatabaseManagerHandler {
    fun parse(message: JSONArray)
}