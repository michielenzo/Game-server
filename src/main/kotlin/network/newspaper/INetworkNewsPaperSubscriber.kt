package main.kotlin.network.newspaper

import main.kotlin.network.dto.DTO

interface INetworkNewsPaperSubscriber {
    fun notifyNetworkNews(dto: DTO)
}