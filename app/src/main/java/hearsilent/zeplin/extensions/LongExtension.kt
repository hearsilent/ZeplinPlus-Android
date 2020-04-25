package hearsilent.zeplin.extensions

import android.content.Context
import android.text.format.DateUtils
import hearsilent.zeplin.R

object LongExtension {

    fun Long.toYears(): Long {
        return this / DateUtils.DAY_IN_MILLIS / 365
    }

    fun Long.toMonths(): Long {
        return this / DateUtils.DAY_IN_MILLIS / 30
    }

    fun Long.toWeeks(): Long {
        return this / DateUtils.DAY_IN_MILLIS / 7
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

    fun Long.toDuration(context: Context): String {
        val duration = System.currentTimeMillis() - this
        return when {
            duration.toYears() > 0 -> {
                context.getString(R.string.duration_year, duration.toYears())
            }
            duration.toMonths() > 0 -> {
                context.getString(R.string.duration_month, duration.toMonths())
            }
            duration.toWeeks() > 0 -> {
                context.getString(R.string.duration_week, duration.toWeeks())
            }
            duration.toDays() > 0 -> {
                context.getString(R.string.duration_day, duration.toDays())
            }
            duration.toHours() > 0 -> {
                context.getString(R.string.duration_hour, duration.toHours())
            }
            duration.toMinutes() > 0 -> {
                context.getString(R.string.duration_minute, duration.toMinutes())
            }
            else -> {
                context.getString(R.string.duration_now)
            }
        }
    }

}