package hearsilent.zeplin.libs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.preference.PreferenceManager
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

object Memory {
    private var appSharedPrefs: SharedPreferences? = null
    private var prefsEditor: SharedPreferences.Editor? = null

    @SuppressLint("CommitPrefEdits")
    fun init(context: Context?) {
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefsEditor = appSharedPrefs!!.edit()
    }

    fun getInt(context: Context?, key: String?, defValue: Int): Int {
        init(context)
        return appSharedPrefs!!.getInt(key, defValue)
    }

    fun setInt(context: Context?, key: String?, value: Int) {
        init(context)
        prefsEditor!!.putInt(key, value)
        prefsEditor!!.commit()
    }

    fun getLong(
        context: Context?,
        key: String?,
        defValue: Long
    ): Long {
        init(context)
        return appSharedPrefs!!.getLong(key, defValue)
    }

    fun setLong(
        context: Context?,
        key: String?,
        value: Long
    ) {
        init(context)
        prefsEditor!!.putLong(key, value)
        prefsEditor!!.commit()
    }

    fun getFloat(
        context: Context?,
        key: String?,
        defValue: Float
    ): Float {
        init(context)
        return appSharedPrefs!!.getFloat(key, defValue)
    }

    fun setFloat(
        context: Context?,
        key: String?,
        value: Float
    ) {
        init(context)
        prefsEditor!!.putFloat(key, value)
        prefsEditor!!.commit()
    }

    fun getString(
        context: Context?,
        key: String?,
        defValue: String?
    ): String? {
        init(context)
        return appSharedPrefs!!.getString(key, defValue)
    }

    fun setString(
        context: Context?,
        key: String?,
        data: String?
    ) {
        init(context)
        prefsEditor!!.putString(key, data)
        prefsEditor!!.commit()
    }

    fun getBoolean(
        context: Context?,
        key: String?,
        defValue: Boolean
    ): Boolean {
        init(context)
        return appSharedPrefs!!.getBoolean(key, defValue)
    }

    fun setBoolean(
        context: Context?,
        key: String?,
        data: Boolean
    ) {
        init(context)
        prefsEditor!!.putBoolean(key, data)
        prefsEditor!!.commit()
    }

    @Suppress("unchecked_cast")
    fun <T> getObject(
        context: Context?,
        key: String?,
        cls: Class<T>?
    ): T? {
        init(context)
        val json = appSharedPrefs!!.getString(key, null)
        return if (TextUtils.isEmpty(json)) {
            null as T
        } else try {
            val mapper = ObjectMapper()
            mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                .registerModule(KotlinModule())
            mapper.readValue(json, cls) as T
        } catch (ignore: Exception) {
            null as T
        }
    }

    fun setObject(
        context: Context?,
        key: String?,
        data: Any?
    ) {
        init(context)
        try {
            val json = ObjectMapper().writeValueAsString(data)
            prefsEditor!!.putString(key, json)
            prefsEditor!!.commit()
        } catch (ignore: Exception) {
        }
    }

    fun remove(context: Context?, key: String?) {
        init(context)
        prefsEditor!!.remove(key)
        prefsEditor!!.commit()
    }
}