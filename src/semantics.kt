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

    return interpretacaoTableaux(atoms)
}

private fun solveTableaux(formula: Formula, atomsValidos: Set<Formula>?): MutableSet<Formula>{
    //nova variável para evitar que as mudanças repercutam em outras chamadas
    val newAtomsValidos: MutableSet<Formula> = mutableSetOf()
    if (!atomsValidos.isNullOrEmpty())
        newAtomsValidos.addAll(atomsValidos as MutableSet<Formula>)
    //println(formula)
    //println(newAtomsValidos)

    return when(formula){
        is Atom ->{
            //se já existe negação desse átomo em lista
            if (Not(formula) in newAtomsValidos) {
                //retorna uma lista vazia pois é insatistatível
                mutableSetOf<Formula>()
            } else {
                //adiciona átomo à lista
                newAtomsValidos.add(formula)
                newAtomsValidos
            }
        }
        is Not -> {
            //se o que está dentro da negação é átomo
            if (formula.proposicao is Atom){
                //se já existe negação desse átomo em lista
                if (formula.proposicao in newAtomsValidos){
                    //retorna uma lista vazia pois é insatistatível
                    mutableSetOf<Formula>()
                } else {
                    //adiciona negação de átomo à lista
                    newAtomsValidos.add(formula)
                    newAtomsValidos
                }
            } else {
                //aplicação de deMorgan
                val newformula = deMorgan(formula)
                //resolvendo a nova fórmula
                solveTableaux(newformula, newAtomsValidos)
            }
        }
        is And -> {
            // puxando uma lista de átomos à esquerda do "And"
            var atomsAux = solveTableaux(formula.left, newAtomsValidos)
            // se atomsAux for vazio a fórmula é insatisfatível e não entra no if
            if (atomsAux.isNotEmpty()){
                //resolve a parte da direita já com os átomos da esquerda
                atomsAux = solveTableaux(formula.right, atomsAux) as MutableSet<Formula>
                //se for insatisfatível podemos começar pelo outro lado
                if (atomsAux.isEmpty()){
                    // puxando uma lista de átomos à direita do "And"
                    atomsAux = solveTableaux(formula.right,newAtomsValidos)
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
            val atomsAux = solveTableaux(formula.left, newAtomsValidos)
            // se atomsAux for vazio a fórmula à esquerda é insatisfatível e tem que entrar no if
            if (atomsAux.isEmpty()) {
                // puxando uma lista de átomos à direita do "Or" e retornando
                // (se for vazio então toda a formula é insatisfativel)
                solveTableaux(formula.right, newAtomsValidos)
            }else {
                atomsAux
            }
        }
        is Implies -> {
            val atomsAux = solveTableaux(formula.right, newAtomsValidos)
            if (atomsAux.isEmpty()) {
                // implies é semelhante ao Or. Porém a fórmula à esquerda entra negada
                solveTableaux(Not(formula.left), newAtomsValidos)
            }else {
                atomsAux
            }
        }
        else -> mutableSetOf<Formula>()
    }
}

private fun interpretacaoTableaux(atoms: Set<Formula>): MutableMap<String, Boolean>?{
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
fun satDPLL(cnf: List<List<Int>>): List<Int> {
    return satDPLL(cnf, listOf())
}
fun satDPLL(cnf: List<List<Int>>, valoracao: List<Int>): List<Int> {

//    println("Antes da unitPropagation")
//    println(cnf)
//    println(valoracao)

    var (mutableCNF,mutableValoracao) =  unitPropagation(cnf, valoracao)
    if (isSatCNF(mutableCNF)){
        return mutableValoracao
    }else if (isNotSatCNF(mutableCNF))
        return listOf()

//    println("Depois da unitPropagation")
//    println(mutableCNF)
//    println(mutableValoracao)

    val mutableClause = mutableCNF[0]
    val literal = mutableClause[0]
    mutableValoracao.add(literal)

    //println(mutableCNF)

    var auxMutableCNF = removeAtLiteral(mutableCNF, literal) as MutableList<MutableList<Int>>
    auxMutableCNF = removeAtNotOfLiteral(auxMutableCNF, literal) as MutableList<MutableList<Int>>
    val valoracaoAux = satDPLL(auxMutableCNF,mutableValoracao)

    //println(auxMutableCNF)
    return if (valoracaoAux.isNotEmpty())
         valoracaoAux
    else {
        mutableValoracao.remove(literal)
        mutableValoracao.add(-literal)
        mutableCNF = removeAtLiteral(mutableCNF, -literal) as MutableList<MutableList<Int>>
        mutableCNF = removeAtNotOfLiteral(mutableCNF, -literal) as MutableList<MutableList<Int>>
        satDPLL(mutableCNF,mutableValoracao)
    }
}

fun isSatCNF(cnf: List<List<Int>>): Boolean{
    return cnf.isEmpty()
}

fun isNotSatCNF(cnf: List<List<Int>>): Boolean{
    return cnf.contains(listOf())
}

fun unitPropagation(cnf: List<List<Int>>, valoracao: List<Int>): Pair<MutableList<MutableList<Int>>,MutableList<Int>>{
    var mutableCNF = copiaLista(cnf)

    val mutableValoracao: MutableList<Int> =
            if (valoracao.isNotEmpty())
                valoracao as MutableList
            else
                mutableListOf<Int>()


    var posicao: Int = getUnitClause(mutableCNF)
    while (posicao != -1){
        val literal = mutableCNF[posicao][0]
        mutableValoracao.add(literal)
        mutableCNF = removeAtLiteral(mutableCNF, literal) as MutableList<MutableList<Int>>
        mutableCNF = removeAtNotOfLiteral(mutableCNF, literal) as MutableList<MutableList<Int>>
        posicao = getUnitClause(mutableCNF)
    }
    return Pair(mutableCNF, mutableValoracao)
}

fun copiaLista(list: List<List<Int>>): MutableList<MutableList<Int>>{
    val copy = mutableListOf<MutableList<Int>>()
    for (subLista in list){
        val i = mutableListOf<Int>()
        for (item in subLista){
            i.add(item)
        }
        copy.add(i)
    }
    return copy
}

fun getUnitClause(cnf: List<List<Int>>): Int{
    for (i in cnf) {
        if (i.size == 1)
            return cnf.indexOf(i)
    }
    return -1
}

fun removeAtLiteral(cnf: List<List<Int>>, literal: Int): List<List<Int>>{
    val mutableCNF = mutableListOf<List<Int>>()
    for (i in cnf){
        if (literal !in i)
            mutableCNF.add(i)
    }
    return mutableCNF
}

fun removeAtNotOfLiteral(cnf: List<List<Int>>, literal: Int): List<List<Int>>{
    val mutableCNF = copiaLista(cnf)

    //println("Antes: $mutableCNF")
    //println("Literal $literal")

    for (i in mutableCNF){
        if ((-literal) in i)
            i.remove(-literal)
    }

    //println("Depois: $mutableCNF\n")

    return mutableCNF
}
