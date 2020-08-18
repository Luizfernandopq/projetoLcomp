package torreGSM

import And
import Atom
import Formula
import Not
import Or
import is_satisfiable
/*
* O algoritmo a seguir irá pedir alguns dados apenas na forma de números para modelagem do problema
* */
fun iniciarTorreGSM(){
    try {
        var numTorres: Int
        var numPares: Int
        do {
            println("Digite o número de torres disponíveis")
            numTorres = readLine()!!.toInt()
            println("Digite o número de pares de torres")
            numPares = readLine()!!.toInt()
        } while (numTorres <= 1 || numPares <= 0)
        calcularPossibilidades(numTorres, numPares)
    }catch (e: Exception){
        println("Insira apenas números, por favor")
        iniciarTorreGSM()
    }
}
/*
* Esta é a função central que irá imprimir o resultado
* */
fun calcularPossibilidades(numTorres: Int, numPares: Int){
    try {
        val pares: MutableList<Pair<Int, Int>> = entrada(numTorres, numPares)
        val atoms: MutableList<Atom> = criaAtomos(numTorres)
        val premissas: Formula = premissas(numTorres, atoms, pares)
        println(solucao(premissas))
    } catch (e: Exception){
        println(listOf(e.toString()))
    }
}
/*
* Esta função solicita os pares de torres sepadados apenas por espaços
* */
fun entrada(numTorres: Int, numPares: Int): MutableList<Pair<Int, Int>>{
    val pares = mutableListOf<Pair<Int, Int>>()
    println("Digite os números dos pares de torres separados por espaços")
    for (i in 0 until numPares){
        try {
            var a: Int
            var b: Int
            do {
                val read = readLine()!!.split(' ')
                val (esquerda, direita) = read.map(String::toInt)
                a = esquerda
                b = direita
            } while(!(a in 1..numTorres && b in 1..numTorres))
            pares.add(a to b)
        } catch (e: Exception){
            println("Insira todos os dados novamente de forma correta, por favor")
            return entrada(numTorres, numPares)
        }
    }
    return pares
}
/*
* Esta função retorna a lista de átomos para modelar o problema
* */
fun criaAtomos(numTorres: Int): MutableList<Atom> {
    val atoms = mutableListOf<Atom>()
    for (i in 1..numTorres){
        for (j in 1..3){
            atoms.add(Atom("${i}_f${j}"))
        }
    }
    return atoms
}
/*
* Esta função cria a modelagem do problema
* */

fun premissas(numTorres: Int, atoms: MutableList<Atom>, pares: MutableList<Pair<Int, Int>>): Formula{
    var formula: Formula? = null
    var formulaAux: Formula
    for(i in 0 until numTorres){
        formulaAux = Or(atoms[3*i], Or(atoms[3*i+1], atoms[3*i + 2]))
        formula = formula?.let { And(it, formulaAux) } ?: formulaAux
    }

    for (i in pares){
        formulaAux = Not(And(atoms[3 * (i.first - 1)], atoms[3 * (i.second - 1)]))
        formulaAux = And(formulaAux, Not(And(atoms[(3 * (i.first - 1)) + 1], atoms[(3 * (i.second - 1)) + 1])))
        formulaAux = And(formulaAux, Not(And(atoms[(3 * (i.first - 1)) + 2], atoms[(3 * (i.second - 1)) + 2])))
        formula = And(formula!!, formulaAux)
    }
    println(formula)
    return formula!!
}
/*
* Esta função retorna a solução do problema
* */

fun solucao(formula: Formula): List<String>{
    val result = is_satisfiable(formula)
    val interpretation =  mutableListOf<String>()
    val saida = mutableListOf<String>()
    return result?.let {
        for (i in it){
            if (i.value)
                interpretation.add(i.key)
        }
        println(interpretation)
        var k = 1
        for (j in interpretation) {
            if (j.startsWith("$k")){
                saida.add(j)
                k++
            }
        }
        saida
    } ?: listOf("solução impossível")
}