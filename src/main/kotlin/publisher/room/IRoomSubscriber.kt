package main.kotlin.publisher.room

import main.kotlin.publisher.ISubscriber
import main.kotlin.utilities.DTO

interface IRoomSubscriber : ISubscriber{
    fun notifyRoomNews(dto: DTO)
}