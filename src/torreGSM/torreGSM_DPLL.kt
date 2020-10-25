package torreGSM

import satDPLL


fun iniciarTorreGSM_DPLL(){
    try {
        var numTorres: Int
        var numPares: Int
        do {
            println("Digite o número de torres disponíveis")
            numTorres = readLine()!!.toInt()
            println("Digite o número de pares de torres")
            numPares = readLine()!!.toInt()
        } while (numTorres <= 1 || numPares <= 0)
        calcularPossibilidadesDPLL(numTorres, numPares)
    }catch (e: Exception){
        println("Insira apenas números, por favor")
        iniciarTorreGSM_DPLL()
    }
}

fun torreGSMSemEntradaDPLL(numTorres: Int, pares: MutableList<Pair<Int, Int>>){
    try {
        val time = System.currentTimeMillis()
        val premissas: List<MutableList<Int>> = premissasDPLL(numTorres, pares)
        var list = solucaoDPLL(premissas)
        if (list.isNotEmpty()) {
            list = list as MutableList<Int>
            list.removeIf {
                it < 0
            }
            for (i in list)
                print("torre: ${i/10} na frequência ${i%10} \t")
        }else{
            print("\nSolução impossivel!")
        }
        println("\nDPLL: Cálculos realidados em ${System.currentTimeMillis() - time} milissegundos")
    } catch (e: Exception){
        println("Erro: $e")
    }
}

fun calcularPossibilidadesDPLL(numTorres: Int, numPares: Int){
    try {
        val pares: MutableList<Pair<Int, Int>> = entrada(numTorres, numPares)
        val time = System.currentTimeMillis()
        val premissas: List<MutableList<Int>> = premissasDPLL(numTorres, pares)
        var list = solucaoDPLL(premissas)
        if (list.isNotEmpty()) {
            list = list as MutableList<Int>
            list.removeIf {
                it < 0
            }
            for (i in list)
                println("torre: ${i/10} na frequência ${i%10}")
        }else{
            println("Solução impossivel!")
        }
        println("Programa finalizado. Cálculos realidados em ${System.currentTimeMillis() - time} milissegundos")
    } catch (e: Exception){
        println("Erro: $e")
    }
}

fun premissasDPLL(numTorres: Int, pares: MutableList<Pair<Int, Int>>): List<MutableList<Int>>{
    val premissa = mutableListOf<MutableList<Int>>()
    for (i in 1..numTorres){
        val clausula = mutableListOf<Int>(i*10 + 1, i*10 + 2, i*10 + 3)
        premissa.add(clausula)
    }
    //println(premissa)
    for (i in pares) {
        val clausula1 = mutableListOf<Int>(-(i.first*10 + 1), -(i.second*10 + 1))
        val clausula2 = mutableListOf<Int>(-(i.first*10 + 2), -(i.second*10 + 2))
        val clausula3 = mutableListOf<Int>(-(i.first*10 + 3), -(i.second*10 + 3))
        premissa.add(clausula1)
        premissa.add(clausula2)
        premissa.add(clausula3)
    }
    println(premissa)
    return premissa
}

fun solucaoDPLL(premissas: List<MutableList<Int>>): List<Int> {
    return satDPLL(premissas)
}
