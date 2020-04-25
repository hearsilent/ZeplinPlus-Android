package hearsilent.zeplin.extensions

import android.text.format.DateUtils

object LongExtension {

    fun Long.toYears(): Long {
        return this / DateUtils.DAY_IN_MILLIS / 365
    }

    fun Long.toMonths(): Long {
        return this / DateUtils.DAY_IN_MILLIS / 30
    }

    fun Long.toDays(): Long {
        return this / DateUtils.DAY_IN_MILLIS
    }

    fun Long.toHours(): Long {
        return this / DateUtils.HOUR_IN_MILLIS
    }

    fun Long.toMinutes(): Long {
        return this / DateUtils.MINUTE_IN_MILLIS
    }

}