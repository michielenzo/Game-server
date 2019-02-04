package main.kotlin.newspaper.gamestate

import main.kotlin.newspaper.ISubscriber
import main.kotlin.utilities.DTO

interface IGameStateNewsPaperSubscriber: ISubscriber {
    fun notifyGameStateNews(dto: DTO)
}
