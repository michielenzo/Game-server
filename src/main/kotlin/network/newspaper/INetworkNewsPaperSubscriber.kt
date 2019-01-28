package main.kotlin.network.newspaper

import com.google.gson.JsonObject
import main.kotlin.network.dto.DTO

interface INetworkNewsPaperSubscriber {
    fun notifyNetworkNews(dto: DTO)
}