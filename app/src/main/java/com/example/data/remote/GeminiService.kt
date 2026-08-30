package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.model.CodeSuggestion
import com.example.model.DiagnosticSeverity
import com.example.model.GeneratedDocs
import com.example.model.GeneratedTests
import com.example.model.Language
import com.example.model.SyntaxDiagnostic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    suspend fun callGemini(prompt: String, systemInstruction: String? = null): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("GeminiService", "GEMINI_API_KEY is not configured or placeholder.")
            return@withContext Result.failure(IllegalStateException("GEMINI_API_KEY not configured"))
        }

        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                systemInstruction?.let { sysText ->
                    val sysObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", sysText))
                        }
                        put("parts", parts)
                    }
                    put("systemInstruction", sysObj)
                }

                val genConfig = JSONObject().apply {
                    put("temperature", 0.2)
                    put("topP", 0.95)
                }
                put("generationConfig", genConfig)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("$baseUrl?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: "Empty error"
                Log.e("GeminiService", "Gemini HTTP error ${response.code}: $errBody")
                return@withContext Result.failure(Exception("HTTP ${response.code}: $errBody"))
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    return@withContext Result.success(text)
                }
            }
            Result.failure(Exception("No content generated from Gemini"))
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception in Gemini API call", e)
            Result.failure(e)
        }
    }
}
