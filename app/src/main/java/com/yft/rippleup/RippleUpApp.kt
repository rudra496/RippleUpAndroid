package com.yft.rippleup

import android.app.Application

/**
 * Application entry point. Kept intentionally thin — the database is constructed
 * lazily inside [com.yft.rippleup.data.db.AppDatabase] so background threads never
 * touch it before Compose is ready.
 */
class RippleUpApp : Application()
