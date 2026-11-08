package ro.pub.cs.systems.eim.practicaltest01

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

// ───── D.2: ascultătorul pentru broadcast-urile trimise de serviciu ─────
// înregistrat dinamic în PracticalTest01MainActivity (onResume / onPause)
class PracticalTest01BroadcastReceiver : BroadcastReceiver() {

    // apelată automat la fiecare broadcast care se potrivește cu IntentFilter-ul
    // rulează pe main thread → doar operații scurte
    override fun onReceive(context: Context, intent: Intent) {
        // datele din broadcast: acțiunea aleasă aleator și mesajul (D.1 a)
        val action = intent.action
        val message = intent.getStringExtra(Constants.BROADCAST_MESSAGE)

        // procesarea = jurnalizarea în consolă, cu tag-ul nostru (D.2)
        Log.d(Constants.BROADCAST_RECEIVER_TAG, "$action -> $message")
    }
}