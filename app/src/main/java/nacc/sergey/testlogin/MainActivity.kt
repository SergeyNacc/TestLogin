package nacc.sergey.testlogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        binding.loginPasswordInput.setOnClickListener {
            Toast.makeText(this, "От 4 до 12 знаков", Toast.LENGTH_SHORT).show()
        }

        binding.loginButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email: String = binding.loginEmailInput.text.toString()
        val password: String = binding.loginPasswordInput.text.toString()

        if (email == "") {
            Toast.makeText(this, "Введите почту", Toast.LENGTH_SHORT).show()

        }else if (password == ""
                && password.length < 4
                && password.length > 12) {
            Toast.makeText(this, "Не соответствует условию", Toast.LENGTH_SHORT).show()

        }else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful) {
                            userID = mAuth.currentUser!!.uid
                            refUser = FirebaseDatabase.getInstance().reference.child("Users").child(userID)

                            val  userHashMap = HashMap<String, Any>()
                            userHashMap["uid"] = userID
                            userHashMap["email"] = email

                            refUser.updateChildren(userHashMap)
                                    .addOnCompleteListener {task ->
                                        if (task.isSuccessful)  {
                                            Toast.makeText(this, "Удача", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                        }else {
                            Toast.makeText(this, "Ошибка:" + task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }


}