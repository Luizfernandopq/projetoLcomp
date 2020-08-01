fun main(){
    val p: Atom = Atom("p")
    val q: Atom = Atom("q")
    val r: Atom = Atom("r")
    val p1 = Not(Atom("p"))
    val imp: Formula = And(Implies(q, Not(And(p, Not(q)))),And(r,Not(r)))
    val imp2: Formula = And(p, And(r, Not (r)))
    println("$imp é satisfatível? ")
    //println(atoms(imp))
    println(is_satisfiable(imp))

//    val allatoms = atoms(imp)
//    val (atom) = allatoms.take(1)
//    allatoms.remove(atom)
//    println(allatoms)
//    println(atom)
//    println(truth_value(p, mapOf("p" to true)))


}