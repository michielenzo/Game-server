package main.kotlin.newspaper.lobby

import main.kotlin.newspaper.ISubscriber
import main.kotlin.utilities.DTO

interface ILobbyNewsPaperSubscriber : ISubscriber{
    fun notifyLobbyNews(dto: DTO)
}