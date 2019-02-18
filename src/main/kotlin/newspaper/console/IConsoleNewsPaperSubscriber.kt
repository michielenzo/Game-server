package main.kotlin.newspaper.console

import main.kotlin.newspaper.ISubscriber
import main.kotlin.utilities.DTO

interface IConsoleNewsPaperSubscriber: ISubscriber {
    fun notifyConsoleNews(dto: DTO)
}