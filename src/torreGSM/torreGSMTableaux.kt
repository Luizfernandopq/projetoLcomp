package torreGSM

import Atom
import Formula
import tableaux

fun iniciarTorreGSMTableaux(){
    try {
        var numTorres: Int
        var numPares: Int
        do {
            println("Digite o número de torres disponíveis")
            numTorres = readLine()!!.toInt()
            println("Digite o número de pares de torres")
            numPares = readLine()!!.toInt()
        } while (numTorres <= 1 || numPares <= 0)
        calcularPossibilidadesTableaux(numTorres, numPares)
    }catch (e: Exception){
        println("Insira apenas números, por favor")
        iniciarTorreGSMTableaux()
    }
}

fun torreGSMSemEntradaTableaux(numTorres: Int, pares: MutableList<Pair<Int, Int>>){
    try {
        /*
        * aqui começa o a resolução do problema
        * */
        val time = System.currentTimeMillis()
        val atoms: MutableList<Atom> = criaAtomos(numTorres)
        val premissas: Formula = premissas(numTorres, atoms, pares)
        println(solucaoTableaux(premissas))
        println("Tableaux: Cálculos realidados em ${System.currentTimeMillis() - time} milissegundos")
    } catch (e: Exception){
        println(listOf(e.toString()))
    }
}

fun calcularPossibilidadesTableaux(numTorres: Int, numPares: Int){
    try {
        val pares: MutableList<Pair<Int, Int>> = entrada(numTorres, numPares)

        /*
        * aqui começa o a resolução do problema
        * */
        val time = System.currentTimeMillis()
        val atoms: MutableList<Atom> = criaAtomos(numTorres)
        val premissas: Formula = premissas(numTorres, atoms, pares)
        println(solucaoTableaux(premissas))
        println("Programa finalizado. Cálculos realidados em ${System.currentTimeMillis() - time} milissegundos")
    } catch (e: Exception){
        println(listOf(e.toString()))
    }
}

fun solucaoTableaux(premissas: Formula) = tableaux(premissas)!!.filter { it.value }