# EIM – Teorie pentru colocviu și examen

> Fișă de recapitulare pe laboratoarele 1–5, cu accent pe **servicii și broadcast-uri**.
> La final: întrebări frecvente cu răspunsuri scurte.

---

## Cuprins

1. [Arhitectura Android](#1-arhitectura-android)
2. [Componentele unei aplicații și manifestul](#2-componentele-unei-aplicații-și-manifestul)
3. [Activități – ciclul de viață și starea](#3-activități--ciclul-de-viață-și-starea)
4. [Interfața grafică (GUI)](#4-interfața-grafică-gui)
5. [Intenții](#5-intenții)
6. [Procese, thread-uri, priorități](#6-procese-thread-uri-priorități)
7. [⭐ Servicii](#7--servicii)
8. [⭐ Broadcast-uri](#8--broadcast-uri)
9. [Notificări](#9-notificări)
10. [Permisiuni](#10-permisiuni)
11. [Întrebări frecvente cu răspunsuri](#11-întrebări-frecvente-cu-răspunsuri)

---

## 1. Arhitectura Android

De jos în sus:

| Nivel | Ce conține |
|---|---|
| **Kernel Linux** (modificat) | drivere, procese, memorie, rețea, energie; **Binder** (IPC), **Logger** (Logcat) |
| **Biblioteci native** | SQLite, WebKit, SSL, media, **Bionic** (libc-ul Android, mai mic decât glibc) |
| **Android Runtime** | **ART** – compilare **AOT** la instalare (fostul Dalvik: **JIT** la rulare); cod `.dex` |
| **Application Framework** | Activity, Service, BroadcastReceiver, ContentProvider, NotificationManager… |
| **Aplicații** | de sistem + ale utilizatorului |

- **Fiecare aplicație = un proces Linux separat** (sandbox), cu UID propriu.
- **Zygote** – proces pornit la boot, cu clasele de bază deja încărcate; aplicațiile noi se creează prin **fork** din el → pornire rapidă + memorie partajată.
- **Fără swap** → când lipsește RAM, sistemul **omoară procese** (de aici: `onDestroy()` nu e garantat, starea trebuie salvată).
- **Binder** – driver de kernel pentru IPC; aproape totul trece prin el (intenții, servicii de sistem, callback-urile ciclului de viață).
- **ActivityManagerService (AMS)** – serviciul de sistem care pornește activitățile/serviciile, livrează broadcast-urile și gestionează procesele.

---

## 2. Componentele unei aplicații și manifestul

| Componentă | Rol | Pornire |
|---|---|---|
| **Activity** | un ecran | `startActivity(intent)` |
| **Service** | operații în fundal, fără UI | `startService()` / `bindService()` |
| **BroadcastReceiver** | răspunde la mesaje broadcast | automat, la `sendBroadcast()` |
| **ContentProvider** | expune date altor aplicații | prin `ContentResolver` |

**Toate se declară în `AndroidManifest.xml`** (excepție: receiverele înregistrate dinamic din cod).
O componentă nedeclarată **nu există** pentru sistem.

```xml
<manifest ...>
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application ...>
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity android:name=".SecondaryActivity" android:exported="false" />

        <service android:name=".MyService" android:exported="false" />

        <receiver android:name=".MyReceiver" android:exported="false">
            <intent-filter>
                <action android:name="ro.pub.cs.systems.eim.MY_ACTION" />
            </intent-filter>
        </receiver>
    </application>
</manifest>
```

- `.` din `.MainActivity` = prescurtare pentru pachetul aplicației.
- `exported="true"` – alte aplicații pot porni componenta. **Obligatoriu** de declarat explicit pentru orice componentă cu `intent-filter` (Android 12+).

---

## 3. Activități – ciclul de viață și starea

### Stări

| Stare | Ce înseamnă |
|---|---|
| **Running / Resumed** | pe ecran, interactivă |
| **Paused** | parțial vizibilă (dialog deasupra) |
| **Stopped** | complet ascunsă, încă în memorie |
| **Destroyed** | eliminată; la revenire e recreată de la zero |

### Callback-uri

```
onCreate → onStart → onResume ══ RUNNING ══ onPause → onStop → onDestroy
                ↑                                       │
                └──────────── onRestart ◄───────────────┘
```

| Callback | Ce faci acolo |
|---|---|
| `onCreate` | `setContentView`, `findViewById`, listener-e, restaurarea stării |
| `onStart` / `onStop` | activitatea devine vizibilă / invizibilă |
| `onResume` / `onPause` | devine interactivă / pierde focusul → **`registerReceiver` / `unregisterReceiver`**, senzori |
| `onDestroy` | eliberare resurse, **`stopService`** |

**Regula perechilor:** ce pornești într-un callback oprești în perechea lui.

### Ce se apelează la fiecare eveniment

| Eveniment | Apeluri |
|---|---|
| Pornire | `onCreate(null)` → `onStart` → `onResume` |
| Home / Recents / ecran stins | `onPause` → `onStop` → `onSaveInstanceState` |
| Revenire | `onRestart` → `onStart` → `onResume` |
| **Rotire** | `onPause` → `onStop` → `onSaveInstanceState` → **`onDestroy`** → **`onCreate(bundle)`** → `onStart` → `onRestoreInstanceState` → `onResume` |
| Back | `onPause` → `onStop` → `onDestroy` (fără save). *Android 12+: pe activitatea principală, Back = ca Home* |
| Proces omorât de sistem | nimic garantat (nici `onDestroy`) |

### Salvarea stării

- **`onSaveInstanceState(outState: Bundle)`** – apelată înainte ca activitatea să *poată* fi distrusă (nu și la Back/`finish()`).
- `super.onSaveInstanceState()` salvează automat View-urile **care au `android:id`** (textul din `EditText`, bifa din `CheckBox`).
- **Variabilele tale** (`var count`) **nu** se salvează automat.
- `android:saveEnabled="false"` – dezactivează salvarea automată pentru un View.
- Restaurare: în `onCreate(savedInstanceState)` (verifici `!= null`) **sau** în `onRestoreInstanceState(bundle)` (apelată după `onStart`, doar dacă există stare).

| Mecanism | Rotire | Proces omorât | Închidere (Back) |
|---|---|---|---|
| `onSaveInstanceState` / Bundle | ✅ | ✅ | ❌ |
| `ViewModel` | ✅ | ❌ | ❌ |
| `SharedPreferences` / DB | ✅ | ✅ | ✅ (permanent) |
| `configChanges` în manifest | „evită” recrearea | ❌ | ❌ |

**Simulare „distrugere pentru resurse”:** Developer options → *Don't keep activities*, sau `adb shell am kill <pachet>` cu aplicația în fundal.

---

## 4. Interfața grafică (GUI)

### Atribute de bază

| Atribut | Valori / rol |
|---|---|
| `layout_width` / `layout_height` | `match_parent`, `wrap_content`, `Xdp`, `0dp` (cu weight) |
| `layout_weight` + `weightSum` | împărțire proporțională (doar în `LinearLayout`) |
| `orientation` | `vertical` / `horizontal` (implicit) |
| `layout_gravity` | poziția **elementului** în părinte |
| `gravity` | poziția **conținutului** în element |
| `layout_margin` / `padding` | spațiu exterior / interior |
| `dp` / `sp` | dimensiuni independente de densitate / text (respectă și setarea de font) |
| `visibility` | `visible`, `invisible` (ocupă loc), `gone` (nu ocupă loc) |

### Layout-uri

| Layout | Când |
|---|---|
| `LinearLayout` | elemente pe o linie (vertical/orizontal), proporții cu `weight` |
| `GridLayout` | grilă (`rowCount`, `columnCount`, `layout_row`, `layout_columnSpan`) |
| `RelativeLayout` | poziții relative (`layout_below`, `layout_toEndOf`, `layout_alignParentBottom`) |
| `FrameLayout` | elemente suprapuse |
| `ConstraintLayout` | constrângeri între elemente (template-ul implicit) |
| `ScrollView` | conținut derulabil – **un singur copil** |

Layout separat pentru landscape: `res/layout-land/` cu **același nume** de fișier.

### Cele 5 moduri de a trata un click

| Mod | Avantaj | Dezavantaj |
|---|---|---|
| Lambda: `btn.setOnClickListener { }` | scurt | logică împrăștiată dacă sunt multe butoane |
| Clasă internă (`inner class : View.OnClickListener`) + `when(view.id)` | un loc pentru toate butoanele | mai mult cod |
| Activitatea implementează `OnClickListener` | simplu | activitatea „face prea multe” |
| Clasă separată (alt fișier) | reutilizabilă | trebuie să-i dai activitatea; doar membri publici |
| `android:onClick="metoda"` în XML | fără cod de legare | nu e verificat la compilare → crash la typo; învechit |

---

## 5. Intenții

**Intenție** = mesaj asincron către sistem: „execută acțiunea X cu datele Y”. Clasa `android.content.Intent`.

Folosite pentru: pornirea activităților, pornirea serviciilor, trimiterea broadcast-urilor.

### Explicit vs implicit

| | Explicită | Implicită |
|---|---|---|
| Ce specifici | **componenta** exactă | **acțiunea** (+ date, categorie, tip) |
| Exemplu | `Intent(this, Second::class.java)` | `Intent(Intent.ACTION_VIEW, Uri.parse("https://..."))` |
| Folosire | componente din aplicația ta; **serviciile (obligatoriu din Android 5)** | delegi altor aplicații (browser, dialer, share) |
| Rezolvare | directă | sistemul caută componentele cu `intent-filter` potrivit |

### Componentele unei intenții

`component`, `action`, `data` (URI), `type` (MIME), `category`, `extras` (Bundle), `flags`.

### `intent-filter`

- Declară ce intenții **implicite** poate primi o componentă.
- Pentru activități pornite implicit cu `startActivity` → **obligatoriu `category.DEFAULT`**.
- Fără `MAIN`/`LAUNCHER` → aplicația nu apare în meniu.

### Rezultat înapoi

- Modern: **`registerForActivityResult(StartActivityForResult()) { result -> }`** + `launcher.launch(intent)`.
  - Se înregistrează **ca câmp sau în `onCreate`**, nu în listener (altfel crash).
- Copilul: `setResult(RESULT_OK / RESULT_CANCELED, dataIntent)` → `finish()`.
- Back în copil → părintele primește `RESULT_CANCELED`.
- Vechi (deprecated): `startActivityForResult(intent, requestCode)` + `onActivityResult(requestCode, resultCode, data)`.

### Flags utile

| Flag | Efect |
|---|---|
| `FLAG_ACTIVITY_NEW_TASK` | pornește activitatea într-un task nou (obligatoriu din afara unei activități, ex. din receiver) |
| `FLAG_ACTIVITY_SINGLE_TOP` | nu recrea activitatea dacă e deja în vârf → se apelează `onNewIntent()` |
| `FLAG_ACTIVITY_CLEAR_TOP` | închide activitățile de deasupra celei pornite |

---

## 6. Procese, thread-uri, priorități

- La pornirea aplicației: **un proces + un thread** (**main / UI thread**).
- **Toate componentele** (activități, servicii, receivere) rulează implicit pe **main thread**.
- Operație lungă pe main thread → interfața îngheață → **ANR** (Application Not Responding) după ~5 s (input) / ~10 s (receiver) / ~20 s (serviciu).
- **UI-ul se modifică doar de pe main thread** → din alt thread: `runOnUiThread { }`, `Handler(Looper.getMainLooper()).post { }`, sau un broadcast.

### Ierarhia proceselor (cine e omorât primul = ultimul din listă)

1. **Foreground** – activitate în prim-plan, foreground service, receiver în `onReceive()`
2. **Visible** – activitate vizibilă, dar fără focus (Paused)
3. **Service** – proces cu un serviciu started
4. **Cached / Background** – doar activități oprite (Stopped)
5. **Empty** – nicio componentă activă

---

## 7. ⭐ Servicii

### 7.1 Ce e un serviciu

- Componentă **fără UI**, pentru operații **de lungă durată** în fundal (rețea, muzică, I/O, sincronizare).
- **Continuă să ruleze** după ce activitatea care l-a pornit dispare.
- **NU are thread propriu** – rulează pe **main thread**. Pentru munca lungă pornești **tu** un thread.
- Nu are ciclul de viață al activității (nici `onResume`, nici `onPause`).
- Poate fi pornit din activități, receivere sau alte servicii.

### 7.2 Tipuri

| Tip | Pornire | Cât trăiește | Comunicare | Exemplu |
|---|---|---|---|---|
| **Started** | `startService(intent)` → `onStartCommand()` | până la `stopService()` / `stopSelf()` | unidirecțională (intent → serviciu); înapoi prin broadcast / `PendingIntent` | upload, trimitere periodică |
| **Bound** | `bindService(intent, conn, flags)` → `onBind()` | cât timp există **cel puțin un client** legat | **bidirecțională**, client-server prin `IBinder` (și IPC) | player controlat din UI, API intern |
| **Foreground** | `startForegroundService()` + `startForeground(id, notif)` | ca started | ca started | muzică, navigație, fitness |
| **Background** | started, fără notificare | ca started, dar **restricționat din Android 8** | — | sarcini scurte |

- Un serviciu poate fi **și started, și bound** – se oprește abia când e oprit explicit **și** nu mai are clienți.

### 7.3 Started vs Bound – comparație

| | Started | Bound |
|---|---|---|
| Callback de pornire | `onStartCommand()` | `onBind()` |
| Se oprește | explicit (`stopService`/`stopSelf`) | automat, când ultimul client face `unbindService` |
| Legătura cu cine l-a pornit | niciuna după pornire | client conectat prin `ServiceConnection` |
| `onBind()` întoarce | `null` | un `IBinder` |
| Prioritate | mai mare decât o activitate oprită | a clientului (activ → mare; inactiv → mică) |

### 7.4 Ciclul de viață

```
STARTED                                 BOUND
───────                                 ─────
startService()                          bindService()
     │                                       │
 onCreate()  ← o singură dată            onCreate()
     │                                       │
 onStartCommand() ← la FIECARE           onBind()  ← primul client
     │              startService()           │
  [rulează]                              [clienți legați]
     │                                       │
stopService() / stopSelf()              unbindService() (ultimul client)
     │                                       │
 onDestroy()                             onUnbind()
                                             │
                                         onDestroy()
```

| Callback | Când | Observații |
|---|---|---|
| `onCreate()` | prima pornire | o singură dată; inițializări |
| `onStartCommand(intent, flags, startId)` | la **fiecare** `startService()` | aici pornești thread-ul; `intent` poate fi `null` |
| `onBind(intent)` | `bindService()` | **trebuie implementat mereu** (abstract); `null` = fără binding |
| `onUnbind(intent)` | toți clienții s-au dezlegat | întoarce `true` → la reconectare se apelează `onRebind()` |
| `onRebind(intent)` | client nou după `onUnbind` care a întors `true` | |
| `onDestroy()` | ultimul apel | oprești thread-uri, dezînregistrezi receivere |
| `onTaskRemoved(intent)` | utilizatorul a închis aplicația din Recents | opțional |

### 7.5 Valoarea întoarsă de `onStartCommand()`

Ce face sistemul dacă **omoară** serviciul din lipsă de memorie:

| Constantă | Repornește? | `intent` primit la repornire | Când o folosești |
|---|---|---|---|
| `START_NOT_STICKY` | ❌ | — | munca poate fi reluată mai târziu de aplicație |
| `START_STICKY` | ✅ | **`null`** | serviciu care rulează continuu, fără date în intent (ex. player) |
| `START_REDELIVER_INTENT` | ✅ | **ultima intenție** | serviciul depinde de datele din intent (ex. colocviul) |

### 7.6 `startId` și `stopSelf`

- `stopSelf()` – oprește serviciul imediat.
- `stopSelf(startId)` – oprește **doar dacă** `startId` e ultimul primit → nu întrerupe o cerere sosită între timp.
- `stopService(intent)` – oprire din exterior (activitate).
- Toate duc la `onDestroy()` (dacă nu mai sunt clienți legați).

### 7.7 Declararea în manifest

```xml
<service
    android:name=".MyService"
    android:enabled="true"
    android:exported="false"
    android:foregroundServiceType="dataSync"
    android:permission="ro.pub...MY_PERMISSION" />
```

| Atribut | Rol |
|---|---|
| `name` | **singurul obligatoriu** |
| `enabled` | poate fi instanțiat (implicit `true`) |
| `exported` | alte aplicații îl pot porni (implicit `false` fără `intent-filter`) |
| `permission` | permisiunea cerută celui care îl pornește/leagă |
| `foregroundServiceType` | obligatoriu pentru foreground pe Android 14+ |
| `process=":remote"` | rulează în proces separat |

### 7.8 Pornire și restricții de versiune

```kotlin
// explicit (OBLIGATORIU pentru servicii, din Android 5)
startService(Intent(this, MyService::class.java).putExtra("key", 42))

// serviciu din ALTĂ aplicație
val intent = Intent().apply {
    component = ComponentName("pachet.app", "pachet.app.MyService")
}
```

| Versiune | Restricție |
|---|---|
| **Android 5** | serviciile se pornesc **doar cu intenții explicite** |
| **Android 8 (Oreo)** | aplicație **în fundal** → `startService()` aruncă `IllegalStateException`; trebuie `startForegroundService()` + `startForeground()` în **~5 s** (altfel crash) |
| **Android 8** | serviciile de fundal ale unei aplicații trecute în background sunt oprite după ~1 min |
| **Android 13** | notificările cer permisiunea `POST_NOTIFICATIONS` la rulare |
| **Android 14** | `foregroundServiceType` obligatoriu + permisiunea `FOREGROUND_SERVICE_<TIP>` |

- O aplicație **în prim-plan** (activitate vizibilă) **poate** folosi `startService()` simplu → cazul colocviului.

### 7.9 Foreground service – pașii

1. Manifest:
   ```xml
   <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
   <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
   <service android:name=".MyService" android:foregroundServiceType="dataSync" />
   ```
2. Pornire: `startForegroundService(intent)` (API 26+).
3. În serviciu, în `onStartCommand`, **în 5 secunde**:
   ```kotlin
   val channel = NotificationChannel("ch", "Service", NotificationManager.IMPORTANCE_LOW)
   getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
   val notification = Notification.Builder(this, "ch")
       .setContentTitle("Serviciu activ")
       .setSmallIcon(android.R.drawable.ic_media_play)   // fără iconiță → notificare invalidă
       .build()
   startForeground(1, notification)
   ```
4. Oprire mod foreground: `stopForeground(STOP_FOREGROUND_REMOVE)`.

### 7.10 Bound service – exemplu complet (local)

```kotlin
class LocalService : Service() {

    // Binder-ul prin care clientul primește o referință la serviciu
    inner class LocalBinder : Binder() {
        fun getService(): LocalService = this@LocalService
    }
    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder = binder

    // metodă publică apelată direct de client
    fun getRandomNumber(): Int = (0..100).random()
}
```

```kotlin
// în activitate
private var service: LocalService? = null

private val connection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        service = (binder as LocalService.LocalBinder).getService()
    }
    override fun onServiceDisconnected(name: ComponentName) {
        service = null       // doar la crash-ul serviciului, NU la unbind
    }
}

override fun onStart() {
    super.onStart()
    bindService(Intent(this, LocalService::class.java), connection, Context.BIND_AUTO_CREATE)
}

override fun onStop() {
    unbindService(connection)
    super.onStop()
}

// folosire: service?.getRandomNumber()
```

- `BIND_AUTO_CREATE` – creează serviciul dacă nu există.
- Între procese diferite → **AIDL** sau **Messenger**.

### 7.11 Thread-uri în serviciu – variante

| Variantă | Avantaj | Dezavantaj |
|---|---|---|
| **`Thread` propriu** (`ProcessingThread`) | simplu, control total | pornire/oprire manuală; flag `@Volatile` + `interrupt()` |
| **`HandlerThread` + `Handler`** | coadă de mesaje, cereri procesate una câte una | mai mult cod |
| **`IntentService`** | thread automat, secvențial, se oprește singur | **deprecated (API 30)**; fără paralelism; nu e pentru muncă continuă |
| **`JobIntentService`** | înlocuitorul compatibil cu Android 8 | tot deprecated |
| **Coroutines** | concis, ieftin (fără schimbare de context la nivel de OS) | dependență + alt model |
| **WorkManager** | muncă garantată (supraviețuiește restart-urilor), recomandat de Google | minim 15 min pentru periodice; nu e „live” |
| **`ExecutorService`** | pool de thread-uri | gestionare manuală a închiderii |

**Oprirea corectă a unui thread:**
```kotlin
@Volatile private var isRunning = true
override fun run() { while (isRunning) { work(); try { sleep(10000) } catch (e: InterruptedException) {} } }
fun stopThread() { isRunning = false; interrupt() }   // interrupt() îl trezește din sleep
```

### 7.12 Unde oprești serviciul (pornit dintr-o activitate)

| Loc | Avantaj | Dezavantaj |
|---|---|---|
| `onDestroy()` activitate | serviciul trăiește cât activitatea, și în fundal | `onDestroy` nu e garantat; e apelat și la rotire (→ `isChangingConfigurations`) |
| `onPause()` / `onStop()` | resurse consumate doar cât e vizibilă | pierde rostul unui serviciu de fundal |
| buton „Stop” | control explicit | utilizatorul poate uita → consum baterie |
| `stopSelf()` în serviciu | ideal pentru sarcini finite | nu merge pentru bucle infinite |

### 7.13 Serviciu vs Thread – când ce

- **Thread** – munca e legată de o activitate și se oprește odată cu ea.
- **Serviciu** – munca trebuie să continue **independent** de UI (și sistemul trebuie să știe că aplicația face ceva → prioritate mai mare a procesului).

---

## 8. ⭐ Broadcast-uri

### 8.1 Ce sunt

- IPC de tip **publish–subscribe**: expeditorul nu știe cine primește.
- **De sistem:** `BOOT_COMPLETED`, `BATTERY_LOW`, `ACTION_POWER_CONNECTED`, `AIRPLANE_MODE_CHANGED`, `SCREEN_ON/OFF`, `CONNECTIVITY_CHANGE`…
- **Proprii:** acțiuni definite de tine (`"ro.pub...ACTION_1"`).
- Intern: **PackageManagerService** știe cine ascultă ce; **AMS** livrează prin Binder.
- Livrarea **nu e garantată în timp real** → pentru comunicare rapidă: bound services.

### 8.2 Trimiterea

```kotlin
val intent = Intent("ro.pub.cs.systems.eim.ACTION_1").apply {
    putExtra("message", "salut")
    setPackage(packageName)      // opțional: doar aplicația ta / o aplicație anume
}
sendBroadcast(intent)
```

| Tip | Metodă | Comportament |
|---|---|---|
| **Normal** | `sendBroadcast()` | asincron, toți receiverii primesc în paralel, ordine nedefinită |
| **Ordered** | `sendOrderedBroadcast()` | pe rând, după `android:priority`; un receiver poate modifica rezultatul sau opri propagarea (`abortBroadcast()`) |
| **Local** (vechi) | `LocalBroadcastManager` | doar în aplicație – **deprecated** |
| **Cu permisiune** | `sendBroadcast(intent, "permisiune")` | primesc doar receiverii care au permisiunea |

### 8.3 Primirea

```kotlin
class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // main thread, ~10 s limită → doar operații scurte
        Log.d("TAG", "${intent.action}: ${intent.getStringExtra("message")}")
    }
}
```

| | **Static** (manifest) | **Dinamic** (cod) |
|---|---|---|
| Declarare | `<receiver>` + `<intent-filter>` | `registerReceiver(receiver, filter, flag)` |
| Primește | chiar cu aplicația **închisă** (sistemul o pornește) | doar între `register` și `unregister` |
| Instanțiere | de **sistem**, constructor **fără parametri** | de tine, cu orice parametri (ex. un `TextView`) |
| Android 8+ | **NU** mai primește broadcast-uri **implicite** (excepții: `BOOT_COMPLETED`, `LOCALE_CHANGED`…) → trebuie `setPackage()` | primește tot ce se potrivește |
| Când | evenimente de sistem, notificări cu aplicația închisă | actualizarea UI-ului |

### 8.4 Înregistrarea dinamică

```kotlin
private val receiver = MyReceiver()
private val filter = IntentFilter().apply { addAction(ACTION_1); addAction(ACTION_2) }

override fun onResume() {
    super.onResume()
    ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
}
override fun onPause() {
    unregisterReceiver(receiver)
    super.onPause()
}
```

| Flag (Android 14+, obligatoriu) | Primește de la |
|---|---|
| `RECEIVER_NOT_EXPORTED` | doar propria aplicație (+ sistem) |
| `RECEIVER_EXPORTED` | și alte aplicații |

- Lipsa flag-ului pe Android 14+ → `SecurityException`.
- `unregisterReceiver` pe un receiver neînregistrat → `IllegalArgumentException`.
- Uitarea lui `unregisterReceiver` → **memory leak** („Activity has leaked IntentReceiver”).

### 8.5 Muncă lungă în `onReceive`

- `onReceive` e scurt (main thread, ~10 s).
- Variante: `goAsync()` (câteva secunde în plus pe alt thread), pornirea unui serviciu, sau `WorkManager`.

### 8.6 Testare din adb

```bash
adb shell am broadcast -a ro.pub.cs.systems.eim.ACTION_1 --es message "test" -p ro.pub.cs.systems.eim.app
# --es String, --ei int, --ez boolean, --ef float; -p = pachetul destinatar
```

---

## 9. Notificări

```kotlin
// 1. canal (obligatoriu din Android 8)
val channel = NotificationChannel("ch_id", "Nume canal", NotificationManager.IMPORTANCE_DEFAULT)
val manager = getSystemService(NotificationManager::class.java)
manager.createNotificationChannel(channel)

// 2. ce se întâmplă la tap
val pendingIntent = PendingIntent.getActivity(
    this, 0,
    Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

// 3. notificarea
val notification = Notification.Builder(this, "ch_id")
    .setSmallIcon(android.R.drawable.ic_dialog_info)   // obligatoriu
    .setContentTitle("Titlu")
    .setContentText("Text")
    .setContentIntent(pendingIntent)
    .setAutoCancel(true)                               // dispare la tap
    .build()

manager.notify(1, notification)                         // același ID → înlocuiește
```

- **`PendingIntent`** – intent „pregătit”, executat de sistem **în numele aplicației** (ex. la tap). Necesar pentru că din Android 10 aplicațiile din fundal **nu pot porni activități direct**.
- `FLAG_IMMUTABLE` / `FLAG_MUTABLE` – **obligatoriu** unul dintre ele din Android 12.
- `FLAG_UPDATE_CURRENT` – actualizează extras-urile unui `PendingIntent` existent.
- Android 13+: permisiunea **`POST_NOTIFICATIONS`** cerută la rulare.

---

## 10. Permisiuni

| Tip | Exemple | Cum se obțin |
|---|---|---|
| **Normale** | `INTERNET`, `VIBRATE`, `FOREGROUND_SERVICE` | doar în manifest, acordate automat |
| **Periculoase** (dangerous) | `CALL_PHONE`, `CAMERA`, `ACCESS_FINE_LOCATION`, `READ_CONTACTS`, `POST_NOTIFICATIONS` | manifest **+** cerere la rulare (Android 6+) |
| **Speciale** | `SYSTEM_ALERT_WINDOW`, `SCHEDULE_EXACT_ALARM` | din Setări, de către utilizator |

```kotlin
private val permissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) { /* ok */ } else { /* refuzată */ }
    }

if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        != PackageManager.PERMISSION_GRANTED) {
    permissionLauncher.launch(Manifest.permission.CALL_PHONE)
}
```

---

## 11. Întrebări frecvente cu răspunsuri

**Pe ce thread rulează un serviciu?**
Pe main thread. Munca lungă trebuie mutată pe un thread separat, altfel apare ANR.

**De ce e nevoie de un thread dedicat în serviciu?**
Ca să nu blochezi main thread-ul (UI) – `sleep`/rețea/I/O pe main → interfață înghețată → ANR.

**Diferența started vs bound?**
Started: pornit cu `startService`, rulează independent până e oprit explicit, comunicare unidirecțională. Bound: pornit cu `bindService`, trăiește cât are clienți, comunicare bidirecțională prin `IBinder`.

**De ce `onBind()` trebuie implementat și la un started service?**
E metodă abstractă în `Service`. Întorci `null` dacă nu permiți binding.

**Ce înseamnă `START_STICKY` / `NOT_STICKY` / `REDELIVER_INTENT`?**
Dacă sistemul omoară serviciul: îl repornește cu `intent = null` / nu-l repornește / îl repornește cu ultima intenție.

**De câte ori se apelează `onCreate` și `onStartCommand` la 3 apeluri `startService`?**
`onCreate` – o dată; `onStartCommand` – de 3 ori.

**Ce restricții a adus Android 8?**
Aplicațiile din fundal nu mai pot porni servicii obișnuite (doar foreground, cu notificare în 5 s); receiverele din manifest nu mai primesc broadcast-uri implicite; canale de notificare obligatorii.

**Ce e un foreground service și de ce are notificare?**
Un serviciu cu prioritate aproape maximă (aproape niciodată omorât); notificarea obligatorie informează utilizatorul că aplicația lucrează în fundal.

**De ce serviciile se pornesc doar cu intenții explicite?**
Securitate: cu intenții implicite, nu ai ști ce serviciu pornești, iar utilizatorul nu vede serviciile.

**Alternativa la thread-ul dedicat? Avantaj/dezavantaj?**
`IntentService` – thread automat și oprire automată, dar procesare secvențială, deprecated. Azi: WorkManager / coroutines.

**Ce e un `BroadcastReceiver`? Static vs dinamic?**
Componentă care reacționează la broadcast-uri. Static (manifest): primește și cu aplicația închisă, fără broadcast-uri implicite din Android 8. Dinamic (cod): doar cât e înregistrat, util pentru UI.

**De ce înregistrezi receiverul în `onResume` și nu în `onCreate`?**
Ca să primească mesaje doar când activitatea e vizibilă și să fie simetric cu `unregisterReceiver` din `onPause` (fără leak-uri, fără dublă înregistrare).

**`RECEIVER_EXPORTED` vs `RECEIVER_NOT_EXPORTED`?**
Acceptă broadcast-uri și din alte aplicații / doar din propria aplicație. Obligatoriu de specificat pe Android 14+.

**De ce `onReceive` trebuie să fie scurt?**
Rulează pe main thread cu limită de ~10 s; după aceea → ANR, iar procesul poate fi omorât.

**Ce e un `PendingIntent`?**
O intenție executată ulterior de sistem în numele aplicației (notificări, alarme), chiar dacă aplicația nu rulează.

**De ce nu e garantat `onDestroy()`?**
Fără swap, sistemul poate omorî procesul direct când are nevoie de memorie, fără să apeleze callback-uri.

**Ce se întâmplă la rotire?**
Schimbare de configurație → activitatea e distrusă și recreată; câmpurile se pierd dacă nu sunt salvate în Bundle.

**Ce face `saveEnabled="false"`?**
Dezactivează salvarea automată a stării View-ului (ex. textul din `EditText`).

**Explicit vs implicit?**
Explicită: componenta exactă (clasa). Implicită: acțiunea; sistemul alege componenta după `intent-filter`.

**De ce `category.DEFAULT` în `intent-filter`?**
`startActivity()` adaugă automat categoria DEFAULT la intențiile implicite; fără ea în filtru, activitatea nu se potrivește.

**Ce e Binder? De ce nu IPC-ul clasic Linux?**
Driver de kernel pentru IPC. System V IPC nu e suportat de Bionic, iar mecanismele clasice lasă resurse ocupate la moartea proceselor și au probleme de securitate. Binder: număr de referințe, UID/PID al expeditorului, apeluri remote ca locale.

**Ce e Zygote?**
Procesul din care se clonează (fork) toate aplicațiile; are clasele de bază încărcate → pornire rapidă + memorie partajată.
