package ro.pub.cs.systems.eim.practicaltest01

// object = o singură instanță, accesată direct: Constants.TAG
object Constants {

    // ───── GENERAL ─────
    // tag-ul pentru Logcat (serviciu, thread, activitate)
    const val TAG = "PracticalTest01"

    // ───── D.1 a: serviciul ─────
    // cheile pentru numerele trimise serviciului (activitate → serviciu)
    const val FIRST_NUMBER = "firstNumber"
    const val SECOND_NUMBER = "secondNumber"

    // cele trei acțiuni, dintre care se alege aleator una
    const val ACTION_1 = "ro.pub.cs.systems.eim.practicaltest01.ACTION_1"
    const val ACTION_2 = "ro.pub.cs.systems.eim.practicaltest01.ACTION_2"
    const val ACTION_3 = "ro.pub.cs.systems.eim.practicaltest01.ACTION_3"
    val ACTIONS = listOf(ACTION_1, ACTION_2, ACTION_3)

    // cheia pentru mesajul din broadcast (serviciu → receiver)
    const val BROADCAST_MESSAGE = "message"

    // intervalul dintre broadcast-uri: 10 secunde, în milisecunde
    const val SLEEP_TIME = 10000L // Thread.sleep() cere Long

    // ───── D.1 b: pornirea serviciului ─────
    // pragul: serviciul pornește când suma DEPĂȘEȘTE această valoare
    const val THRESHOLD = 5

    // ───── D.2: receiverul ─────
    // tag-ul cu care receiverul scrie mesajele primite în Logcat
    const val BROADCAST_RECEIVER_TAG = "[Message]"
}