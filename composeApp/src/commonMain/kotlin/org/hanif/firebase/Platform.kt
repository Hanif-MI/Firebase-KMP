package org.hanif.firebase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform