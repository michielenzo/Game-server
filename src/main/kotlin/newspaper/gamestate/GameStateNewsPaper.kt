package main.kotlin.newspaper.gamestate

import main.kotlin.newspaper.INewsPaper
import main.kotlin.utilities.DTO

object GameStateNewsPaper: INewsPaper<IGameStateNewsPaperSubscriber>() {

    override fun broadcast(dto: DTO) {
        subscribers.forEach {
            it.notifyGameStateNews(dto)
        }
    }

}