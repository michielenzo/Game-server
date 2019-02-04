package main.kotlin.newspaper

import main.kotlin.utilities.DTO

interface INewsPaper<ISubscriber> {
    fun broadcast(dto: DTO)
    fun subscribe(newSubscriber: ISubscriber)
}