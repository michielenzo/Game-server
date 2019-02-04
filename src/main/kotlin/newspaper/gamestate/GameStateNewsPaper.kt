package main.kotlin.newspaper.gamestate

import main.kotlin.newspaper.INewsPaper
import main.kotlin.utilities.DTO

class GameStateNewsPaper: INewsPaper<IGameStateNewsPaperSubscriber> {

    private val subscribers: MutableList<IGameStateNewsPaperSubscriber> = mutableListOf()

    companion object {
        private val gameStateNewsPaper = GameStateNewsPaper()

        fun getInstance(): GameStateNewsPaper {
            return gameStateNewsPaper
        }
    }

    override fun subscribe(newSubscriber: IGameStateNewsPaperSubscriber) {
        subscribers.add(newSubscriber)
    }

    override fun broadcast(dto: DTO) {
        subscribers.forEach { subscriber ->
            subscriber.notifyGameStateNews(dto)
        }
    }

}