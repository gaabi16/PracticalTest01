package ro.pub.cs.systems.eim.practicaltest01

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// ───── C.1: activitatea secundară ─────
// pornită DOAR prin intenție din activitatea principală (C.2)
// nu are MAIN/LAUNCHER în manifest → nu apare în meniul de aplicații
class PracticalTest01SecondaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ───── C.1: încărcăm layout-ul (cod generat de template) ─────
        // interfața grafică din activity_practical_test01_secondary.xml
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test01_secondary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ───── C.1: legăm variabilele de elementele din layout ─────
        val totalText = findViewById<TextView>(R.id.total_text)
        //val totalText = findViewById<EditText>(R.id.total_text)   // EditText în loc de TextView
        val okButton = findViewById<Button>(R.id.ok_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        // ───── C.1: afișăm totalul primit prin intenție ─────
        // intent = intenția cu care a fost pornită activitatea (din C.2)
        // cheia trebuie să fie aceeași ca la putExtra din activitatea principală
        val total = intent.getIntExtra("totalClicks", 0)
        totalText.text = total.toString()
        //totalText.setText(total.toString())                       // setText în loc de .text =

        // ───── C.1: OK și Cancel → revenim cu rezultate diferite ─────
        // setResult = ce rezultat primește activitatea principală (Toast-ul din C.2)
        // finish    = închidem activitatea → revenim în cea principală
        okButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}