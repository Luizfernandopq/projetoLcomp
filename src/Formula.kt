open class Formula()

data class Atom(var proposicao: String) : Formula(){
    override fun toString(): String {
        return "$proposicao"
    }
}

data class Not(var proposicao: Formula) : Formula(){
    override fun toString(): String {
        return "\u00ac$proposicao"
    }
}

data class Implies(var left: Formula, var right: Formula) : Formula(){
    override fun toString(): String {
        return "($left \u2192 $right)"
    }
}

data class And(var left: Formula, var right: Formula) : Formula(){
    override fun toString(): String {
        return "($left \u2227 $right)"
    }
}

data class Or(var left: Formula, var right: Formula) : Formula(){
    override fun toString(): String {
        return "($left \u2228 $right)"
    }
}


data class OnlyIf(var left: Formula, var right: Formula) : Formula(){
    override fun toString(): String {
        return "($left \u2194 $right)"
    }
}


data class Xor(var left: Formula, var right: Formula) : Formula(){
    override fun toString(): String {
        return "($left \u2295 $right)"
    }
}