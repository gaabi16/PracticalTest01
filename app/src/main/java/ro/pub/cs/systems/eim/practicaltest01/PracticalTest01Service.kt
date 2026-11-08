package ro.pub.cs.systems.eim.practicaltest01

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

// ───── D.1 a: serviciu de tip STARTED ─────
// pornit cu startService() din activitatea principală (D.1 b), rulează până la stopService()
class PracticalTest01Service : Service() {

    // thread-ul care trimite broadcast-urile (serviciul rulează pe main thread)
    private var processingThread: ProcessingThread? = null

    // apelată la FIECARE startService()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(Constants.TAG, "onStartCommand() was invoked")

        // ───── D.1 a: numerele primite prin intenția cu care a fost pornit ─────
        // intent? → poate fi null; ?: 0 → valoare implicită
        val firstNumber = intent?.getIntExtra(Constants.FIRST_NUMBER, 0) ?: 0
        val secondNumber = intent?.getIntExtra(Constants.SECOND_NUMBER, 0) ?: 0

        // ───── D.1 a: pornim thread-ul care trimite broadcast-uri la 10 s ─────
        // o singură dată, ca să nu avem mai multe thread-uri în paralel
        if (processingThread == null) {
            processingThread = ProcessingThread(this, firstNumber, secondNumber)
            processingThread?.start()
        }
        // OPȚIONAL – actualizarea numerelor la fiecare startService
        // (necesită metoda updateNumbers în ProcessingThread):
        // else {
        //     processingThread?.updateNumbers(firstNumber, secondNumber)
        // }

        // dacă sistemul omoară serviciul, îl repornește cu ACEEAȘI intenție (cu numerele)
        return START_REDELIVER_INTENT
    }

    // obligatoriu; null = serviciul nu permite binding (e doar started)
    override fun onBind(intent: Intent?): IBinder? = null

    // ───── D.1 b: oprirea serviciului ─────
    // apelată după stopService() din onDestroy-ul activității principale
    override fun onDestroy() {
        Log.d(Constants.TAG, "onDestroy() was invoked")
        // oprim thread-ul → nu mai trimite broadcast-uri
        processingThread?.stopThread()
        processingThread = null
        super.onDestroy()
    }
}