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

fun tableaux(formula: Formula): MutableMap<String, Boolean>? {
    return when (formula){
        // quando a fórmula é um átomo, ela já é satisfatível
        is Atom -> mutableMapOf<String, Boolean>(formula.proposicao to true)
        // transportando a fórmula para dentro de uma lista e chamando o solveTableaux
        else -> {
            val interpretation = solveTableaux(formula, null)
            if (isNullInterpretation(interpretation))
                null
            else
                complementaInterpretacao(formula, interpretation!!)
        }
    }
}

private fun solveTableaux(formula: Formula, interpretation: Map<String, Boolean>?): MutableMap<String, Boolean>?{
    // criação de novos objetos(cópia) para evitar manipular os objetos que já estão na memória
    val newInterpretation: MutableMap<String,Boolean> = mutableMapOf<String,Boolean>()
    interpretation?.let { newInterpretation.putAll(it) }
    var newformula = formula
    println(newformula)
    println(newInterpretation)

    when (newformula){
        is Atom -> {
            //caso já exista uma interpretação anterior onde este átomo é falso
            return if (newInterpretation[newformula.proposicao] == false){
                println("Solve Atom 1")
                //o ramo é insatisfatível, logo esta interpretação é inválida
                null
            }else {
                //se não, o átomo pode ser adicionado à interpretação como verdadeiro
                newInterpretation[newformula.proposicao] = true
                println("Solve Atom 2 $newInterpretation")
                newInterpretation
            }
        }
        is Not -> {
            return if (newformula.proposicao is Atom) {
                // variável criada com a necessidade de um "cast"
                val atom: Atom = newformula.proposicao as Atom
                //caso já exista uma interpretação anterior onde este átomo é verdadeiro
                if (newInterpretation[atom.proposicao] == true) {
                    //o ramo é insatisfatível, logo esta interpretação é inválida
                    println("Solve Not 1")
                    println(newInterpretation)
                    null
                } else {
                    println("Solve Not 2")
                    newInterpretation[atom.proposicao] = false
                    newInterpretation
                }
            } else{
                // caso o que esteja dentro da negação não seja átomo, deve-se aplicar equivalência
                // de deMorgan
                println("Solve Not 3")
                newformula = deMorgan(newformula)
                solveTableaux(newformula,newInterpretation)
            }
        }
        is And -> {
            var interpretationAux = solveTableaux(newformula.left, solveTableaux(newformula.right,newInterpretation))

            return if (isNullInterpretation(interpretationAux)){
                println("Solve And 1")
                interpretationAux = solveTableaux(newformula.right, solveTableaux(newformula.left, newInterpretation))
                if (isNullInterpretation(interpretationAux))
                    null
                else
                    solveTableaux(newformula.left, interpretationAux)
            } else {
                println("Solve And 2")
                interpretationAux = solveTableaux(newformula.right, interpretationAux)
                interpretationAux
            }
        }
        is Or -> {
            val interpretationAux = solveTableaux(newformula.left, newInterpretation)

            return if (isNullInterpretation(interpretationAux)) {
                println("Solve Or 1")
                solveTableaux(newformula.right, newInterpretation)
            } else{
                println("Solve Or 2")
                interpretationAux
            }
        }
        is Implies -> {
            val interpretationAux = solveTableaux(Not(newformula.left), newInterpretation)

            return if (isNullInterpretation(interpretationAux)) {
                println("Solve Implies 1")
                solveTableaux(newformula.right, newInterpretation)
            } else{
                println("Solve Implies 2")
                interpretationAux
            }
        }
        is OnlyIf -> {
            return solveTableaux(Or(And(newformula.left,newformula.right),
                            And(Not(newformula.left),Not(newformula.right))),newInterpretation)
        }
        is Xor -> {
            return solveTableaux(Or(And(Not(newformula.left),newformula.right),
                    And(newformula.left,Not(newformula.right))),newInterpretation)
        }
        else -> {
            println("erro1")
            return null
        }
    }
}

fun complementaInterpretacao(formula: Formula, interpretation: Map<String, Boolean>): MutableMap<String, Boolean>?{
    val atoms = atoms(formula)
    val newInterpretation = interpretation.toMutableMap()
    for (i in atoms){
        if (interpretation[i.toString()] != false){
            newInterpretation[i.toString()] = true
        }
    }
    return newInterpretation
}
