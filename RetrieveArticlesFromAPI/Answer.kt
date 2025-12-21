import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Article(
    val title: String?,
    val url: String?,
    val author: String?,
    val num_comments: Long?,
    val story_id: Long?,
    val story_title: String?,
    val story_url: String?,
    val parent_id: Long?,
    val created_at: Long?
)

data class ApiResponse(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    val total: Int,
    @SerializedName("total_pages") val totalPages: Int,
    val data: List<Article>
)

fun getArticleTitles(author: String): Array<String> {

    val titles = mutableListOf<String>()
    val gson = Gson()
    var page = 1
    var totalPages = 1

    do {
        val url = URL(
            "https://testapi.com/api/articles?author=$author&page=$page"
        )

        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = reader.readText()
        reader.close()

        val apiResponse = gson.fromJson(response, ApiResponse::class.java)

        totalPages = apiResponse.totalPages

        for (article in apiResponse.data) {
            when {
                !article.title.isNullOrEmpty() -> titles.add(article.title)

                article.title.isNullOrEmpty() && !article.story_title.isNullOrEmpty() -> titles.add(
                    article.story_title
                )
            }
        }

        page++
    } while (page <= totalPages)

    return titles.toTypedArray()
}
