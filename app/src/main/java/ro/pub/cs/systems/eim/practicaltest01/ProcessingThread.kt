package ro.pub.cs.systems.eim.practicaltest01

import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Date
import kotlin.math.sqrt

// ───── D.1 a: thread-ul care trimite broadcast-urile ─────
// thread separat: serviciul rulează pe main thread, iar sleep() acolo l-ar bloca (ANR)
// primește numerele de la serviciu, prin constructor
class ProcessingThread(
    private val context: Context,
    private val firstNumber: Int,
    private val secondNumber: Int
) : Thread() {

    // ───── D.1 b: flag-ul de oprire ─────
    // @Volatile: modificarea făcută din main thread e văzută imediat aici
    @Volatile
    private var isRunning = true

    // ───── D.1 a: media aritmetică și media geometrică ─────
    // se calculează o singură dată (numerele nu se schimbă)
    private val arithmeticMean = (firstNumber + secondNumber) / 2.0          // 2.0 → Double
    private val geometricMean = sqrt((firstNumber * secondNumber).toDouble())

    // ───── D.1 a: bucla – un broadcast la fiecare 10 secunde ─────
    override fun run() {
        Log.d(Constants.TAG, "Thread started, TID: ${android.os.Process.myTid()}")
        while (isRunning) {
            sendMessage()
            sleepThread()
        }
        Log.d(Constants.TAG, "Thread stopped")
    }

    // ───── D.1 a: construirea și trimiterea broadcast-ului ─────
    private fun sendMessage() {
        val intent = Intent()
        // acțiune aleatoare din cele trei
        intent.action = Constants.ACTIONS.random()
        // mesajul: data și ora + cele două medii
        intent.putExtra(
            Constants.BROADCAST_MESSAGE,
            "${Date()} | arithmetic mean: $arithmeticMean | geometric mean: $geometricMean"
        )
        // broadcast-ul ajunge doar în aplicația noastră
        intent.setPackage(context.packageName)

        context.sendBroadcast(intent)
        Log.d(Constants.TAG, "Broadcast sent: ${intent.action}")
    }

    // ───── D.1 a: pauza de 10 secunde dintre mesaje ─────
    private fun sleepThread() {
        try {
            sleep(Constants.SLEEP_TIME)
        } catch (e: InterruptedException) {
            // trezit de stopThread()
        }
    }

    // ───── D.1 b: oprirea thread-ului ─────
    // apelată din onDestroy() al serviciului → nu mai trimite broadcast-uri
    fun stopThread() {
        isRunning = false
        interrupt()     // îl trezește imediat din sleep
    }

    // ───── OPȚIONAL: numere actualizate la fiecare startService ─────
    // 1. în constructor: scoate „private val” de la firstNumber și secondNumber
    // 2. adaugă:
    //      @Volatile private var firstNumber = firstNumber
    //      @Volatile private var secondNumber = secondNumber
    //      fun updateNumbers(first: Int, second: Int) {
    //          firstNumber = first
    //          secondNumber = second
    //      }
    // 3. mută calculul celor două medii ÎN sendMessage() (ca să folosească valorile curente)
    // 4. în serviciu, activează ramura else cu updateNumbers
}