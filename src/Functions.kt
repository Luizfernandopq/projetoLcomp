fun length(formula: Formula): Int {
    return when (formula) {
        is Atom -> 1
        is Not -> length(formula.proposicao) + 1
        is And -> length(formula.left) + length(formula.right) + 1
        is Or -> length(formula.left) + length(formula.right) + 1
        is Implies -> length(formula.left) + length(formula.right) + 1
        is OnlyIf -> length(formula.left) + length(formula.right) + 1
        is Xor -> length(formula.left) + length(formula.right) + 1
        else -> 0
    }
}

fun subformulas(formula: Formula): List<Formula> {
    val formulas = mutableSetOf<Formula>()
    when (formula) {
        is Atom -> formulas.add(formula)
        is Not -> {
            formulas.add(formula)
            formulas.addAll(subformulas(formula.proposicao))
        }
        is And -> {
            formulas.add(formula)
            formulas.addAll(subformulas(formula.left))
            formulas.addAll(subformulas(formula.right))
        }
        is Or -> {
            formulas.add(formula)
            formulas.addAll(subformulas(formula.left))
            formulas.addAll(subformulas(formula.right))
        }
        is Implies -> {
            formulas.add(formula)
            formulas.addAll(subformulas(formula.left))
            formulas.addAll(subformulas(formula.right))
        }
        is OnlyIf -> {
            formulas.add(formula)
            formulas.addAll(subformulas(formula.left))
            formulas.addAll(subformulas(formula.right))
        }
        is Xor -> {
            formulas.add(formula)
            formulas.addAll(subformulas(formula.left))
            formulas.addAll(subformulas(formula.right))
        }
    }
    return formulas.sortedWith(compareBy{
        it.toString()
    })
}

fun atoms(formula: Formula): MutableSet<Formula> {
    val formulas = mutableSetOf<Formula>()
    when (formula) {
        is Atom -> formulas.add(formula)
        is Not -> formulas.addAll(atoms(formula.proposicao))
        is And -> {
            formulas.addAll(atoms(formula.left))
            formulas.addAll(atoms(formula.right))
        }
        is Or -> {
            formulas.addAll(atoms(formula.left))
            formulas.addAll(atoms(formula.right))
        }
        is Implies -> {
            formulas.addAll(atoms(formula.left))
            formulas.addAll(atoms(formula.right))
        }
        is OnlyIf -> {
            formulas.addAll(atoms(formula.left))
            formulas.addAll(atoms(formula.right))
        }
        is Xor -> {
            formulas.addAll(atoms(formula.left))
            formulas.addAll(atoms(formula.right))
        }
    }
    return formulas
}

