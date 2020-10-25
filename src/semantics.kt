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
    var listFormula = polimentoFormulas(formula)
    val literaisValidos = getLiterais(listFormula)
    listFormula = removeLiterais(listFormula, literaisValidos)
    if (containsContradicao(literaisValidos))
        return null
    val atoms = solveTableaux(listFormula, literaisValidos)
    return interpretacaoTableaux(atoms)
}

private fun solveTableaux(listFormula: List<Formula>, literaisValidos: Set<Formula>): Set<Formula>{
    //nova variável para evitar que as mudanças repercutam em outras chamadas
    val mutableLiteraisValidos: MutableSet<Formula> = mutableSetOf()
    val mutableListFormula = mutableListOf<Formula>()

    if (!literaisValidos.isNullOrEmpty())
        mutableLiteraisValidos.addAll(literaisValidos)
    mutableListFormula.addAll(listFormula)

    if (mutableListFormula.isEmpty())
        return mutableLiteraisValidos

    while (mutableListFormula.isNotEmpty()){
        when(val formula = mutableListFormula[0]) {
            is Atom -> {
                mutableListFormula.remove(formula)
                mutableLiteraisValidos.add(formula)
            }
            is Not -> {
                mutableListFormula.remove(formula)
                if (formula.proposicao is Atom) {
                    mutableLiteraisValidos.add(formula)
                }else {
                    mutableListFormula.add(deMorgan(formula))
                }
            }
            is And -> {
                mutableListFormula.remove(formula)
                mutableListFormula.add((formula.left))
                mutableListFormula.add((formula.right))
            }
            is Or -> {
                mutableListFormula.remove(formula)
                mutableListFormula.add(0,formula.right)
                var result = solveTableaux(mutableListFormula,mutableLiteraisValidos)
                if (result.isEmpty()){
                    mutableListFormula.remove(formula.right)
                    mutableListFormula.add(0,formula.left)
                    result = solveTableaux(mutableListFormula,mutableLiteraisValidos)
                }
                mutableLiteraisValidos.addAll(result)
                if (result.isEmpty() || containsContradicao(mutableLiteraisValidos))
                    return emptySet()
                return mutableLiteraisValidos
            }
            else -> error("Não implementado")
        }
        if (containsContradicao(mutableLiteraisValidos))
            return emptySet()
    }
    return mutableLiteraisValidos
}

// separa em listas as formulas com And
// "empurra" as negações pra dentro
fun polimentoFormulas(formula: Formula):List<Formula>{
    val mutableListFormula = mutableListOf<Formula>()

    //println(listFormula)

        when(formula){
            is Not -> {
                if (formula.proposicao is Atom)
                    mutableListFormula.add(formula)
                else
                    mutableListFormula.addAll(polimentoFormulas(deMorgan(formula)))
            }
            is And -> {
                mutableListFormula.addAll(polimentoFormulas((formula.left)))
                mutableListFormula.addAll(polimentoFormulas((formula.right)))
            }
            else -> mutableListFormula.add(formula)
        }

    //println(mutableListFormula)

    return mutableListFormula
}

// retorna todos os literais da lista
fun getLiterais(listFormula: List<Formula>): Set<Formula>{
    val literais = mutableSetOf<Formula>()
    listFormula.forEach {
        when(it){
            is Atom -> literais.add(it)
            is Not -> literais.add(it)
        }
    }
    return literais
}

fun containsContradicao(literais: Set<Formula>): Boolean{

    literais.forEach{
        when(it){
            is Atom -> if (literais.contains(Not(it))) return true
            is Not -> if (literais.contains((it.proposicao))) return true
        }
    }
    return false
}

// remove todos os literais da lista
fun removeLiterais(listFormula: List<Formula>, literais: Set<Formula>): List<Formula>{
    val mutableFormula = listFormula as MutableList
    literais.forEach{
        mutableFormula.remove(it)
    }
    return mutableFormula
}

// cria uma interpretação para os literais do tableaux
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

//após a propagação de unidade realiza uma valoração de um literal e chama
//a função recursivamente
//caso seja insatisfatível tenta novamente com o literal negado
private fun satDPLL(cnf: List<List<Int>>, valoracao: List<Int>): List<Int> {

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

//checa se a cnf é satisfatível
private fun isSatCNF(cnf: List<List<Int>>): Boolean{
    return cnf.isEmpty()
}

// checa se a cnf é não satisfatível
private fun isNotSatCNF(cnf: List<List<Int>>): Boolean{
    return cnf.contains(listOf())
}

// propaga todas as clausulas unitárias
private fun unitPropagation(cnf: List<List<Int>>, valoracao: List<Int>): Pair<MutableList<MutableList<Int>>,MutableList<Int>>{
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

// faz a cópia de uma lista de listas de inteiros
private fun copiaLista(list: List<List<Int>>): MutableList<MutableList<Int>>{
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

// pega a posição de uma clausula unitária
private fun getUnitClause(cnf: List<List<Int>>): Int{
    for (i in cnf) {
        if (i.size == 1)
            return cnf.indexOf(i)
    }
    return -1
}

// remove as clausulas com o literal
private fun removeAtLiteral(cnf: List<List<Int>>, literal: Int): List<List<Int>>{
    val mutableCNF = mutableListOf<List<Int>>()
    for (i in cnf){
        if (literal !in i)
            mutableCNF.add(i)
    }
    return mutableCNF
}

// remove da lista os literais que estão negados
private fun removeAtNotOfLiteral(cnf: List<List<Int>>, literal: Int): List<List<Int>>{
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
