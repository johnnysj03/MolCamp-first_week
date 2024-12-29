package com.example.myapplication_test

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.myapplication_test.ui.theme.MyApplication_testTheme
import com.example.myapplication_test.utils.loadJson
import com.example.myapplication_test.utils.parseJson
import com.example.myapplication_test.utils.readJsonFile
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Serializable
data class userData(
    val username: String,
    val password: String
)

@Serializable
data class reviewData(
    val reviewId : Int,
    val city: String,
    val district: String,
    val neighborhood: String,
    val name: String,
    val rating: Int,
    val recommend: Int,
    var image: String,
)

@Serializable
data class contactData(
    val title: String,
    val color: String,
    val dialogContent: String,
    val phoneNumber: String,
    val website: String
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reviewDatas = parseJson<reviewData>(this,"review.json").toMutableList()
        val tempBitmap = this.readJsonFile("test1_img.txt")
        val defalultUserData:List<userData> = parseJson(this, "users.json")
        val userData:List<userData> = loadJson(this,"users.json")
        val users = (userData+defalultUserData).toMutableList()
        val imgData:List<contactData> = parseJson(this, "contact.json")
        for(item in reviewDatas){
            if(item.image=="null"){
                item.image = tempBitmap
            }
        }

        setContent {
            MyApplication_testTheme {
                var isLoggedIn by remember { mutableStateOf(false) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLoggedIn) {
                        // 로그인 성공 후 탭 화면 표시
                        TabLayout(
                            context = this,
                            imgData,
                            reviewDatas
                        )
                    } else {
                        // 로그인 화면 표시
                        LoginScreen(
                            data = users,
                            onLoginSuccess = { isLoggedIn = true },
                            onSignUp = { username, password ->
                                users.add(userData(username, password))
                                saveJson(this, "users.json", users)
                            }
                        )
                    }
                }
            }
        }
    }
    private fun saveJson(context: Context, fileName: String, data: List<userData>) {
        val jsonString = Json.encodeToString(data)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }
}