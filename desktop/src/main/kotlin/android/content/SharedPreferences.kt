package android.content

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Desktop stand-in for android.content.SharedPreferences — persists to a JSON file
 * under ~/.spanda/. Supports the subset used by AppSettings (strings + string sets).
 */
class SharedPreferences(private val name: String) {

    private val file: File = File(File(System.getProperty("user.home"), ".spanda"), "$name.json")
    private var data: JSONObject = load()

    private fun load(): JSONObject = try {
        if (file.exists()) JSONObject(file.readText()) else JSONObject()
    } catch (e: Exception) {
        JSONObject()
    }

    private fun save() {
        file.parentFile?.mkdirs()
        file.writeText(data.toString())
    }

    fun getString(key: String, defValue: String?): String? =
        if (data.has(key)) data.getString(key) else defValue

    fun getStringSet(key: String, defValue: Set<String>?): Set<String>? {
        if (!data.has(key)) return defValue
        val arr = data.getJSONArray(key)
        return (0 until arr.length()).map { arr.getString(it) }.toSet()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean =
        if (data.has(key)) data.getBoolean(key) else defValue

    fun getInt(key: String, defValue: Int): Int =
        if (data.has(key)) data.getInt(key) else defValue

    fun edit(): Editor = Editor()

    inner class Editor {
        fun putString(key: String, value: String): Editor {
            data.put(key, value)
            return this
        }

        fun putStringSet(key: String, value: Set<String>): Editor {
            data.put(key, JSONArray(value.toList()))
            return this
        }

        fun putBoolean(key: String, value: Boolean): Editor {
            data.put(key, value)
            return this
        }

        fun putInt(key: String, value: Int): Editor {
            data.put(key, value)
            return this
        }

        fun remove(key: String): Editor {
            data.remove(key)
            return this
        }

        fun apply() = save()

        fun commit(): Boolean {
            save()
            return true
        }
    }
}
