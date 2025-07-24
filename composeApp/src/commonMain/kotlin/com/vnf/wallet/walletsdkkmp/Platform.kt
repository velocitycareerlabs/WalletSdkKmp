package com.vnf.wallet.walletsdkkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform