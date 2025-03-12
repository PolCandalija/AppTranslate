package dam2.m08.apptranslate

import android.os.Bundle
import android.os.Debug
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dam2.m08.apptranslate.API.retrofitService
import dam2.m08.apptranslate.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.HTTP

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }
    var allLanguages = emptyList<Language>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
        getLanguages()
    }

    private fun initListener() {
        binding.btDetectLanguage.setOnClickListener {
            val text = binding.etLanguage.text.toString()
            if(text.isNotEmpty()){
                getTextLanguage(text)
            }
        }
    }

    private fun getTextLanguage(text: String) {
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            val result = retrofitService.getTextLanguage(text)
            if(result.isSuccessful){
                checkResult(result.body())
            }else{
                showError()
            }
        }
    }

    private fun checkResult(detectionResponse: DetectionResponse?) {
        if(detectionResponse != null && !detectionResponse.data.detections.isNullOrEmpty()){
            val correctLanguages = detectionResponse.data.detections.filter { it.isReliable }

            if(correctLanguages.isNotEmpty()){
                val languageName = allLanguages.find { it.code == correctLanguages.first().language }

                if(languageName != null){
                    runOnUiThread {
                        Toast.makeText(this, "L'idioma és ${languageName.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getLanguages() {
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {

            val languages = retrofitService.getLanguages()

            if(languages.isSuccessful){
                allLanguages = languages.body() ?: emptyList()
                showSuccess()
            }else{
                showError()
            }
        }
    }

    private fun showSuccess() {
        runOnUiThread{
            Toast.makeText(this, "Succesful get", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showError() {
        runOnUiThread{
            Toast.makeText(this, "Error get", Toast.LENGTH_SHORT).show()
        }
    }
}