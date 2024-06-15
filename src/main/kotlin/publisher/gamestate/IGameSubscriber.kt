package main.kotlin.publisher.gamestate

import main.kotlin.game.spaceBalls.gameobjects.Player
import main.kotlin.publisher.ISubscriber
import main.kotlin.utilities.DTO

interface IGameSubscriber: ISubscriber {
    fun notifyGameStateNews(dto: DTO)

    fun notifyGameStateNews(dto: DTO, players: List<Player>)
}
