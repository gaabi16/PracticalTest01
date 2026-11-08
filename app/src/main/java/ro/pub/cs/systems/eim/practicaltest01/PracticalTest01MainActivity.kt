package ro.pub.cs.systems.eim.practicaltest01

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PracticalTest01MainActivity : AppCompatActivity() {

    // ───── STAREA ─────
    // de câte ori a fost apăsat fiecare buton; pornesc de la 0, ca în layout
    private var leftCount = 0
    private var rightCount = 0

    // ───── REFERINȚE CĂTRE CÂMPURILE TEXT ─────
    // lateinit = „le inițializez mai târziu”, în onCreate, după setContentView
    private lateinit var leftText: EditText
    private lateinit var rightText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ───── COD GENERAT DE TEMPLATE (nu-l modifica) ─────
        // conținutul se întinde până la marginile ecranului, iar blocul ViewCompat
        // adaugă padding ca să nu intre sub bara de status
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test01_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ───── 1. LEGĂM VARIABILELE DE ELEMENTELE DIN LAYOUT ─────
        // findViewById caută elementul după id-ul din XML (android:id="@+id/...")
        // trebuie apelat DUPĂ setContentView, altfel layout-ul nu e încă încărcat → crash
        leftText = findViewById(R.id.left_text)
        rightText = findViewById(R.id.right_text)
        val leftButton = findViewById<Button>(R.id.left_button)
        val rightButton = findViewById<Button>(R.id.right_button)

        // ───── 2. ASCULTĂTORII DE CLICK (cerința B.1) ─────
        // setOnClickListener { ... } = codul din acolade rulează la fiecare apăsare

        leftButton.setOnClickListener {
            leftCount++                                // incrementăm contorul în memorie
            leftText.setText(leftCount.toString())     // afișăm noua valoare în câmpul text
        }

        rightButton.setOnClickListener {
            rightCount++
            rightText.setText(rightCount.toString())
        }

        // SAU VARIANTA CU UN SINGUR LISTENER:
//        val listener = ButtonClickListener()
//        leftButton.setOnClickListener(listener)
//        rightButton.setOnClickListener(listener)
    }

//    private inner class ButtonClickListener : View.OnClickListener {
//        override fun onClick(view: View) {
//            when (view.id) {     // aflăm CE buton a fost apăsat
//                R.id.left_button -> {
//                    leftCount++
//                    leftText.setText(leftCount.toString())
//                }
//                R.id.right_button -> {
//                    rightCount++
//                    rightText.setText(rightCount.toString())
//                }
//            }
//        }
//    }
}