package main.kotlin.publisher

import main.kotlin.utilities.DTO

abstract class IPublisher<ISubscriber> {

    protected val subscribers: MutableList<ISubscriber> = mutableListOf()

    abstract fun broadcast(dto: DTO)

    fun subscribe(newSubscriber: ISubscriber) {
        subscribers.add(newSubscriber)
    }

    fun unsubscribe(subscriber: ISubscriber){
        subscribers.remove(subscriber)
    }
}