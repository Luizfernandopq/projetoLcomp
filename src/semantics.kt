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

fun is_satisfiable(formula: Formula): Boolean {
    val allatoms: MutableSet<Formula> = atoms(formula)
    return satisfiability_check(formula, allatoms, mutableMapOf())
}


fun satisfiability_check(formula: Formula, allAtoms: MutableSet<Formula>, interpretation: MutableMap<String, Boolean>): Boolean {
    //println(interpretation)
    //println(allAtoms)
    val natoms = mutableSetOf<Formula>()
    natoms.addAll(allAtoms)
    if (allAtoms.isEmpty()) return truth_value(formula, interpretation)
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
        return satisfiability_check(formula, natoms, interpretation1) || satisfiability_check(formula, natoms, interpretation2)
    }
}
