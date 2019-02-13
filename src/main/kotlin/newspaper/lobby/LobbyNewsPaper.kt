package main.kotlin.newspaper.lobby

import main.kotlin.newspaper.INewsPaper
import main.kotlin.utilities.DTO

object LobbyNewsPaper: INewsPaper<ILobbyNewsPaperSubscriber>() {

    override fun broadcast(dto: DTO) {
        subscribers.forEach {
            it.notifyLobbyNews(dto)
        }
    }

}