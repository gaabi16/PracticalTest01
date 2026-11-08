# Ce scriu și unde – ghid pentru colocviul EIM

> Pentru fiecare tip de cerință: **în ce fișier** se rezolvă și **ce cod** scrii acolo.
> La final, ambele colocvii (Var01 și Var08) desfăcute cerință cu cerință.

---

## Cuprins

1. [Fișierele unui proiect și rolul lor](#1-fișierele-unui-proiect-și-rolul-lor)
2. [Cele 4 întrebări care îți spun fișierul](#2-cele-4-întrebări-care-îți-spun-fișierul)
3. [Catalog: formulare din enunț → fișier → cod](#3-catalog-formulare-din-enunț--fișier--cod)
   - [Interfață grafică](#31-interfață-grafică)
   - [Butoane și acțiuni](#32-butoane-și-acțiuni)
   - [O activitate nouă](#33-o-activitate-nouă)
   - [Trimiterea datelor între activități](#34-trimiterea-datelor-între-activități)
   - [Rezultatul înapoi](#35-rezultatul-înapoi)
   - [Salvarea stării](#36-salvarea-stării)
   - [Serviciul](#37-serviciul)
   - [Broadcast și receiver](#38-broadcast-și-receiver)
4. [Fluxul datelor – desene](#4-fluxul-datelor--desene)
5. [Var01 desfăcut pe fișiere](#5-var01-desfăcut-pe-fișiere)
6. [Var08 desfăcut pe fișiere](#6-var08-desfăcut-pe-fișiere)

---

## 1. Fișierele unui proiect și rolul lor

| Fișier | Ce conține | Răspunde la întrebarea |
|---|---|---|
| `res/layout/activity_X.xml` | **cum arată** ecranul activității X | *Ce se vede?* |
| `XActivity.kt` | **ce face** ecranul X (click-uri, date, pornire de activități/servicii) | *Ce se întâmplă când…?* |
| `AndroidManifest.xml` | **lista componentelor** (activități, servicii, receivere statice) + permisiuni | *Există componenta pentru sistem?* |
| `Constants.kt` | chei, acțiuni, praguri, intervale – **opțional** | *Unde țin valorile folosite în mai multe fișiere?* |
| `XService.kt` | serviciul: primește datele, pornește/oprește thread-ul | *Ce rulează în fundal?* |
| `ProcessingThread.kt` | munca repetată: calcule, `sleep`, `sendBroadcast` | *Ce se face la fiecare N secunde?* |
| `XBroadcastReceiver.kt` | ce se întâmplă când **sosește** un broadcast | *Ce fac cu mesajul primit?* |

**Regula de bază:** **XML = aspect**, **Kotlin = comportament**. Dacă cerința descrie *cum arată* ceva → XML. Dacă descrie *ce se întâmplă* → Kotlin.

---

## 2. Cele 4 întrebări care îți spun fișierul

Pentru orice propoziție din enunț, pune-ți pe rând:

**1. Se vede pe ecran?** (buton, câmp, text, aliniere, „needitabil”, „jumătate”)
→ **layout-ul XML** al ecranului pe care apare.

**2. Ce declanșează acțiunea și unde e?** (un click, deschiderea unei activități)
→ **`.kt`-ul activității în care se află butonul** / care e deschisă.
*Butonul Play e în activitatea principală → codul lui e în `MainActivity.kt`, chiar dacă deschide altă activitate.*

**3. Cine are datele și cine are nevoie de ele?**
→ **cine are** datele le pune (`putExtra`) – în fișierul lui;
→ **cine are nevoie** le citește (`getXExtra`) – în fișierul lui.

**4. E o componentă nouă?** (activitate, serviciu)
→ **clasă nouă** + **declarare în manifest** (+ layout nou, dacă e activitate).

---

## 3. Catalog: formulare din enunț → fișier → cod

### 3.1 Interfață grafică

---

**„Să se creeze o interfață grafică care conține un buton…, două câmpuri text…”**

📄 `res/layout/activity_<activitate>.xml`

```xml
<LinearLayout android:orientation="vertical" ...>
    <Button android:id="@+id/play_button" ... />
    <LinearLayout android:orientation="horizontal" ...>
        <EditText android:id="@+id/left_text" ... />
        <EditText android:id="@+id/right_text" ... />
    </LinearLayout>
</LinearLayout>
```

---

**„…câmpuri text needitabile…”**

📄 același XML, pe fiecare câmp

```xml
android:enabled="false"
```

---

**„…ocupând fiecare jumătate din lățime…”**

📄 același XML

```xml
<LinearLayout android:orientation="horizontal" android:weightSum="2">
    <EditText android:layout_width="0dp" android:layout_weight="1" ... />
    <EditText android:layout_width="0dp" android:layout_weight="1" ... />
</LinearLayout>
```

---

**„…se va afișa inițial 0” / „…cu textul de ghidare X”**

📄 același XML

```xml
android:text="0"          <!-- valoare inițială -->
android:hint="riddle"     <!-- text gri, vizibil DOAR când câmpul e gol -->
```

---

**„…buton aliniat la dreapta / centrat sub câmp”**

📄 același XML, pe buton

```xml
android:layout_gravity="end"                 <!-- dreapta -->
android:layout_gravity="center_horizontal"   <!-- centrat -->
```

---

### 3.2 Butoane și acțiuni

---

**„La apăsarea butonului X, valoarea din câmp să fie incrementată”**

📄 `.kt`-ul activității **în care se află butonul**, în `onCreate`

```kotlin
leftText = findViewById(R.id.left_text)
findViewById<Button>(R.id.left_button).setOnClickListener {
    leftCount++
    leftText.setText(leftCount.toString())
}
```

---

**„La apăsarea butonului Check, se compară… și se afișează un Toast”**

📄 `.kt`-ul activității **în care e butonul Check** (ex. `PlayActivity.kt`)

```kotlin
checkButton.setOnClickListener {
    if (guessText.text.toString() == answer)
        Toast.makeText(this, "Victory!", Toast.LENGTH_SHORT).show()
    else
        Toast.makeText(this, "Fail!", Toast.LENGTH_SHORT).show()
}
```

**Atenție:** `answer` trebuie să fie **deja** în această activitate → a venit prin intenție (vezi 3.4).

---

### 3.3 O activitate nouă

---

**„Să se implementeze o nouă activitate Y, cu interfața în activity_y.xml”**

📄 trei locuri – le face wizard-ul **New → Activity → Empty Views Activity**:

| Fișier | Ce apare |
|---|---|
| `YActivity.kt` (nou) | `class YActivity : AppCompatActivity()` + `setContentView(R.layout.activity_y)` |
| `res/layout/activity_y.xml` (nou) | interfața (o scrii tu) |
| `AndroidManifest.xml` | `<activity android:name=".YActivity" android:exported="false" />` |

---

**„…poate fi invocată doar prin intermediul unei intenții”**

📄 `AndroidManifest.xml` – **fără** `intent-filter` MAIN/LAUNCHER (bifa *Launcher Activity* nebifată în wizard)

---

### 3.4 Trimiterea datelor între activități

**Regula:** activitatea care **deschide** pune datele, activitatea **deschisă** le citește.

---

**„La apăsarea butonului Play (din principală) se invocă activitatea Y”**

📄 `MainActivity.kt` – acolo e butonul Play

```kotlin
findViewById<Button>(R.id.play_button).setOnClickListener {
    val intent = Intent(this, YActivity::class.java)
    startActivity(intent)                // sau launcher.launch(intent), dacă vrei rezultat (3.5)
}
```

---

**„…Y primește ghicitoarea și răspunsul” / „…afișează totalul de apăsări”**

📄 **două** fișiere:

`MainActivity.kt` – în același listener, **înainte** de pornire:
```kotlin
intent.putExtra("riddle", riddleText.text.toString())
intent.putExtra("answer", answerText.text.toString())
intent.putExtra("totalClicks", leftCount + rightCount)
```

`YActivity.kt` – în `onCreate`, după `findViewById`:
```kotlin
val riddle = intent.getStringExtra("riddle") ?: ""
val total = intent.getIntExtra("totalClicks", 0)
riddleText.setText(riddle)
```

**Atenție:** cheia (`"riddle"`) trebuie să fie **identică** în ambele fișiere. Poți s-o pui în `Constants.kt` ca să eviți greșelile.

---

### 3.5 Rezultatul înapoi

---

**„Butoanele OK/Cancel revin la activitatea din care a fost invocată, cu rezultate diferite”**

📄 `YActivity.kt` – acolo sunt butoanele OK/Cancel

```kotlin
okButton.setOnClickListener {
    setResult(RESULT_OK)                                  // sau cu date:
    // setResult(RESULT_OK, Intent().putExtra("result", "..."))
    finish()
}
cancelButton.setOnClickListener {
    setResult(RESULT_CANCELED)
    finish()
}
```

---

**„…afișând la revenire, într-un Toast, rezultatul primit”**

📄 `MainActivity.kt` – activitatea **care primește** rezultatul

1. launcher-ul, **ca câmp** al clasei:
```kotlin
private val launcher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val status = if (result.resultCode == RESULT_OK) "OK" else "CANCELED"
        Toast.makeText(this, "Result: $status", Toast.LENGTH_LONG).show()
        // date: result.data?.getStringExtra("result")
    }
```
2. în listener-ul butonului care deschide Y: `launcher.launch(intent)` **în loc de** `startActivity(intent)`.

---

### 3.6 Salvarea stării

**Regula:** salvarea se face în activitatea **a cărei stare** trebuie păstrată.

---

**„Să se forțeze sistemul să nu salveze automat starea câmpurilor”**

📄 layout-ul XML, pe fiecare câmp

```xml
android:saveEnabled="false"
```

---

**„Să se implementeze un mecanism pentru salvarea și restaurarea valorii / rezultatului”**

📄 `.kt`-ul activității respective

```kotlin
// metodă nouă în clasă
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt("leftCount", leftCount)
    outState.putString("lastResult", lastResult)
}

// în onCreate, după findViewById
if (savedInstanceState != null) {
    leftCount = savedInstanceState.getInt("leftCount")
    lastResult = savedInstanceState.getString("lastResult") ?: ""
}
leftText.setText(leftCount.toString())     // nu uita să afișezi
```

---

### 3.7 Serviciul

---

**„Să se implementeze un serviciu de tip started X”**

📄 trei locuri:

| Fișier | Ce scrii |
|---|---|
| `XService.kt` (nou, **Kotlin Class**) | `class XService : Service()` cu `onStartCommand`, `onBind = null`, `onDestroy` |
| `ProcessingThread.kt` (nou) | thread-ul cu bucla și `sleep` |
| `AndroidManifest.xml` | `<service android:name=".XService" android:exported="false" />` |

---

**„…pornit de activitatea Y când suma depășește un prag / când e lansată”**

📄 `YActivity.kt` – activitatea **care pornește** serviciul

```kotlin
// la depășirea pragului → în listener-ele butoanelor
if (leftCount + rightCount > Constants.THRESHOLD) startService(serviceIntent)

// la lansarea activității → în onCreate
startService(serviceIntent)
```

📄 `Constants.kt` – pragul: `const val THRESHOLD = 5`

---

**„…primește prin intenție numerele / răspunsul”**

📄 **două** fișiere:

`YActivity.kt` – înainte de `startService`:
```kotlin
val serviceIntent = Intent(this, XService::class.java)
serviceIntent.putExtra("firstNumber", leftCount)
serviceIntent.putExtra("answer", answer)
startService(serviceIntent)
```

`XService.kt` – în `onStartCommand`:
```kotlin
val first = intent?.getIntExtra("firstNumber", 0) ?: 0
val answer = intent?.getStringExtra("answer") ?: ""
if (processingThread == null) {
    processingThread = ProcessingThread(this, first, answer).also { it.start() }
}
```

---

**„…la fiecare N secunde propagă un mesaj care conține data, media, indiciul…”**

📄 `ProcessingThread.kt` – tot ce e **repetat** și **calculat**

```kotlin
override fun run() {
    while (isRunning) {
        val intent = Intent(Constants.ACTIONS.random())       // acțiunea
        intent.putExtra("message", "${Date()} | ...")          // conținutul
        intent.setPackage(context.packageName)
        context.sendBroadcast(intent)
        try { sleep(Constants.SLEEP_TIME) } catch (e: InterruptedException) { }
    }
}
```

📄 `Constants.kt` – acțiunile și intervalul (`ACTION_1..3`, `SLEEP_TIME = 10000L`)

---

**„Serviciul este oprit când activitatea Y / aplicația este distrusă”**

📄 **două** fișiere:

`YActivity.kt` – activitatea a cărei distrugere oprește serviciul:
```kotlin
override fun onDestroy() {
    if (!isChangingConfigurations) stopService(Intent(this, XService::class.java))
    super.onDestroy()
}
```

`XService.kt` – ca să nu mai trimită mesaje:
```kotlin
override fun onDestroy() {
    processingThread?.stopThread()
    super.onDestroy()
}
```

---

### 3.8 Broadcast și receiver

---

**„Să se implementeze un ascultător pentru mesajele cu difuzare în activitatea Y”**

📄 **două** fișiere:

`XBroadcastReceiver.kt` (nou) – **ce face** cu mesajul:
```kotlin
class XBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("[Message]", "${intent.action}: ${intent.getStringExtra("message")}")
    }
}
```

`YActivity.kt` – **când** ascultă (activitatea numită în enunț):
```kotlin
private val receiver = XBroadcastReceiver()
private val filter = IntentFilter().apply { addAction(Constants.ACTION_1) /* ... */ }

override fun onResume() {
    super.onResume()
    ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
}
override fun onPause() {
    unregisterReceiver(receiver)
    super.onPause()
}
```

**Nu** în manifest (e înregistrat dinamic).

---

**„Procesarea mesajului implică jurnalizarea / afișarea într-un Toast / într-un câmp”**

📄 `XBroadcastReceiver.kt`, în `onReceive`

```kotlin
Log.d(TAG, message)                                         // jurnalizare
Toast.makeText(context, message, Toast.LENGTH_LONG).show()  // Toast – folosești context
```

Pentru afișare **într-un câmp** din activitate: receiverul primește câmpul prin constructor –
```kotlin
class XBroadcastReceiver(private val textView: TextView) : BroadcastReceiver() { ... }
// în activitate: XBroadcastReceiver(messageTextView)
```

---

## 4. Fluxul datelor – desene

### Între activități (B / C)

```
MainActivity.kt                                    YActivity.kt
───────────────                                    ────────────
butonul Play / Navigate
  Intent(this, Y::class.java)
  putExtra("cheie", valoare)   ──── intenția ────►  onCreate:
  launcher.launch(intent)                             intent.getXExtra("cheie")
                                                      afișare în câmp

                                                    butonul OK / Check / Cancel
launcher callback:             ◄── rezultatul ────    setResult(RESULT_OK, ...)
  result.resultCode                                   finish()
  Toast
```

### Activitate → serviciu → receiver (D)

```
YActivity.kt              XService.kt             ProcessingThread.kt          XBroadcastReceiver.kt
────────────              ───────────             ───────────────────          ─────────────────────
startService(intent) ──►  onStartCommand:
  putExtra(date)            getXExtra(date)
                            ProcessingThread(...) ──► run():
                              .start()                  while:
                                                          calcule
                                                          sendBroadcast ──────►  onReceive:
                                                          sleep(N)                 Log / Toast
onResume:
  registerReceiver ─────────────────────────────────────────────────────────────► (ascultă)
onPause:
  unregisterReceiver
onDestroy:
  stopService ─────────►  onDestroy:
                            stopThread() ─────────► bucla se oprește
```

---

## 5. Var01 desfăcut pe fișiere

| Cerința | Fișier | Ce scrii |
|---|---|---|
| **A.1** repo, proiect | GitHub + wizard | — |
| **A.2** două câmpuri needitabile 50/50, butoane centrate sub ele, „0” | `activity_practical_test01_main.xml` | `LinearLayout`-uri, `0dp` + `weight`, `enabled="false"`, `text="0"` |
| **B.1** click → incrementare | `PracticalTest01MainActivity.kt` | `setOnClickListener { leftCount++; setText(...) }` |
| **B.2 a** fără salvare automată | `activity_practical_test01_main.xml` | `saveEnabled="false"` |
| **B.2 b** salvare/restaurare | `PracticalTest01MainActivity.kt` | `onSaveInstanceState` + restaurare în `onCreate` |
| **C.1** activitate nouă cu total, OK/Cancel | `PracticalTest01SecondaryActivity.kt` + `activity_practical_test01_secondary.xml` + manifest | `getIntExtra("totalClicks")`, `setResult` + `finish` |
| **C.2** buton Navigate + Toast la revenire | `activity_practical_test01_main.xml` (butonul) + `PracticalTest01MainActivity.kt` (intenție + launcher + Toast) | `putExtra("totalClicks", ...)`, `launcher.launch`, callback cu `Toast` |
| **D.1 a** serviciu, numere, mesaj la 10 s | `PracticalTest01Service.kt` + `ProcessingThread.kt` + `Constants.kt` + manifest | `onStartCommand` → thread; în thread: `Date()`, medii, `ACTIONS.random()`, `sendBroadcast`, `sleep` |
| **D.1 b** pornire la prag, oprire la distrugere | `PracticalTest01MainActivity.kt` + `Constants.kt` | `checkAndStartService()` în click-uri, `THRESHOLD`, `stopService` în `onDestroy` |
| **D.2** receiver cu jurnalizare | `PracticalTest01BroadcastReceiver.kt` + `PracticalTest01MainActivity.kt` | `onReceive` → `Log.d`; `register`/`unregister` în `onResume`/`onPause` |

---

## 6. Var08 desfăcut pe fișiere

| Cerința | Fișier | Ce scrii |
|---|---|---|
| **A.1** repo, proiect | GitHub + wizard | — |
| **A.2** buton Play pe toată lățimea, sub el două câmpuri 50/50 cu hint | `activity_practical_test01_var08_main.xml` | `Button` cu `match_parent`; rând `horizontal` cu `0dp` + `weight`; `hint` |
| **B.1** activitate nouă Play, pornită din Play, primește ghicitoarea + răspunsul | `PracticalTest01Var08MainActivity.kt` (butonul Play: `Intent` + `putExtra`) **+** `PracticalTest01Var08PlayActivity.kt` (`getStringExtra` + `setText`) + `activity_practical_test01_var08_play.xml` + manifest | trimiterea în **main**, citirea în **play** |
| **B.1** câmp needitabil cu ghicitoarea, câmp editabil, Check + Back | `activity_practical_test01_var08_play.xml` | `enabled="false"` pe ghicitoare, `0dp` + `weight` pe butoane |
| **B.2** Check → compară → Toast Victory/Fail | `PracticalTest01Var08PlayActivity.kt` | listener pe Check, `if (guess == answer)` |
| **C.1** Check/Back închid jocul cu rezultate diferite | `PracticalTest01Var08PlayActivity.kt` | `setResult(RESULT_OK, Intent().putExtra(...))` / `setResult(RESULT_CANCELED)` + `finish()` |
| **C.1** Toast cu rezultatul în principală | `PracticalTest01Var08MainActivity.kt` | launcher ca câmp + callback cu `Toast`; `launcher.launch(intent)` la Play |
| **C.2** salvarea rezultatului ultimului joc | `PracticalTest01Var08MainActivity.kt` | `lastResult` salvat în `onSaveInstanceState`, restaurat + `Toast` + `Log` |
| **D.1 a** serviciu pornit la lansarea jocului, primește răspunsul, indiciu la 5 s | `PracticalTest01Var08PlayActivity.kt` (`startService` în `onCreate` cu `putExtra(answer)`) + `PracticalTest01Var08Service.kt` + `ProcessingThread.kt` (construirea indiciului cu `*`) + `Constants.kt` + manifest | |
| **D.1 b** oprire la distrugerea jocului | `PracticalTest01Var08PlayActivity.kt` (`stopService` în `onDestroy`) + `PracticalTest01Var08Service.kt` (`onDestroy` → oprește thread-ul) | |
| **D.2** receiver în activitatea de joc, Toast + log | `PBroadcastReceiver.kt` (`onReceive`: `Toast` + `Log`) + `PracticalTest01Var08PlayActivity.kt` (`register`/`unregister`) | |

**Diferența principală față de Var01:** în Var08, aproape toată partea D e în **activitatea de joc**, nu în cea principală – pentru că enunțul spune „pornit de activitatea de joc” și „ascultător în activitatea de joc”. **Activitatea numită în enunț = fișierul în care scrii.**
