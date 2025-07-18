package org.dodo.uhabits.utils

object AdConfig {
    // 是否开启测试广告模式（建议自动判断）
    private val USE_TEST_AD: Boolean
        get() = com.dodo.dohabits.BuildConfig.DEBUG

    // 广告位类型定义
    enum class AdType {
        BANNER_HOME,
        INTERSTITIAL_GAME,
        REWARDED_VIDEO,
        APP_OPEN
    }

    // 获取广告位 ID（自动切换测试 / 正式）
    fun getAdUnitId(type: AdType): String {
        return if (USE_TEST_AD) {
            // Google 官方测试广告位
            when (type) {
                AdType.BANNER_HOME       -> "ca-app-pub-3940256099942544/6300978111"
                AdType.INTERSTITIAL_GAME -> "ca-app-pub-3940256099942544/1033173712"
                AdType.REWARDED_VIDEO    -> "ca-app-pub-3940256099942544/5224354917"
                AdType.APP_OPEN          -> "ca-app-pub-3940256099942544/3419835294"
            }
        } else {
            // 你自己申请的正式广告位 ID（请替换为实际 ID）
            when (type) {
                AdType.BANNER_HOME       -> "ca-app-pub-3940256099942544/6300978111"
                AdType.INTERSTITIAL_GAME -> "ca-app-pub-3940256099942544/1033173712"
                AdType.REWARDED_VIDEO    -> "ca-app-pub-3940256099942544/5224354917"
                AdType.APP_OPEN          -> "ca-app-pub-3940256099942544/3419835294"
            }
        }
    }
} 