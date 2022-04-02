package com.sergnf88.task1




import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

var listNews: MutableList<Any> = mutableListOf()
var setNews:MutableSet<String> = mutableSetOf()
const val URL_BASE = "https://api.kiparo.com/static/it_news.json"

fun main(args: Array<String>) {

    getUserInput()
}

fun getUserInput(){

    var isValidInput:Boolean = false
    getJson(URL_BASE)
    while (!isValidInput){
        println("Введите NEWS, если хотите прочитать новости")
        println("или выберите новость по ключевому слову:")
        setNews.forEach { item-> print(" $item,")
            println("")}

        val userInput = readLine()
        when (userInput?.lowercase()){
            "news" -> for (ll in listNews) {
                println("------")
                println((ll as New).title)
                println((ll as New).description)
                println((ll as New).date?.let { it1 -> cnv(it1) })
                println((ll as New).keywords)
                isValidInput = true
            }
            in setNews ->                 for (ll in listNews) {
                if (userInput?.lowercase() in (ll as New).keywords){
                    println("------")
                    println((ll as New).title)
                    println((ll as New).date?.let { it1 -> cnv(it1) })
                    println((ll as New).keywords)}
                isValidInput =true
            }

            else -> println("попробуйте ещё")
        }
    }
}


fun getJson(urlString: String) {

    val client = OkHttpClient()

    val request = Request.Builder()
        .url(urlString)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }


        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                for ((name, value) in response.headers) {
//                    println("$name: $value")
//                    println("-------header-------")
                }

                val body = response.body!!.string()

                val obj = Json.decodeFromString<Kiparo>(body)
                listNews = obj.news.toMutableList()//news.news.toMutableList()
                listNews.sortBy { mm  -> (mm as New).date }
                listNews.forEach { item -> (item as New).keywords.forEach { itm ->
                    if (itm != null) {
                        setNews.add(itm)
                    }
                }}
                setNews.forEach { item-> print(" $item,") }
                println("")
            }
        }
    })
}

@RequiresApi(Build.VERSION_CODES.O)
fun cnv(string: String = "2014-10-25 12:35:00 +0300"): String? {//2014-10-25 12:35:00

    val yourArray: List<String> = string.split(" ")
    var date = LocalDate.parse(yourArray[0])

//2014-10-25
    var formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")

    var formattedDate = date.format(formatter)
    return formattedDate
}