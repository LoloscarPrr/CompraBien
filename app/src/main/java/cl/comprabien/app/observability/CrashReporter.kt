package cl.comprabien.app.observability

import cl.comprabien.app.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashReporter {
    fun record(error: Throwable, context: String? = null) {
        if (!BuildConfig.CRASHLYTICS_ENABLED) return
        runCatching {
            val crashlytics = FirebaseCrashlytics.getInstance()
            context?.let { crashlytics.setCustomKey("context", it.take(120)) }
            crashlytics.recordException(error)
        }
    }

    fun log(message: String) {
        if (!BuildConfig.CRASHLYTICS_ENABLED) return
        runCatching {
            FirebaseCrashlytics.getInstance().log(message.take(250))
        }
    }
}
