package nacc.sergey.testlogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import nacc.sergey.testlogin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mAuth : FirebaseAuth
    private lateinit var refUser : DatabaseReference
    private var userID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.loginEmailInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.contains('@')) {
                    autoComplete()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.loginPasswordInput.setOnClickListener {
            Toast.makeText(this, "От 6 до 12 знаков", Toast.LENGTH_SHORT).show()
        }

        binding.loginButton.setOnClickListener {
            registerUser()
        }
    }


    private fun registerUser() {
        val name: String = binding.loginNameInput.text.toString()
        val email: String = binding.loginEmailInput.text.toString()
        val password: String = binding.loginPasswordInput.text.toString()

        if (email == "") {
            Toast.makeText(this, "Введите почту", Toast.LENGTH_SHORT).show()

        }else if (password == "") {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()

        }else if (name == "") {
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()

        }else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful) {
                            userID = mAuth.currentUser!!.uid
                            refUser = FirebaseDatabase.getInstance().reference.child("Users").child(userID)

                            val  userHashMap = HashMap<String, Any>()
                            userHashMap["name"] = name
                            userHashMap["email"] = email
                            userHashMap["uid"] = userID

                            refUser.updateChildren(userHashMap)
                                    .addOnCompleteListener {task ->
                                        if (task.isSuccessful)  {
                                            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                        }else {
                            Toast.makeText(this, "Ошибка:" + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

        }
    }

    fun autoComplete() {

        val emailArray = mutableListOf("@mail.ru", "@gmail.com", "@yahoo.com",
                "@i.ua", "@yandex.ru", "@rambler.ru", "@qip.ru", "@mail.ua")

        val autoTextView = findViewById<AutoCompleteTextView>(R.id.login_email_input)
        //val emailArray = resources.getStringArray(R.array.email_array)
        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emailArray).also { adapter ->
            autoTextView.setAdapter(adapter)
        }
    }


}