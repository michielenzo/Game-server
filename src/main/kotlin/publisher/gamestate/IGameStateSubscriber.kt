package main.kotlin.publisher.gamestate

import main.kotlin.publisher.ISubscriber
import main.kotlin.utilities.DTO

interface IGameStateSubscriber: ISubscriber {
    fun notifyGameStateNews(dto: DTO)
}
