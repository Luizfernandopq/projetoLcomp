import torreGSM.iniciarTorreGSM

/*
* Siga as instruções do console para que o algoritmo retorne a resposta desejada
* */
fun main(){
//    iniciarTorreGSM()
//    val m = mutableMapOf<String, Boolean>("a" to false,
//            "a" to true)
//    println(m)

    val p = Atom("p")
    val q = Atom("q")
    val r = Atom("r")
    val s = Atom("s")

    val k = Not(Or(p,q))
    val l = Or(k,r)
    val m = Or(p, Not(q))
    val n = And(m,r)
    val o = And(l,n)
    val a = Or(o,s)

    println(tableaux(o))

}







