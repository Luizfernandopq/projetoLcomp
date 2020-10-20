import torreGSM.iniciarTorreGSM
import torreGSM.iniciarTorreGSM_DPLL

/*
* Siga as instruções do console para que o algoritmo retorne a resposta desejada
* */
fun main() {

    println("TorreGSM força bruta: ")
    iniciarTorreGSM()
    println("\n \nTorreGSM DPLL: ")
    iniciarTorreGSM_DPLL()
//    testeTablaux()

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
    println(tableaux(k1))
    println(tableaux(k2))

    val l1 = Or(Or(q1, q2), q3)
    val l2 = Not(Or(Or(q1, q2), q3))

    println(tableaux(l1))
    println(tableaux(l2))

}







