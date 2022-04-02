package com.sergnf88.task1




import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

var listNews: MutableList<Any> = mutableListOf() // имя переменной изменяемого списка (Kiparo)
var setNews:MutableSet<String> = mutableSetOf()     //  имя переменной изменяемого множества (keywords)
const val URL_BASE = "https://api.kiparo.com/static/it_news.json"  // Url адрес для получения json


fun main() { // главная функция

    getUserInput()       // вызов функции, получающей ввод пользователя
}


fun getUserInput(){

    var isValidInput:Boolean = false              // пока переменная false будет запрос ввода
    getJson(URL_BASE)                             // вызов функции, получающей json
    while (!isValidInput){                        // цикл запроса ввода от пользователя
        println("Введите NEWS, если хотите прочитать новости")
        println("или выберите новость по ключевому слову:")
        setNews.forEach { item-> print(" $item,")       // печать ключевых слов из изменяемого множества keywords
            println("")}

        val userInput = readLine()                   // запрос от пользователя
        when (userInput?.lowercase()){               // запрос в нижний регистр для удобства
            "news" -> for (ll in listNews) {         // печать новостей
                println("------")
                println((ll as New).title)
                println((ll as New).description)
                println((ll as New).date?.let { it1 -> cnv(it1) })
                println((ll as New).keywords)
                isValidInput = true
            }
            in setNews ->                 for (ll in listNews) {      // печать новостей по ключевым словам
                if (userInput?.lowercase() in (ll as New).keywords){
                    println("------")
                    println((ll as New).title)
                    println((ll as New).date?.let { it1 -> cnv(it1) })
                    println((ll as New).keywords)}
                isValidInput =true
            }

            else -> println("попробуйте ещё")                        // при неправильном вводе- обратно на запрос
        }
    }
}


fun getJson(urlString: String) {

    val client = OkHttpClient()              // создатся объкт OkHttpClient

    val request = Request.Builder()         // создаётся клиент OkHttpClient
        .url(urlString)
        .build()

    client.newCall(request).enqueue(object : Callback {                   // клиент OkHttpClient ставится в очередь на запрос
        override fun onFailure(call: Call, e: IOException) {              // и происходит вызов
            e.printStackTrace()                                           // при ошибке соединения печать исключения
        }


        override fun onResponse(call: Call, response: Response) {                 // при получении ответа 200...300
            response.use {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")   //  проверка на непустой ответ в response

                for ((name, value) in response.headers) {                                   //  извлечение заголовков из ответа
//                    println("$name: $value")
//                    println("-------header-------")
                }

                val body = response.body!!.string()                                         //  тело запроса - json

                val obj = Json.decodeFromString<Kiparo>(body)                               // классы Kiparo New инициализируются данными из json
                listNews = obj.news.toMutableList()//news.news.toMutableList()              // перенос данных в изменяемый список listNews
                listNews.sortBy { mm  -> (mm as New).date }                                 // сортировка списка по дате
                listNews.forEach { item -> (item as New).keywords.forEach { itm ->          // извлечение ключевых слов в изменяемое множество
                    if (itm != null) {
                        setNews.add(itm)
                    }
                }}
                setNews.forEach { item-> print(" $item,") }                                 // печать ключевых слов множества
                println("")
            }
        }
    })
}

@RequiresApi(Build.VERSION_CODES.O)
fun cnv(string: String = "2014-10-25 12:35:00 +0300"): String? {//2014-10-25 12:35:00      // функция форматирования даты под условия задачи

    val yourArray: List<String> = string.split(" ")                      // извлечение даты из строки json
    var date = LocalDate.parse(yourArray[0])

//2014-10-25
    var formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")              // задание формата вывода

    var formattedDate = date.format(formatter)                                      //  форматирование
    return formattedDate                                                            // возврат результата функции
}