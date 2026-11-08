# Kotlin – fișă de sintaxă pentru colocviul EIM

> Tot ce ar putea apărea la colocviu, cu exemple comentate.
> Partea 1: Kotlin „pur”. Partea 2: lucruri specifice Android.
> **Truc:** în Android Studio, `Option + Enter` pe ceva roșu → *Import* rezolvă importurile lipsă.

---

## Cuprins

**Partea 1 – Kotlin**
1. [Variabile și tipuri](#1-variabile-și-tipuri)
2. [Null safety](#2-null-safety)
3. [String-uri](#3-string-uri)
4. [Conversii și matematică](#4-conversii-și-matematică)
5. [Numere aleatoare](#5-numere-aleatoare)
6. [if, when, intervale](#6-if-when-intervale)
7. [Bucle](#7-bucle)
8. [Array-uri](#8-array-uri)
9. [Liste](#9-liste)
10. [Map și Set](#10-map-și-set)
11. [Operații pe colecții](#11-operații-pe-colecții)
12. [Funcții și lambda](#12-funcții-și-lambda)
13. [Clase](#13-clase)
14. [apply / let / also / run](#14-apply--let--also--run)
15. [Excepții](#15-excepții)
16. [Data și ora](#16-data-și-ora)

**Partea 2 – Android**

17. [UI: View-uri, text, click-uri](#17-ui-view-uri-text-click-uri)
18. [Toast, Log, dialog](#18-toast-log-dialog)
19. [Intenții și Bundle](#19-intenții-și-bundle)
20. [Thread-uri, temporizare, UI thread](#20-thread-uri-temporizare-ui-thread)
21. [Serviciu + broadcast – șablon complet](#21-serviciu--broadcast--șablon-complet)
22. [Locația curentă](#22-locația-curentă)
23. [Senzori](#23-senzori)
24. [SharedPreferences](#24-sharedpreferences)
25. [Alte lucruri utile](#25-alte-lucruri-utile)
26. [Capcane frecvente](#26-capcane-frecvente)

---

# PARTEA 1 – KOTLIN

## 1. Variabile și tipuri

```kotlin
val x = 5                 // val = nemodificabil (ca final în Java)
var count = 0             // var = modificabil
count++                   // count = count + 1
count += 5

// tipul e dedus automat; îl poți scrie explicit cu ":"
val name: String = "Mark"
var age: Int = 22
val price: Double = 9.99
val ratio: Float = 0.5f   // f = Float
val big: Long = 10000L    // L = Long (ex. pentru Thread.sleep)
val ok: Boolean = true
val letter: Char = 'A'

// constante – doar la nivel de fișier, în object sau în companion object
const val THRESHOLD = 5

// lateinit – „o inițializez mai târziu” (doar var, doar obiecte, NU Int/Boolean/Double)
private lateinit var leftText: EditText
```

| Tip | Exemplu | Observație |
|---|---|---|
| `Int` | `42` | întreg pe 32 biți |
| `Long` | `42L` | întreg pe 64 biți |
| `Double` | `3.14` | implicit pentru zecimale |
| `Float` | `3.14f` | |
| `Boolean` | `true` / `false` | |
| `String` | `"text"` | |
| `Char` | `'a'` | |

---

## 2. Null safety

```kotlin
var a: String = "abc"     // NU poate fi null
var b: String? = null     // ? = POATE fi null

b?.length                 // safe call: dacă b e null → rezultatul e null (fără crash)
b ?: "implicit"           // elvis: dacă b e null → folosește "implicit"
b!!.length                // „sigur nu e null” → crash (NullPointerException) dacă e null – EVITĂ

val len = b?.length ?: 0  // combinat: lungimea sau 0

// verificare clasică
if (b != null) {
    println(b.length)     // smart cast: aici b e tratat ca String
}

// let: execută blocul doar dacă nu e null
b?.let { value ->
    println("Valoarea e $value")
}
```

---

## 3. String-uri

```kotlin
val name = "Mark"
val n = 7

// template-uri
"Salut, $name!"                       // variabilă simplă
"Total: ${n * 2}"                     // expresie → cu acolade
"Lungime: ${name.length}"

// formatare (ca printf)
String.format("%.2f", 3.14159)        // "3.14"
"%.2f".format(3.14159)                // "3.14" (stil Kotlin)
"%d clicks, media %.1f".format(5, 2.5)
"%05d".format(42)                     // "00042"

// operații
name.length                           // 4
name.uppercase()                      // "MARK"
name.lowercase()                      // "mark"
"  text  ".trim()                     // "text"
name.contains("ar")                   // true
name.startsWith("M")                  // true
name.substring(0, 2)                  // "Ma" (de la 0 inclusiv, până la 2 exclusiv)
name[0]                               // 'M'  (Char)
name.reversed()                       // "kraM"
"a,b,c".split(",")                    // listOf("a", "b", "c")
name.replace("M", "P")                // "Park"
"".isEmpty()                          // true
"  ".isBlank()                        // true (doar spații)
name.dropLast(1)                      // "Mar" (șterge ultimul caracter)
name.take(2)                          // "Ma"
"7".padStart(3, '0')                  // "007"

// comparare
name == "Mark"                        // true (compară conținutul, NU referința)
name.equals("mark", ignoreCase = true)

// concatenare
val s = "a" + "b" + 1                 // "ab1"
val sb = StringBuilder()
sb.append("x").append(1)
sb.toString()                         // "x1"

// string pe mai multe rânduri
val text = """
    linia 1
    linia 2
""".trimIndent()
```

---

## 4. Conversii și matematică

```kotlin
// Int ↔ String
42.toString()                // "42"
"42".toInt()                 // 42  – CRASH (NumberFormatException) dacă nu e număr
"abc".toIntOrNull()          // null – varianta sigură
"3.5".toDouble()
"3.5".toDoubleOrNull()

// între tipuri numerice
val i = 7
i.toDouble()                 // 7.0
3.9.toInt()                  // 3 (taie zecimalele)
i.toLong()
i.toFloat()

// ⚠️ împărțirea
7 / 2                        // 3   (Int / Int = Int!)
7 / 2.0                      // 3.5
7.toDouble() / 2             // 3.5
7 % 2                        // 1   (restul)

// kotlin.math (import kotlin.math.*)
sqrt(16.0)                   // 4.0   – cere Double
16.0.pow(0.5)                // 4.0   – putere
2.0.pow(10)                  // 1024.0
abs(-5)                      // 5
max(3, 7)                    // 7
min(3, 7)                    // 3
round(2.6)                   // 3.0
2.6.roundToInt()             // 3
floor(2.9)                   // 2.0
ceil(2.1)                    // 3.0
PI                           // 3.14159...
ln(E)                        // 1.0
log10(100.0)                 // 2.0

// media aritmetică și geometrică
val a = 3; val b = 4
val arithmetic = (a + b) / 2.0                 // 3.5
val geometric = sqrt((a * b).toDouble())       // 3.464...

// rotunjire la 2 zecimale pentru afișare
"%.2f".format(geometric)                       // "3.46"

// limitare într-un interval
15.coerceIn(0, 10)           // 10
```

---

## 5. Numere aleatoare

```kotlin
import kotlin.random.Random

(1..6).random()              // întreg între 1 și 6 INCLUSIV (zar)
(0 until 10).random()        // 0..9
Random.nextInt(10)           // 0..9
Random.nextInt(5, 10)        // 5..9 (capătul de sus EXCLUS)
Random.nextDouble()          // 0.0 .. 1.0
Random.nextDouble(1.0, 5.0)  // 1.0 .. 5.0
Random.nextBoolean()         // true / false

// element aleator dintr-o listă / array
listOf("A", "B", "C").random()
arrayOf(ACTION_1, ACTION_2, ACTION_3).random()

// index aleator
val list = listOf(10, 20, 30)
val index = list.indices.random()        // 0, 1 sau 2

// amestecare
listOf(1, 2, 3, 4).shuffled()

// generator cu seed (aceeași secvență la fiecare rulare)
val rng = Random(42)
rng.nextInt(100)

// listă de 5 numere aleatoare între 1 și 100
val numbers = List(5) { (1..100).random() }
```

---

## 6. if, when, intervale

```kotlin
// if ca instrucțiune
if (count > 5) {
    println("mare")
} else if (count > 0) {
    println("mic")
} else {
    println("zero")
}

// if ca expresie (în loc de operatorul ternar ?: din Java)
val status = if (resultCode == RESULT_OK) "OK" else "CANCELED"

// when = switch
when (x) {
    1 -> println("unu")
    2, 3 -> println("doi sau trei")
    in 4..10 -> println("între 4 și 10")
    !in 0..100 -> println("în afara intervalului")
    else -> println("altceva")
}

// when ca expresie
val text = when (view.id) {
    R.id.left_button -> "stânga"
    R.id.right_button -> "dreapta"
    else -> "necunoscut"
}

// when fără argument (înlocuiește if-else lung)
when {
    sum > 10 -> println("peste 10")
    sum > 5 -> println("peste 5")
    else -> println("mic")
}

// when după tip
when (obj) {
    is String -> println(obj.length)       // smart cast
    is Int -> println(obj + 1)
}

// intervale
1..5                     // 1, 2, 3, 4, 5
1 until 5                // 1, 2, 3, 4
5 downTo 1               // 5, 4, 3, 2, 1
0..10 step 2             // 0, 2, 4, 6, 8, 10
x in 1..10               // true dacă x e între 1 și 10
```

---

## 7. Bucle

```kotlin
// for pe interval
for (i in 0..4) { println(i) }           // 0..4
for (i in 0 until 5) { println(i) }      // 0..4
for (i in 10 downTo 0 step 2) { }        // 10, 8, ..., 0

// for pe colecție
val list = listOf("a", "b", "c")
for (item in list) { println(item) }

// cu index
for (i in list.indices) { println("$i: ${list[i]}") }
for ((index, value) in list.withIndex()) { println("$index: $value") }

// forEach
list.forEach { item -> println(item) }
list.forEach { println(it) }             // it = numele implicit al parametrului
list.forEachIndexed { index, item -> println("$index $item") }

// while / do-while
var n = 0
while (n < 5) { n++ }
do { n-- } while (n > 0)

// repeat
repeat(3) { i -> println("pasul $i") }

// break / continue
for (i in 1..10) {
    if (i == 3) continue                 // sare peste 3
    if (i == 8) break                    // se oprește la 8
}

// pe Map
val map = mapOf("a" to 1, "b" to 2)
for ((key, value) in map) { println("$key = $value") }
```

---

## 8. Array-uri

```kotlin
// creare
val arr = arrayOf(10, 20, 30)              // Array<Int>
val names = arrayOf("Ana", "Ion")
val zeros = IntArray(5)                    // [0, 0, 0, 0, 0]
val squares = IntArray(5) { i -> i * i }   // [0, 1, 4, 9, 16]
val empty = arrayOfNulls<String>(3)        // [null, null, null]

// acces – indexarea începe de la 0
arr[0]                    // 10 (primul)
arr[arr.size - 1]         // 30 (ultimul)
arr.last()                // 30
arr.first()               // 10
arr[1] = 25               // modificare
arr.getOrNull(10)         // null în loc de crash (ArrayIndexOutOfBounds)

// proprietăți
arr.size                  // 3
arr.indices               // 0..2
arr.isEmpty()

// căutare
arr.contains(20)          // sau: 20 in arr
arr.indexOf(20)           // 1 (-1 dacă nu există)

// calcule
arr.sum()
arr.average()             // Double
arr.max()                 // maxOf / maxOrNull pentru array-uri goale
arr.min()

// sortare
arr.sorted()              // listă nouă sortată
arr.sortedDescending()
arr.sort()                // sortează pe loc

// afișare
arr.joinToString(", ")    // "10, 25, 30"
arr.contentToString()     // "[10, 25, 30]"

// conversie
arr.toList()
arr.toMutableList()
```

**Array vs List:** array-ul are dimensiune **fixă**; pentru adăugări/ștergeri folosește `mutableListOf`.

---

## 9. Liste

```kotlin
// listă nemodificabilă
val list = listOf(1, 2, 3)

// listă modificabilă
val mlist = mutableListOf<String>()
mlist.add("a")                  // adaugă la final
mlist.add(0, "start")           // inserează la poziția 0
mlist.addAll(listOf("b", "c"))
mlist.remove("a")               // după valoare
mlist.removeAt(0)               // după index
mlist[0] = "x"                  // modificare
mlist.clear()

// acces
list[0]                         // primul element
list.first()
list.last()
list.getOrNull(5)               // null dacă nu există
list.size
list.isEmpty() / list.isNotEmpty()
list.indexOf(2)
2 in list                       // contains

// sub-listă
list.subList(0, 2)              // [1, 2]
list.take(2)                    // primele 2
list.drop(1)                    // fără primul

// ArrayList – necesar pentru putStringArrayListExtra / putIntegerArrayListExtra
val arrayList = arrayListOf("EIM", "laborator")
val fromList = ArrayList(list)

// listă generată
val evens = List(5) { i -> i * 2 }       // [0, 2, 4, 6, 8]
```

---

## 10. Map și Set

```kotlin
// Map = dicționar cheie → valoare
val ages = mapOf("Ana" to 20, "Ion" to 22)
ages["Ana"]                       // 20 (Int?, null dacă lipsește)
ages.getOrDefault("Dan", 0)       // 0
ages.containsKey("Ion")
ages.keys                         // [Ana, Ion]
ages.values                       // [20, 22]

val mmap = mutableMapOf<String, Int>()
mmap["Dan"] = 25                  // adăugare / modificare
mmap.remove("Dan")
mmap.getOrPut("Eva") { 0 }        // adaugă dacă lipsește

for ((name, age) in ages) { println("$name: $age") }

// Set = fără duplicate
val set = setOf(1, 2, 2, 3)       // [1, 2, 3]
val mset = mutableSetOf<String>()
mset.add("a")
"a" in mset
```

---

## 11. Operații pe colecții

```kotlin
val nums = listOf(5, 3, 8, 1, 9, 2)

nums.filter { it > 4 }                 // [5, 8, 9]
nums.map { it * 2 }                    // [10, 6, 16, 2, 18, 4]
nums.sum()                             // 28
nums.average()                         // 4.666...
nums.count { it % 2 == 0 }             // 2 (câte sunt pare)
nums.maxOrNull()                       // 9
nums.minOrNull()                       // 1
nums.sorted()                          // [1, 2, 3, 5, 8, 9]
nums.sortedDescending()
nums.reversed()
nums.distinct()                        // fără duplicate
nums.any { it > 8 }                    // true – există vreunul
nums.all { it > 0 }                    // true – toate
nums.none { it < 0 }                   // true – niciunul
nums.first { it > 4 }                  // 5 (crash dacă nu există)
nums.firstOrNull { it > 100 }          // null
nums.find { it > 4 }                   // = firstOrNull
nums.indexOfFirst { it > 4 }           // 0
nums.sumOf { it * 2 }                  // 56
nums.partition { it > 4 }              // Pair([5, 8, 9], [3, 1, 2])
nums.groupBy { it % 2 == 0 }           // {false=[5, 3, 1, 9], true=[8, 2]}
nums.chunked(2)                        // [[5, 3], [8, 1], [9, 2]]
nums.zip(listOf("a", "b"))             // [(5, a), (3, b)]
nums.joinToString(" | ")               // "5 | 3 | 8 | 1 | 9 | 2"
nums.reduce { acc, x -> acc * x }      // produsul

// pe obiecte
data class Student(val name: String, val grade: Int)
val students = listOf(Student("Ana", 9), Student("Ion", 7))
students.sortedBy { it.grade }
students.maxByOrNull { it.grade }?.name   // "Ana"
students.map { it.name }                  // ["Ana", "Ion"]
students.filter { it.grade >= 8 }
```

---

## 12. Funcții și lambda

```kotlin
// funcție clasică
fun sum(a: Int, b: Int): Int {
    return a + b
}

// funcție pe o singură expresie
fun sum2(a: Int, b: Int) = a + b

// fără valoare întoarsă (Unit = void)
fun greet(name: String) {
    println("Salut, $name")
}

// parametri cu valoare implicită + apel cu nume
fun format(value: Double, decimals: Int = 2) = "%.${decimals}f".format(value)
format(3.14159)                  // "3.14"
format(3.14159, decimals = 4)    // "3.1416"

// număr variabil de parametri
fun average(vararg numbers: Int) = numbers.average()
average(1, 2, 3, 4)

// funcții private (doar în clasă)
private fun checkAndStartService() { }

// lambda = funcție anonimă
val square = { x: Int -> x * x }
square(5)                        // 25

// funcție care primește o funcție
fun applyTwice(x: Int, f: (Int) -> Int) = f(f(x))
applyTwice(3) { it * 2 }         // 12

// lambda ca ultim parametru → se scrie în afara parantezelor
button.setOnClickListener { /* ... */ }
```

---

## 13. Clase

```kotlin
// clasă cu constructor primar
class Person(val name: String, var age: Int) {
    fun describe() = "$name are $age ani"
}
val p = Person("Mark", 22)
p.age = 23
p.describe()

// bloc init + constructor secundar
class Counter(var value: Int) {
    init { println("creat cu $value") }
    constructor() : this(0)        // Counter() = Counter(0)
}

// data class – equals, hashCode, toString, copy generate automat
data class Point(val x: Int, val y: Int)
val a = Point(1, 2)
val b = a.copy(y = 5)            // Point(x=1, y=5)
println(a)                       // "Point(x=1, y=2)"
val (x, y) = a                   // destructurare

// object = singleton (o singură instanță)
object Constants {
    const val TAG = "PracticalTest01"
}
Constants.TAG

// companion object = membri „statici” ai unei clase
class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE = 1
        var isVisible = false
    }
}
MainActivity.isVisible

// moștenire – clasele sunt final implicit → open
open class Animal(val name: String) {
    open fun sound() = "..."
}
class Dog(name: String) : Animal(name) {
    override fun sound() = "Ham"
}

// interfață
interface Clickable {
    fun click()
    fun info() = "implementare implicită"
}

// clasă internă cu acces la clasa exterioară
class Outer {
    private var count = 0
    inner class Inner { fun inc() { count++ } }   // fără inner → n-ar avea acces la count
}

// enum
enum class Direction { NORTH, SOUTH, EAST, WEST }
val d = Direction.NORTH
d.name                           // "NORTH"
Direction.values().random()      // direcție aleatoare

// obiect anonim (implementare pe loc a unei interfețe)
val listener = object : View.OnClickListener {
    override fun onClick(v: View) { }
}

// referință la clasă (pentru Intent)
MyService::class.java
```

---

## 14. apply / let / also / run

```kotlin
// apply – configurezi un obiect, întoarce OBIECTUL (this în bloc)
val intent = Intent(this, SecondActivity::class.java).apply {
    putExtra("total", 5)
    putExtra("name", "Mark")
}

// let – execuți ceva cu valoarea, de obicei dacă nu e null (it în bloc)
savedInstanceState?.let {
    leftCount = it.getInt("leftCount")
}

// also – faci ceva în plus, întoarce obiectul (it în bloc)
val thread = ProcessingThread(this).also { it.start() }

// run – execuți un bloc pe obiect, întoarce REZULTATUL blocului
val length = "text".run { length * 2 }

// with – ca run, dar obiectul e parametru
with(leftText) {
    setText("0")
    isEnabled = false
}
```

---

## 15. Excepții

```kotlin
try {
    val n = editText.text.toString().toInt()     // poate arunca NumberFormatException
} catch (e: NumberFormatException) {
    Toast.makeText(this, "Nu e număr!", Toast.LENGTH_SHORT).show()
} catch (e: Exception) {
    Log.e(TAG, "Eroare: ${e.message}")
} finally {
    // rulează mereu
}

// try ca expresie
val n = try { input.toInt() } catch (e: NumberFormatException) { 0 }

// mai simplu, fără try
val n2 = input.toIntOrNull() ?: 0

// aruncare
throw IllegalArgumentException("valoare invalidă")

// în thread – sleep poate fi întrerupt
try { Thread.sleep(1000) } catch (e: InterruptedException) { }
```

---

## 16. Data și ora

```kotlin
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

// cea mai simplă variantă
val now = Date()
now.toString()                                    // "Sun Nov 08 21:08:00 GMT+02:00 2026"

// formatată (merge pe orice versiune Android)
val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
sdf.format(Date())                                // "08.11.2026 21:08:00"

SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())          // "21:08"
SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())     // "2026-11-08"
SimpleDateFormat("EEEE, d MMMM", Locale("ro")).format(Date())          // "duminică, 8 noiembrie"

// timestamp (milisecunde de la 1 ianuarie 1970)
val millis = System.currentTimeMillis()
val dateFromMillis = Date(millis)

// măsurarea unui interval
val start = System.currentTimeMillis()
// ... cod ...
val elapsed = System.currentTimeMillis() - start  // în ms

// componente individuale
val cal = Calendar.getInstance()
cal.get(Calendar.YEAR)
cal.get(Calendar.MONTH) + 1                       // ⚠️ lunile încep de la 0!
cal.get(Calendar.DAY_OF_MONTH)
cal.get(Calendar.HOUR_OF_DAY)                     // 0..23
cal.get(Calendar.MINUTE)
cal.get(Calendar.SECOND)
cal.add(Calendar.DAY_OF_MONTH, 1)                 // mâine

// java.time – mai modern, dar cere minSdk 26 (Android 8)
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
```

| Literă | Înseamnă | Exemplu |
|---|---|---|
| `yyyy` | an | 2026 |
| `MM` / `MMM` / `MMMM` | lună | 11 / Nov / noiembrie |
| `dd` | zi | 08 |
| `HH` / `hh` | oră 0–23 / 1–12 | 21 / 09 |
| `mm` | **minute** (nu `MM`!) | 08 |
| `ss` | secunde | 00 |
| `a` | AM/PM | PM |
| `EEEE` | ziua săptămânii | duminică |

---

# PARTEA 2 – ANDROID

## 17. UI: View-uri, text, click-uri

```kotlin
// legare de layout – DUPĂ setContentView
private lateinit var resultText: TextView
resultText = findViewById(R.id.result_text)
val button = findViewById<Button>(R.id.my_button)

// TextView
textView.text = "Salut"                  // sau setText(...)
textView.text = count.toString()         // ⚠️ niciodată setText(count) cu Int → crash
val t = textView.text.toString()

// EditText
editText.setText("abc")                  // ⚠️ editText.text = "abc" NU compilează
val input = editText.text.toString()
val number = editText.text.toString().toIntOrNull() ?: 0   // citire sigură a unui număr
if (input.isBlank()) { editText.error = "Câmp obligatoriu" }

// activare / vizibilitate
editText.isEnabled = false
button.visibility = View.GONE            // VISIBLE / INVISIBLE / GONE
button.isClickable = false

// click
button.setOnClickListener {
    count++
    resultText.text = count.toString()
}

// click lung
button.setOnLongClickListener {
    count = 0
    true                                 // true = evenimentul a fost tratat
}

// un singur listener pentru mai multe butoane
val listener = View.OnClickListener { view ->
    when (view.id) {
        R.id.left_button -> leftCount++
        R.id.right_button -> rightCount++
    }
}
leftButton.setOnClickListener(listener)
rightButton.setOnClickListener(listener)

// textul butonului apăsat
val digit = (view as Button).text.toString()

// CheckBox / Switch
checkBox.isChecked                        // citire
checkBox.isChecked = true                 // setare
checkBox.setOnCheckedChangeListener { _, isChecked -> /* ... */ }

// RadioGroup
radioGroup.setOnCheckedChangeListener { _, checkedId ->
    when (checkedId) { R.id.radio_a -> { } }
}

// reacție la fiecare caracter tastat
editText.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable?) { /* s.toString() */ }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
})

// culoare text / fundal
textView.setTextColor(Color.RED)
textView.setBackgroundColor(Color.parseColor("#EEEEEE"))

// mărime text
textView.textSize = 24f                   // în sp

// ImageView
imageView.setImageResource(R.drawable.dice_1)
```

---

## 18. Toast, Log, dialog

```kotlin
// Toast
Toast.makeText(this, "Mesaj", Toast.LENGTH_SHORT).show()     // ⚠️ nu uita .show()
Toast.makeText(this, "Rezultat: $status", Toast.LENGTH_LONG).show()
// dintr-o inner class / callback: this@MainActivity sau applicationContext

// Log – filtrare în Logcat cu tag:NumeTag
Log.v(TAG, "verbose")
Log.d(TAG, "debug: count = $count")
Log.i(TAG, "info")
Log.w(TAG, "warning")
Log.e(TAG, "error", exception)

// thread-ul și procesul curent
Log.d(TAG, "PID: ${android.os.Process.myPid()} TID: ${android.os.Process.myTid()}")

// AlertDialog
AlertDialog.Builder(this)
    .setTitle("Confirmare")
    .setMessage("Sigur?")
    .setPositiveButton("Da") { _, _ -> /* ... */ }
    .setNegativeButton("Nu", null)
    .show()
```

---

## 19. Intenții și Bundle

```kotlin
// pornire activitate cu date
val intent = Intent(this, SecondActivity::class.java)
intent.putExtra("total", 5)                          // Int
intent.putExtra("name", "Mark")                      // String
intent.putExtra("ok", true)                          // Boolean
intent.putExtra("avg", 3.5)                          // Double
intent.putStringArrayListExtra("list", arrayListOf("a", "b"))
intent.putIntegerArrayListExtra("nums", arrayListOf(1, 2))
startActivity(intent)

// citire în activitatea pornită
val total = intent.getIntExtra("total", 0)           // 0 = implicit
val name = intent.getStringExtra("name")             // String?
val ok = intent.getBooleanExtra("ok", false)
val avg = intent.getDoubleExtra("avg", 0.0)
val list = intent.getStringArrayListExtra("list")    // ArrayList<String>?
val hasKey = intent.hasExtra("total")

// cu rezultat – launcher CA CÂMP
private val launcher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val msg = result.data?.getStringExtra("message")
        }
    }
launcher.launch(intent)

// în copil
setResult(RESULT_OK, Intent().putExtra("message", "gata"))
finish()

// intenții implicite utile
startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://upb.ro")))   // browser
startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:0722123456")))   // dialer
startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:44.43,26.10"))) // hartă
startActivity(Intent.createChooser(
    Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, "salut") },
    "Trimite cu"
))

// Bundle (salvarea stării)
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt("count", count)
    outState.putString("text", text)
    outState.putBoolean("started", started)
    outState.putStringArrayList("items", items)
}
// în onCreate
savedInstanceState?.let {
    count = it.getInt("count", 0)
    text = it.getString("text") ?: ""
}
```

---

## 20. Thread-uri, temporizare, UI thread

```kotlin
// thread simplu
Thread {
    Thread.sleep(2000)              // ⚠️ niciodată pe main thread
    runOnUiThread {                 // UI-ul se modifică DOAR de pe main thread
        resultText.text = "Gata"
    }
}.start()

// clasă Thread cu oprire
class Worker : Thread() {
    @Volatile private var running = true
    override fun run() {
        while (running) {
            // muncă
            try { sleep(1000) } catch (e: InterruptedException) { }
        }
    }
    fun stopWorker() { running = false; interrupt() }
}

// execuție întârziată pe main thread
val handler = Handler(Looper.getMainLooper())
handler.postDelayed({
    Toast.makeText(this, "După 3 secunde", Toast.LENGTH_SHORT).show()
}, 3000)

// execuție periodică pe main thread (fără thread propriu)
val tick = object : Runnable {
    override fun run() {
        seconds++
        timerText.text = seconds.toString()
        handler.postDelayed(this, 1000)          // se reprogramează
    }
}
handler.post(tick)                               // pornire
handler.removeCallbacks(tick)                    // oprire (ex. în onPause/onDestroy)

// numărătoare inversă
object : CountDownTimer(10000, 1000) {           // 10 s, tick la 1 s
    override fun onTick(millisUntilFinished: Long) {
        timerText.text = (millisUntilFinished / 1000).toString()
    }
    override fun onFinish() { timerText.text = "Gata!" }
}.start()
```

---

## 21. Serviciu + broadcast – șablon complet

```kotlin
// ── Constants.kt ──
object Constants {
    const val TAG = "PracticalTest01"
    const val ACTION_1 = "ro.pub.cs.systems.eim.practicaltest01.ACTION_1"
    const val ACTION_2 = "ro.pub.cs.systems.eim.practicaltest01.ACTION_2"
    val ACTIONS = listOf(ACTION_1, ACTION_2)
    const val DATA = "data"
}

// ── MyService.kt ──
class MyService : Service() {
    private var worker: Worker? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val value = intent?.getIntExtra("value", 0) ?: 0
        if (worker == null) worker = Worker(this, value).also { it.start() }
        return START_REDELIVER_INTENT
    }
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() { worker?.stopWorker(); worker = null; super.onDestroy() }
}

class Worker(private val ctx: Context, private val value: Int) : Thread() {
    @Volatile private var running = true
    override fun run() {
        while (running) {
            val intent = Intent(Constants.ACTIONS.random()).apply {
                putExtra(Constants.DATA, "${Date()} – $value")
                setPackage(ctx.packageName)
            }
            ctx.sendBroadcast(intent)
            try { sleep(5000) } catch (e: InterruptedException) { }
        }
    }
    fun stopWorker() { running = false; interrupt() }
}

// ── MyReceiver.kt ──
class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(Constants.TAG, "${intent.action}: ${intent.getStringExtra(Constants.DATA)}")
    }
}

// ── în activitate ──
private val receiver = MyReceiver()
private val filter = IntentFilter().apply { Constants.ACTIONS.forEach { addAction(it) } }

startService(Intent(this, MyService::class.java).putExtra("value", 42))   // pornire
stopService(Intent(this, MyService::class.java))                          // oprire

override fun onResume() {
    super.onResume()
    ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
}
override fun onPause() {
    unregisterReceiver(receiver)
    super.onPause()
}

// ── manifest ──
// <service android:name=".MyService" android:exported="false" />
```

---

## 22. Locația curentă

### Manifest

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### Cod – cu `LocationManager` (fără dependențe suplimentare)

```kotlin
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var locationText: TextView

    // primește actualizări de locație
    private val locationListener = LocationListener { location: Location ->
        showLocation(location)
    }

    // cererea de permisiune la rulare (launcher CA CÂMP)
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startLocation()
            else Toast.makeText(this, "Permisiune refuzată", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationText = findViewById(R.id.location_text)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        findViewById<Button>(R.id.location_button).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                startLocation()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")   // permisiunea e verificată înainte de apel
    private fun startLocation() {
        // 1. ultima locație cunoscută (poate fi null)
        val last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        last?.let { showLocation(it) }

        // 2. actualizări: la cel mult 5 s, la orice distanță (0 m)
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 5000L, 0f, locationListener
        )
    }

    private fun showLocation(location: Location) {
        val lat = location.latitude
        val lon = location.longitude
        locationText.text = "Lat: %.5f, Lon: %.5f".format(lat, lon)
        Log.d("Location", "$lat, $lon (precizie ${location.accuracy} m)")
    }

    override fun onPause() {
        locationManager.removeUpdates(locationListener)      // economie de baterie
        super.onPause()
    }
}
```

- **Pe emulator:** butonul `⋯` (Extended controls) → **Location** → alegi un punct → **Set location**. Fără asta, `getLastKnownLocation` întoarce de obicei `null`.
- Alte proprietăți: `location.altitude`, `location.speed`, `location.accuracy`, `location.time`.
- Distanța dintre două locații: `loc1.distanceTo(loc2)` (metri).

### Adresa din coordonate (opțional)

```kotlin
val geocoder = Geocoder(this, Locale.getDefault())
val addresses = geocoder.getFromLocation(lat, lon, 1)     // deprecated din API 33, dar funcțional
val address = addresses?.firstOrNull()?.getAddressLine(0)
```

### Varianta Google (FusedLocationProvider)

Necesită în `build.gradle.kts`: `implementation("com.google.android.gms:play-services-location:21.3.0")`.

```kotlin
val client = LocationServices.getFusedLocationProviderClient(this)
client.lastLocation.addOnSuccessListener { location: Location? ->
    location?.let { showLocation(it) }
}
```

---

## 23. Senzori

```kotlin
class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // alte tipuri: TYPE_GYROSCOPE, TYPE_LIGHT, TYPE_PROXIMITY, TYPE_MAGNETIC_FIELD
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)       // baterie
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]; val y = event.values[1]; val z = event.values[2]
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }
}
```

---

## 24. SharedPreferences

Salvare **permanentă** (supraviețuiește închiderii aplicației și repornirii telefonului).

```kotlin
val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)

// scriere
prefs.edit()
    .putInt("count", count)
    .putString("name", "Mark")
    .putBoolean("firstRun", false)
    .apply()                           // asincron; commit() = sincron

// citire
val count = prefs.getInt("count", 0)
val name = prefs.getString("name", "")
val firstRun = prefs.getBoolean("firstRun", true)

// ștergere
prefs.edit().remove("count").apply()
prefs.edit().clear().apply()
```

---

## 25. Alte lucruri utile

```kotlin
// ─── nivelul bateriei (broadcast „sticky”, fără receiver propriu) ───
val battery: Intent? = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
val percent = level * 100 / scale

// ─── conexiune la internet ───  (manifest: ACCESS_NETWORK_STATE)
val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
val caps = cm.getNetworkCapabilities(cm.activeNetwork)
val online = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

// ─── vibrație ───  (manifest: VIBRATE)
val vibrator = getSystemService(Vibrator::class.java)
vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))

// ─── informații despre dispozitiv ───
Build.MODEL                     // "Pixel 9"
Build.MANUFACTURER
Build.VERSION.SDK_INT           // nivelul API
Build.VERSION.RELEASE           // "16"

// ─── verificarea versiunii ───
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { /* Android 13+ */ }
// O = 8, P = 9, Q = 10, R = 11, S = 12, TIRAMISU = 13, UPSIDE_DOWN_CAKE = 14

// ─── resurse ───
getString(R.string.app_name)
ContextCompat.getColor(this, R.color.purple_500)
resources.displayMetrics.density          // dp → px: (16 * density).toInt()

// ─── închiderea activității ───
finish()

// ─── ascunderea tastaturii ───
val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
imm.hideSoftInputFromWindow(editText.windowToken, 0)

// ─── orientarea curentă ───
val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
```

---

## 26. Capcane frecvente

| Greșeală | Efect | Corect |
|---|---|---|
| `setText(count)` cu `Int` | crash `Resources$NotFoundException` | `setText(count.toString())` |
| `editText.text = "x"` | nu compilează | `editText.setText("x")` |
| `(a + b) / 2` cu `Int` | rezultat întreg | `/ 2.0` |
| `sqrt(a * b)` cu `Int` | nu compilează | `sqrt((a * b).toDouble())` |
| `"abc".toInt()` | crash `NumberFormatException` | `toIntOrNull() ?: 0` |
| `findViewById` înainte de `setContentView` | `NullPointerException` | întâi `setContentView` |
| `lateinit` folosit înainte de inițializare | `UninitializedPropertyAccessException` | inițializează în `onCreate` |
| `lateinit var x: Int` | nu compilează | `var x = 0` |
| `Toast.makeText(...)` fără `.show()` | nu apare nimic | `.show()` |
| `Thread.sleep` pe main thread | interfață înghețată / ANR | thread separat |
| modificarea UI-ului din alt thread | crash `CalledFromWrongThreadException` | `runOnUiThread { }` |
| `registerForActivityResult` în listener | crash `IllegalStateException` | ca câmp sau în `onCreate` |
| `"MM"` pentru minute în `SimpleDateFormat` | afișează luna | `"mm"` |
| `Calendar.MONTH` folosit direct | luna cu 1 mai mică | `+ 1` |
| `list[5]` pe o listă cu 3 elemente | `IndexOutOfBoundsException` | `getOrNull(5)` |
| `==` vs `===` | `===` compară referința | pentru conținut: `==` |
| `intent!!` în `onStartCommand` | crash la repornire cu `START_STICKY` | `intent?.getIntExtra(...) ?: 0` |
| `inner` lipsă la clasa internă | nu are acces la câmpurile clasei exterioare | `inner class` |
| `getStringExtra` pentru o valoare `Int` | `null`, fără eroare | metoda potrivită tipului |
