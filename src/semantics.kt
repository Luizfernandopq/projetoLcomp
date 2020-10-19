fun truth_value(formula: Formula, interpretation: Map<String, Boolean>): Boolean {
    //println(interpretation)
    //println(formula)
    return when (formula) {
        is Atom -> interpretation[formula.proposicao]!!
        is Not -> !truth_value(formula.proposicao, interpretation)!!
        is And -> truth_value(formula.left, interpretation) && truth_value(formula.right, interpretation)
        is Or -> truth_value(formula.left, interpretation) || truth_value(formula.right, interpretation)
        is Implies -> !truth_value(formula.left, interpretation) || truth_value(formula.right, interpretation)
        is OnlyIf -> !(!(truth_value(formula.left, interpretation)) && truth_value(formula.right, interpretation)) || ((truth_value(formula.left, interpretation)) && !(truth_value(formula.right, interpretation)))
        is Xor -> (!(truth_value(formula.left, interpretation)) && truth_value(formula.right, interpretation)) || ((truth_value(formula.left, interpretation)) && !(truth_value(formula.right, interpretation)))
        else -> error("não é uma fórmula")
    }
}


/*
* ATENÇÃO!
* A função is_satisfiable pode retornar nulo
* */
fun is_satisfiable(formula: Formula): MutableMap<String, Boolean>? {
    val allatoms: MutableSet<Formula> = atoms(formula)
    return satisfiability_check(formula, allatoms, mutableMapOf())
}


private fun satisfiability_check(formula: Formula, allAtoms: MutableSet<Formula>, interpretation: MutableMap<String, Boolean>): MutableMap<String, Boolean>? {
    //println(interpretation)
    //println(allAtoms)
    val natoms = mutableSetOf<Formula>()
    natoms.addAll(allAtoms)
    if (allAtoms.isEmpty())
        return if (truth_value(formula, interpretation)) interpretation else null
    else {
        val (atom) = allAtoms.take(1)
        natoms.remove(atom)

        val interpretation1 = mutableMapOf<String, Boolean>()
        interpretation1 += interpretation
        interpretation1.put(atom.toString(), true)

        val interpretation2 = mutableMapOf<String, Boolean>()
        interpretation2 += interpretation
        interpretation2.put(atom.toString(), false)

        //println("$interpretation1 ; $interpretation2")

        val result = satisfiability_check(formula, natoms, interpretation2)

        return satisfiability_check(formula, natoms, interpretation1) ?: result


    }
}

fun tableaux(formula: Formula): MutableMap<String, Boolean>?{
    val atoms = solveTableaux(formula, setOf<Formula>())
    println(formula)

    return complementaInterpretacao(atoms)
}

private fun solveTableaux(formula: Formula, allAtoms: Set<Formula>?): MutableSet<Formula>{
    //nova variável para evitar que as mudanças repercutam em outras chamadas
    val newAtoms: MutableSet<Formula> = mutableSetOf()
    if (!allAtoms.isNullOrEmpty())
        newAtoms.addAll(allAtoms as MutableSet<Formula>)
//    println(formula)
//    println(newAtoms)

    return when(formula){
        is Atom ->{
            //se já existe negação desse átomo em lista
            if (Not(formula) in newAtoms) {
                //retorna uma lista vazia pois é insatistatível
                mutableSetOf<Formula>()
            } else {
                //adiciona átomo à lista
                newAtoms.add(formula)
                newAtoms
            }
        }
        is Not -> {
            //se o que está dentro da negação é átomo
            if (formula.proposicao is Atom){
                //se já existe negação desse átomo em lista
                if (formula.proposicao in newAtoms){
                    //retorna uma lista vazia pois é insatistatível
                    mutableSetOf<Formula>()
                } else {
                    //adiciona negação de átomo à lista
                    newAtoms.add(formula)
                    newAtoms
                }
            } else {
                //aplicação de deMorgan
                val newformula = deMorgan(formula)
                //resolvendo a nova fórmula
                solveTableaux(newformula, newAtoms)
            }
        }
        is And -> {
            // puxando uma lista de átomos à esquerda do "And"
            var atomsAux = solveTableaux(formula.left, newAtoms)
            // se atomsAux for vazio a fórmula é insatisfatível e não entra no if
            if (atomsAux.isNotEmpty()){
                //resolve a parte da direita já com os átomos da esquerda
                atomsAux = solveTableaux(formula.right, atomsAux) as MutableSet<Formula>
                //se for insatisfatível podemos começar pelo outro lado
                if (atomsAux.isEmpty()){
                    // puxando uma lista de átomos à direita do "And"
                    atomsAux = solveTableaux(formula.right,newAtoms)
                    if (atomsAux.isNotEmpty()) {
                        //resolve a parte da direita já com os átomos da esquerda
                        atomsAux = solveTableaux(formula.left, atomsAux)
                    }
                }
            }
            //ou sendo satisfatível ou não, atomsAux ou terá átomos ou será vazio, respectivamente
            atomsAux
        }
        is Or -> {
            // puxando uma lista de átomos à esquerda do "Or"
            val atomsAux = solveTableaux(formula.left, newAtoms)
            // se atomsAux for vazio a fórmula à esquerda é insatisfatível e tem que entrar no if
            if (atomsAux.isEmpty()) {
                // puxando uma lista de átomos à direita do "Or" e retornando
                // (se for vazio então toda a formula é insatisfativel)
                solveTableaux(formula.right, newAtoms)
            }else {
                atomsAux
            }
        }
        is Implies -> {
            val atomsAux = solveTableaux(formula.right, newAtoms)
            if (atomsAux.isEmpty()) {
                // implies é semelhante ao Or. Porém a fórmula à esquerda entra negada
                solveTableaux(Not(formula.left), newAtoms)
            }else {
                atomsAux
            }
        }
        else -> mutableSetOf<Formula>()
    }

}

fun complementaInterpretacao(atoms: Set<Formula>): MutableMap<String, Boolean>?{
    val newInterpretation = mutableMapOf<String, Boolean>()
    for (i in atoms){
        if (i is Atom){
            newInterpretation[i.toString()] = true
        } else if (i is Not){
            newInterpretation[i.proposicao.toString()] = false
        }
    }
    return newInterpretation
}
