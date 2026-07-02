package com.example.api

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private const val MODEL = "gemini-3.5-flash"

    suspend fun analyzeMealPhoto(bitmap: Bitmap): AnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext AnalysisResult(
                isFood = false,
                error = "Gemini API Key is not set. Please add GEMINI_API_KEY to your Secrets panel."
            )
        }

        // 1. Resize and compress Bitmap to keep Base64 small & fast
        val maxDim = 600
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val resized = if (originalWidth > maxDim || originalHeight > maxDim) {
            val ratio = originalWidth.toFloat() / originalHeight.toFloat()
            val (newW, newH) = if (ratio > 1f) {
                maxDim to (maxDim / ratio).toInt()
            } else {
                (maxDim * ratio).toInt() to maxDim
            }
            Bitmap.createScaledBitmap(bitmap, newW, newH, true)
        } else {
            bitmap
        }

        val outputStream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        val imageBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

        // 2. Build request JSON using org.json
        val promptText = """
            Analyze this image. Your task is to identify if the image contains food or a meal.
            You must respond with a raw JSON object only. Do not wrap the JSON in ```json markdown blocks.
            
            - If the image does NOT contain food or a meal (for example: a person's face, a pet, a car, an empty room, computer screens, documents, or random non-food items), you must set "isFood" to false, and set "error" to a helpful message explaining that it is not a food image.
            - If the image contains food, you must set "isFood" to true, "error" to null, and estimate:
              - "foodName": A clear name of the meal/food (e.g., 'Spaghetti Carbonara', 'Avocado Toast', etc.).
              - "estimatedCalories": Total estimated calories as an integer.
              - "estimatedCarbsG": Total estimated carbohydrates in grams as an integer.
              - "estimatedProteinG": Total estimated protein in grams as an integer.
              - "estimatedFatG": Total estimated fat in grams as an integer.

            JSON Schema format:
            {
              "isFood": boolean,
              "error": "Reason why it is not food" or null,
              "foodName": "Food Name" or null,
              "estimatedCalories": 350 or null,
              "estimatedCarbsG": 20 or null,
              "estimatedProteinG": 15 or null,
              "estimatedFatG": 10 or null
            }
        """.trimIndent()

        try {
            // Build content parts
            val textPartJson = JSONObject().put("text", promptText)
            val imagePartJson = JSONObject().put("inlineData", JSONObject()
                .put("mimeType", "image/jpeg")
                .put("data", imageBase64)
            )

            val partsArray = JSONArray().put(textPartJson).put(imagePartJson)
            val contentJson = JSONObject().put("parts", partsArray)
            val contentsArray = JSONArray().put(contentJson)

            // Optional response configuration for JSON
            val configJson = JSONObject()
                .put("responseMimeType", "application/json")

            val requestJson = JSONObject()
                .put("contents", contentsArray)
                .put("generationConfig", configJson)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext AnalysisResult(
                        isFood = false,
                        error = "API request failed with code: ${response.code}"
                    )
                }

                val responseBodyStr = response.body?.string()
                if (responseBodyStr.isNullOrEmpty()) {
                    return@withContext AnalysisResult(
                        isFood = false,
                        error = "Empty response received from server."
                    )
                }

                // Parse candidate text
                val rootJson = JSONObject(responseBodyStr)
                val candidates = rootJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext AnalysisResult(
                        isFood = false,
                        error = "No output candidate returned from Gemini."
                    )
                }

                val firstCandidate = candidates.getJSONObject(0)
                val text = firstCandidate.getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .optString("text")

                if (text.isNullOrEmpty()) {
                    return@withContext AnalysisResult(
                        isFood = false,
                        error = "Failed to parse text response."
                    )
                }

                // Parse text block to JSON object
                val cleanedText = text.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val resultJson = JSONObject(cleanedText)
                val isFood = resultJson.optBoolean("isFood", false)
                val error = resultJson.optString("error", "").takeIf { it != "null" && it.isNotEmpty() }
                
                if (!isFood) {
                    return@withContext AnalysisResult(
                        isFood = false,
                        error = error ?: "This does not look like a food image. Please snap a picture of your food!"
                    )
                }

                val foodName = resultJson.optString("foodName", "Unknown Food")
                val calories = resultJson.optInt("estimatedCalories", 0)
                val carbs = resultJson.optInt("estimatedCarbsG", 0)
                val protein = resultJson.optInt("estimatedProteinG", 0)
                val fat = resultJson.optInt("estimatedFatG", 0)

                AnalysisResult(
                    isFood = true,
                    foodName = foodName,
                    calories = calories,
                    carbs = carbs,
                    protein = protein,
                    fat = fat
                )
            }
        } catch (e: Exception) {
            AnalysisResult(
                isFood = false,
                error = "Exception: ${e.message ?: "Unknown error"}"
            )
        }
    }
}

data class AnalysisResult(
    val isFood: Boolean,
    val error: String? = null,
    val foodName: String? = null,
    val calories: Int = 0,
    val carbs: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0
)
