import torreGSM.iniciarTorreGSM
import torreGSM.iniciarTorreGSM_DPLL
import java.io.BufferedReader
import java.io.File

/*
* Siga as instruções do console para que o algoritmo retorne a resposta desejada
* */
fun main() {
//    println("TorreGSM força bruta: ")
//    iniciarTorreGSM()
//    println("\n \nTorreGSM DPLL: ")
//    iniciarTorreGSM_DPLL()
//    testeTablaux()


    val a = System.currentTimeMillis()
    testeCNF()
    println(System.currentTimeMillis() - a)
}

fun testeCNF() {
    // declaração das variáveis
    val bufferedReader: BufferedReader = File("src/Formulas_Insatisfativeis/uuf50-03.cnf").bufferedReader()
    val listLinhasFILE = mutableListOf<String>()
    val listaClausulasCNF = mutableListOf<MutableList<Int>>()
    var clausula: MutableList<Int>

    bufferedReader.useLines { lines -> lines.forEach { listLinhasFILE.add(it) } }

    val cnf = leituraCNF(listLinhasFILE)
    println(cnf)
    println(satDPLL(cnf))

}

fun leituraCNF(listaLinhasString: List<String>): List<List<Int>>{
    var clausula = mutableListOf<Int>()
    val cnf = mutableListOf<MutableList<Int>>()
    listaLinhasString.forEach {
        if (!it.startsWith("%") && !it.startsWith('c') &&
                !it.startsWith('p') && !it.startsWith('0')){
            println(it)
            clausula = listStrToListInt(it.trim().split(" ")) as MutableList<Int>
            if (clausula.isNotEmpty()){
                clausula.removeLast()
                cnf.add(clausula)
            }
        }
    }
    return cnf
}

fun listStrToListInt(listSTR: List<String>): List<Int> {
    val listINT = mutableListOf<Int>()
    listSTR.forEach {
        try {
            listINT.add(it.toInt())
        } catch (e: Exception){
            //e.printStackTrace()
            println(it)
        }
    }
    return listINT
}

fun testeTablaux(){
    val p1 = Atom("p1")
    val q1 = Atom("q1")
    val r1 = Atom("r1")
    val p2 = Atom("p2")
    val q2 = Atom("q2")
    val r2 = Atom("r2")
    val p3 = Atom("p3")
    val q3 = Atom("q3")
    val r3 = Atom("r3")

    val s1 = Atom("s")
    val t1 = Atom("t")

    val k1 = And(And(Not(Or(p2, p3)), Not(p1)), Or(Or(p2, p3), p1))
    val k2 = And(And(Not(Or(p2, p3)), p1), Or(Or(p2, p3), p1))
    //println(tableaux(k1))
    //println(tableaux(k2))

    val l1 = Or(Or(q1, q2), q3)
    val l2 = And(Not(And(q1,q2)),Not(And(p1,q3)))
    //println(tableaux(l1))
    println(tableaux(l2))

}







