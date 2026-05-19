package com.efoy.money.lab.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class QuoteApiService {
    
    suspend fun fetchRandomQuote(): Result<Pair<String, String>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.quotable.io/random")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 4000
            connection.readTimeout = 4000
            connection.doInput = true

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseText)
                val quote = json.getString("content")
                val author = json.getString("author")
                Result.success(Pair(quote, author))
            } else {
                Result.failure(Exception("HTTP Error Code: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
