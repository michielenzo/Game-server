package main.kotlin.publisher.lobby

import main.kotlin.publisher.ISubscriber
import main.kotlin.utilities.DTO

interface ILobbySubscriber : ISubscriber{
    fun notifyLobbyNews(dto: DTO)
}