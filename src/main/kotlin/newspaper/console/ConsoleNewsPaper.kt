package main.kotlin.newspaper.network

import main.kotlin.newspaper.INewsPaper
import main.kotlin.newspaper.console.IConsoleNewsPaperSubscriber
import main.kotlin.utilities.DTO

object ConsoleNewsPaper: INewsPaper<IConsoleNewsPaperSubscriber>() {

    override fun broadcast(dto: DTO) {
        subscribers.forEach {
            it.notifyConsoleNews(dto)
        }
    }

}