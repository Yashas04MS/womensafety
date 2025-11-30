import android.content.Context

object TokenManager {

    private const val PREF = "user_token"

    fun saveToken(context: Context, token: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putString("jwt", token).apply()
    }

    fun getToken(context: Context): String? {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString("jwt", null)
    }
}
