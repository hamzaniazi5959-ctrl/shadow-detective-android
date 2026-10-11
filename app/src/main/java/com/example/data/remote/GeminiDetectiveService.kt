package com.example.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val tools: List<JsonObject>? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null,
    val imageConfig: ImageConfig? = null,
    val responseModalities: List<String>? = null
)

@Serializable
data class ImageConfig(
    val aspectRatio: String,
    val imageSize: String = "1K"
)

@Serializable
data class GenerateVideosRequest(
    val prompt: String,
    val image: VeoInputImage? = null,
    val config: VeoConfig? = null
)

@Serializable
data class VeoInputImage(
    val bytesBase64Encoded: String,
    val mimeType: String = "image/jpeg"
)

@Serializable
data class VeoConfig(
    val numberOfVideos: Int = 1,
    val resolution: String = "720p",
    val aspectRatio: String = "16:9"
)

data class GroundedSourceLink(
    val title: String,
    val uri: String
)

data class GroundedSearchResponse(
    val text: String,
    val sources: List<GroundedSourceLink>
)

data class ImageGenerationResult(
    val bitmap: Bitmap?,
    val savedFilePath: String?,
    val captionText: String,
    val error: String? = null
)

data class VideoGenerationResult(
    val videoFilePath: String?,
    val remoteVideoUrl: String?,
    val statusMessage: String,
    val isCompleted: Boolean,
    val error: String? = null
)

interface GeminiRestApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): JsonObject

    @POST("v1beta/models/{model}:generateVideos")
    suspend fun generateVideos(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateVideosRequest
    ): JsonObject

    @GET("v1beta/{operationName}")
    suspend fun getOperationStatus(
        @Path("operationName", encoded = true) operationName: String,
        @Query("key") apiKey: String
    ): JsonObject
}

object GeminiDetectiveService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    const val IMAGE_MODEL = "gemini-nano-banana-2.1"
    const val VIDEO_MODEL = "veo-3.1-fast-generate-preview"
    const val SEARCH_MODEL = "gemini-3.5-flash"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val api: GeminiRestApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeminiRestApi::class.java)
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * 1. Create or Edit Images using gemini-nano-banana-2.1
     */
    suspend fun createOrEditImage(
        context: Context,
        prompt: String,
        aspectRatio: String = "16:9",
        inputBitmap: Bitmap? = null
    ): ImageGenerationResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext ImageGenerationResult(
                bitmap = null,
                savedFilePath = null,
                captionText = "",
                error = "Please configure GEMINI_API_KEY in the AI Studio Secrets panel to generate or edit live images with $IMAGE_MODEL."
            )
        }

        try {
            val parts = mutableListOf<Part>()
            parts.add(Part(text = prompt))
            if (inputBitmap != null) {
                parts.add(
                    Part(
                        inlineData = InlineData(
                            mimeType = "image/jpeg",
                            data = bitmapToBase64(inputBitmap)
                        )
                    )
                )
            }

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = parts)),
                generationConfig = GenerationConfig(
                    imageConfig = ImageConfig(aspectRatio = aspectRatio, imageSize = "1K"),
                    responseModalities = listOf("TEXT", "IMAGE")
                )
            )

            val responseJson = api.generateContent(
                model = IMAGE_MODEL,
                apiKey = apiKey,
                request = request
            )

            val candidates = responseJson["candidates"]?.jsonArray
            val contentParts = candidates?.firstOrNull()?.jsonObject
                ?.get("content")?.jsonObject
                ?.get("parts")?.jsonArray

            var generatedBitmap: Bitmap? = null
            var caption = "Forensic composite generated via $IMAGE_MODEL."

            contentParts?.forEach { element ->
                val partObj = element.jsonObject
                partObj["text"]?.jsonPrimitive?.content?.let { txt ->
                    if (txt.isNotBlank()) caption = txt
                }
                val inlineObj = partObj["inlineData"]?.jsonObject ?: partObj["inline_data"]?.jsonObject
                val base64Data = inlineObj?.get("data")?.jsonPrimitive?.content
                if (!base64Data.isNullOrBlank()) {
                    val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                    generatedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            }

            if (generatedBitmap != null) {
                val file = File(context.filesDir, "sara_photo_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { out ->
                    generatedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 92, out)
                }
                ImageGenerationResult(
                    bitmap = generatedBitmap,
                    savedFilePath = file.absolutePath,
                    captionText = caption
                )
            } else {
                ImageGenerationResult(
                    bitmap = null,
                    savedFilePath = null,
                    captionText = caption,
                    error = "Model returned text without image payload. Try a more descriptive visual prompt."
                )
            }
        } catch (e: Exception) {
            ImageGenerationResult(
                bitmap = null,
                savedFilePath = null,
                captionText = "",
                error = "Gemini Image API ($IMAGE_MODEL): ${e.localizedMessage ?: "Request failed"}"
            )
        }
    }

    /**
     * 2. Animate uploaded photo into video using veo-3.1-fast-generate-preview
     * Aspect Ratio MUST be "16:9" (landscape) or "9:16" (portrait).
     */
    suspend fun animateImageWithVeo(
        context: Context,
        sourceBitmap: Bitmap,
        prompt: String,
        aspectRatio: String, // "16:9" or "9:16"
        onProgress: (String) -> Unit
    ): VideoGenerationResult = withContext(Dispatchers.IO) {
        val validRatio = if (aspectRatio == "9:16") "9:16" else "16:9"
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext VideoGenerationResult(
                videoFilePath = null,
                remoteVideoUrl = null,
                statusMessage = "Missing GEMINI_API_KEY in AI Studio Secrets panel.",
                isCompleted = false,
                error = "Configure GEMINI_API_KEY in the Secrets panel to run Veo ($VIDEO_MODEL)."
            )
        }

        try {
            onProgress("Encoding uploaded photo & dispatching to $VIDEO_MODEL ($validRatio)...")
            val base64Photo = bitmapToBase64(sourceBitmap)
            val request = GenerateVideosRequest(
                prompt = prompt,
                image = VeoInputImage(
                    bytesBase64Encoded = base64Photo,
                    mimeType = "image/jpeg"
                ),
                config = VeoConfig(
                    numberOfVideos = 1,
                    resolution = "720p",
                    aspectRatio = validRatio
                )
            )

            val initialResponse = api.generateVideos(
                model = VIDEO_MODEL,
                apiKey = apiKey,
                request = request
            )

            val operationName = initialResponse["name"]?.jsonPrimitive?.content
            if (operationName.isNullOrBlank()) {
                return@withContext VideoGenerationResult(
                    videoFilePath = null,
                    remoteVideoUrl = null,
                    statusMessage = "Veo operation started",
                    isCompleted = false,
                    error = "Unexpected operation response from $VIDEO_MODEL."
                )
            }

            // Poll long-running operation up to 12 times
            for (attempt in 1..12) {
                onProgress("Rendering Veo 3.1 cutscene ($validRatio) — poll #$attempt...")
                delay(4000L)
                val statusJson = api.getOperationStatus(
                    operationName = operationName.removePrefix("v1beta/"),
                    apiKey = apiKey
                )
                val isDone = statusJson["done"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false
                if (isDone) {
                    val responseObj = statusJson["response"]?.jsonObject
                    val videoUri = extractVideoUri(responseObj)
                    if (!videoUri.isNullOrBlank()) {
                        onProgress("Downloading generated Veo MP4 cutscene...")
                        val localPath = downloadVideoFile(context, videoUri, apiKey)
                        return@withContext VideoGenerationResult(
                            videoFilePath = localPath,
                            remoteVideoUrl = videoUri,
                            statusMessage = "Veo 3.1 Video ($validRatio) Ready!",
                            isCompleted = true
                        )
                    } else {
                        return@withContext VideoGenerationResult(
                            videoFilePath = null,
                            remoteVideoUrl = null,
                            statusMessage = "Operation completed.",
                            isCompleted = true,
                            error = "Veo operation finished without a downloadable URI."
                        )
                    }
                }
            }

            VideoGenerationResult(
                videoFilePath = null,
                remoteVideoUrl = null,
                statusMessage = "Operation $operationName is still rendering on Veo servers.",
                isCompleted = false,
                error = "Veo rendering timed out waiting for poll completion."
            )
        } catch (e: Exception) {
            VideoGenerationResult(
                videoFilePath = null,
                remoteVideoUrl = null,
                statusMessage = "Veo API call finished with notice.",
                isCompleted = false,
                error = "Veo ($VIDEO_MODEL): ${e.localizedMessage ?: "Connection error"}"
            )
        }
    }

    private fun extractVideoUri(responseObj: JsonObject?): String? {
        if (responseObj == null) return null
        val genVidResp = responseObj["generateVideoResponse"]?.jsonObject
        val samples = genVidResp?.get("generatedSamples")?.jsonArray
        val firstSampleUri = samples?.firstOrNull()?.jsonObject
            ?.get("video")?.jsonObject
            ?.get("uri")?.jsonPrimitive?.content
        if (!firstSampleUri.isNullOrBlank()) return firstSampleUri

        val videos = responseObj["videos"]?.jsonArray
        return videos?.firstOrNull()?.jsonObject?.get("uri")?.jsonPrimitive?.content
    }

    private fun downloadVideoFile(context: Context, videoUri: String, apiKey: String): String? {
        return try {
            val urlWithKey = if (videoUri.contains("?")) "$videoUri&key=$apiKey" else "$videoUri?key=$apiKey"
            val req = Request.Builder().url(urlWithKey).build()
            okHttpClient.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return null
                val outFile = File(context.filesDir, "veo_cutscene_${System.currentTimeMillis()}.mp4")
                resp.body?.byteStream()?.use { input ->
                    FileOutputStream(outFile).use { output ->
                        input.copyTo(output)
                    }
                }
                outFile.absolutePath
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * 3. Google Search Grounding using gemini-3.5-flash (with googleSearch tool)
     */
    suspend fun queryCrimeNewsWithGoogleSearch(
        query: String,
        saraContext: String
    ): GroundedSearchResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext GroundedSearchResponse(
                text = "Configure GEMINI_API_KEY in the AI Studio Secrets panel to fetch live Google Search grounded news via $SEARCH_MODEL.",
                sources = emptyList()
            )
        }

        try {
            val googleSearchTool = buildJsonObject {
                putJsonObject("googleSearch") {}
            }

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(
                                text = "You are the WNKP Channel 7 Crime & Forensics News Anchor in Shadow Detective ($saraContext). " +
                                    "Use Google Search to provide accurate, up-to-date real-world facts and news for: $query"
                            )
                        )
                    )
                ),
                tools = listOf(googleSearchTool),
                generationConfig = GenerationConfig(temperature = 0.5f)
            )

            val responseJson = api.generateContent(
                model = SEARCH_MODEL,
                apiKey = apiKey,
                request = request
            )

            val firstCandidate = responseJson["candidates"]?.jsonArray?.firstOrNull()?.jsonObject
            val parts = firstCandidate?.get("content")?.jsonObject?.get("parts")?.jsonArray
            val answerText = parts?.mapNotNull { it.jsonObject["text"]?.jsonPrimitive?.content }
                ?.joinToString("\n")
                ?.ifBlank { "No grounded report returned." }
                ?: "No grounded report returned."

            val sources = mutableListOf<GroundedSourceLink>()
            val groundingMetadata = firstCandidate?.get("groundingMetadata")?.jsonObject
            val chunks = groundingMetadata?.get("groundingChunks")?.jsonArray
            chunks?.forEach { chunkEl ->
                val webObj = chunkEl.jsonObject["web"]?.jsonObject
                val title = webObj?.get("title")?.jsonPrimitive?.content
                val uri = webObj?.get("uri")?.jsonPrimitive?.content
                if (!uri.isNullOrBlank()) {
                    sources.add(GroundedSourceLink(title = title ?: uri, uri = uri))
                }
            }

            GroundedSearchResponse(text = answerText, sources = sources.distinctBy { it.uri })
        } catch (e: Exception) {
            GroundedSearchResponse(
                text = "Live Google Search Grounding ($SEARCH_MODEL) error: ${e.localizedMessage ?: "Network error"}",
                sources = emptyList()
            )
        }
    }
}
