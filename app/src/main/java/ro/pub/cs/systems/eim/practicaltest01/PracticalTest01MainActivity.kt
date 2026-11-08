package ro.pub.cs.systems.eim.practicaltest01

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
// import android.view.View   // doar pentru varianta cu ButtonClickListener (B.1)

class PracticalTest01MainActivity : AppCompatActivity() {

    // ═════════════════════════════ CÂMPURI ═════════════════════════════

    // ───── B.1 / B.2: starea ─────
    // de câte ori a fost apăsat fiecare buton; pornesc de la 0, ca în layout
    // se pierd la distrugerea activității → le salvăm în Bundle (B.2)
    private var leftCount = 0
    private var rightCount = 0

    // ───── B.1: referințe către câmpurile text ─────
    // lateinit = „le inițializez mai târziu”, în onCreate, după setContentView
    private lateinit var leftText: EditText
    private lateinit var rightText: EditText

    // ───── C.2: launcher pentru activitatea secundară ─────
    // se înregistrează ca CÂMP (înainte de onCreate), altfel crash
    // callback-ul { result -> ... } rulează când activitatea secundară se închide
    private val secondaryActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val status = if (result.resultCode == RESULT_OK) "OK" else "CANCELED"
            Toast.makeText(this, "Result returned from secondary activity: $status", Toast.LENGTH_LONG).show()
        }

    // ───── D.2: receiverul și filtrul cu cele trei acțiuni ─────
    private val broadcastReceiver = PracticalTest01BroadcastReceiver()

    // IntentFilter = ce acțiuni vrem să primim (exact cele trimise de serviciu)
    private val intentFilter = IntentFilter().apply {
        addAction(Constants.ACTION_1)
        addAction(Constants.ACTION_2)
        addAction(Constants.ACTION_3)
    }

    // ═════════════════════════════ onCreate ═════════════════════════════

    // savedInstanceState: null la prima pornire, nenul la recreare
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ───── A.2: încărcăm layout-ul (cod generat de template) ─────
        // conținutul se întinde până la marginile ecranului, iar blocul ViewCompat
        // adaugă padding ca să nu intre sub bara de status
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test01_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ───── B.1: legăm variabilele de elementele din layout ─────
        // findViewById caută elementul după id-ul din XML (android:id="@+id/...")
        // trebuie apelat DUPĂ setContentView, altfel layout-ul nu e încă încărcat → crash
        leftText = findViewById(R.id.left_text)
        rightText = findViewById(R.id.right_text)
        val leftButton = findViewById<Button>(R.id.left_button)
        val rightButton = findViewById<Button>(R.id.right_button)

        // ───── B.2 b: restaurarea stării ─────
        // citim valorile salvate, cu aceleași chei ca la salvare
        if (savedInstanceState != null) {
            leftCount = savedInstanceState.getInt("leftCount")
            rightCount = savedInstanceState.getInt("rightCount")
        }

        // le afișăm (din cauza saveEnabled="false", câmpurile pornesc cu "0")
        leftText.setText(leftCount.toString())
        rightText.setText(rightCount.toString())

        // ───── B.1: ascultătorii de click ─────
        // setOnClickListener { ... } = codul din acolade rulează la fiecare apăsare
        leftButton.setOnClickListener {
            leftCount++                                // incrementăm contorul în memorie
            leftText.setText(leftCount.toString())     // afișăm noua valoare în câmpul text
            checkAndStartService()                     // D.1 b
        }

        rightButton.setOnClickListener {
            rightCount++
            rightText.setText(rightCount.toString())
            checkAndStartService()                     // D.1 b
        }

        // B.1 – SAU VARIANTA CU UN SINGUR LISTENER:
//        val listener = ButtonClickListener()
//        leftButton.setOnClickListener(listener)
//        rightButton.setOnClickListener(listener)

        // ───── C.2: deschidem activitatea secundară ─────
        val navigateButton = findViewById<Button>(R.id.navigate_button)
        navigateButton.setOnClickListener {
            // intenție explicită către activitatea secundară
            val intent = Intent(this, PracticalTest01SecondaryActivity::class.java)
            // trimitem totalul apăsărilor (C.1 îl afișează)
            intent.putExtra("totalClicks", leftCount + rightCount)
            // pornim activitatea, așteptând un rezultat
            secondaryActivityLauncher.launch(intent)
        }

        // ───── D.1 b: repornirea serviciului după recreare ─────
        // dacă activitatea a fost recreată și suma e deja peste prag,
        // repornim serviciul (poate fi fost oprit în onDestroy)
        checkAndStartService()
    }

    // ═════════════════════════════ METODE ═════════════════════════════

    // ───── B.1 – varianta cu un singur listener ─────
//    // inner = acces la câmpurile activității
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
//            checkAndStartService()     // D.1 b
//        }
//    }

    // ───── B.2 b: salvarea stării ─────
    // apelată înainte ca activitatea să poată fi distrusă (nu și la Back)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)            // obligatoriu, primul
        outState.putInt("leftCount", leftCount)        // cheie → valoare
        outState.putInt("rightCount", rightCount)
    }

    // ───── D.1 b: pornim serviciul când suma depășește pragul ─────
    private fun checkAndStartService() {
        if (leftCount + rightCount > Constants.THRESHOLD) {
            // intenție explicită către serviciu, cu numerele din câmpuri (D.1 a)
            val intent = Intent(this, PracticalTest01Service::class.java)
            intent.putExtra(Constants.FIRST_NUMBER, leftCount)
            intent.putExtra(Constants.SECOND_NUMBER, rightCount)
            // dacă serviciul rulează deja, se apelează doar onStartCommand,
            // iar serviciul nu pornește un al doilea thread (processingThread != null)
            startService(intent)
        }
    }

    // ───── D.1 b: oprim serviciul când aplicația e distrusă ─────
    override fun onDestroy() {
        // la rotire activitatea e distrusă și recreată imediat → serviciul NU se oprește
        if (!isChangingConfigurations) {
            stopService(Intent(this, PracticalTest01Service::class.java))
            Log.d(Constants.TAG, "Service stopped from activity")
        }
        super.onDestroy()
    }

    // ───── D.2: ascultăm broadcast-urile cât timp activitatea e vizibilă ─────
    override fun onResume() {
        super.onResume()
        // Android 13+ cere să precizăm de unde acceptăm broadcast-uri;
        // NOT_EXPORTED = doar din aplicația noastră (serviciul e în aceeași aplicație)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(broadcastReceiver, intentFilter)
        }
    }

    override fun onPause() {
        // perechea lui registerReceiver din onResume
        unregisterReceiver(broadcastReceiver)
        super.onPause()
    }
}