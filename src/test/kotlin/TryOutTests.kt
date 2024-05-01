package test

import com.google.gson.Gson
import java.time.LocalDateTime

class TryOutTests{

    @org.junit.Test
    fun tryOutGson(){
        val father = Father("Tim", 60, "brillenman")
        val person = Person("Michiel", 22, father)
        person.hobby.add("yu-gi-oh")
        person.hobby.add("Runescape")
        person.hobby.add("Chillen")
        val gson = Gson()
        println(gson.toJson(person))
    }

    data class Person(val name: String, val age: Int, val father: Father, val hobby: MutableList<String> = mutableListOf())
    data class Father(val name: String, val age: Int, val job: String)

    @org.junit.Test
    fun tryOutLocalDateTime(){
        println(LocalDateTime.now())
    }

    @org.junit.Test
    fun tryOutPlus(){

    }



}