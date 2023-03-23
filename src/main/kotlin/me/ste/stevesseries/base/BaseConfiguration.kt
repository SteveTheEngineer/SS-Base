package me.ste.stevesseries.base

import me.ste.stevesseries.base.api.config.Config

class BaseConfiguration(
    impl: BaseAPIImpl
) : Config() {
    val storageProvider by value("providers.storage", impl.getStorageManager().fileProvider.toString(), listOf(
        "The provider used for storing data outside any world.",
        "Currently, the only available storage provider is \"ss-base:file\"."
    ))
    val localeProviders by value("providers.locale", arrayOf(impl.getI18nManager().clientProviderId.toString()), listOf(
        "Providers used for determining a player's language.",
        "The earlier the entry is, the more priority it has.",
        "",
        "Available locale providers:",
        "\"ss-base:client\" - uses the locale from player's client settings."
    ))
}