package synchmanager.error.interfaces

import org.json.JSONArray

interface IErrorManagerHandler {
    fun parse(message: JSONArray)
}