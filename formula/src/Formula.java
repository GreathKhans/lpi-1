import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Constant {
    String name;
    public Constant(String name) {
        this.name = name;
    }
    public String name() {
        return this.name;
    }
    public String eval(Structure m) {
        return m.iC(name());
    }
    @Override
    public String toString() {
        return name();
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        Constant otherC = (Constant) other;
        return name().equals(otherC.name());
    }
}

class Formula {
    public List<Formula> subfs() {
        throw new RuntimeException("Not implemented 1");
    }

    @Override
    public String toString() {
        throw new RuntimeException("Not implemented 2");
    }

    public boolean isTrue(Structure m) {
        throw new RuntimeException("Not implemented 3");
    }

    @Override
    public boolean equals(Object other) {
        throw new RuntimeException("Not implemented 4");
    }

    public int deg() {
        throw new RuntimeException("Not implemented 5");
    }

    public Set<AtomicFormula> atoms() {
        throw new RuntimeException("Not implemented 6");
    }

    public Set<String> constants() {
        throw new RuntimeException("Not implemented 7");
    }

    public Set<String> predicates() {
        throw new RuntimeException("Not implemented 8");
    }
}

class AtomicFormula extends Formula {
    AtomicFormula(){}
    public List<Formula> subfs() {
        throw new RuntimeException("Not implemented 1");
    }
    public Set<AtomicFormula> atoms() {
        Set<AtomicFormula> retv = new HashSet<>();
        retv.add(this);
        return retv;
    }
    public Set<String> constants() {
        throw new RuntimeException("Not implemented 7.1");
    }
    public Set<String> predicates() { throw new RuntimeException("Not implemented 8.1"); }
    public int deg() {return 0;}
}

class PredicateAtom extends AtomicFormula {
    private String name;
    private List<Constant> args;
    PredicateAtom(String name, List<Constant> args) {
        this.name = name;
        this.args = args;
    }

    public boolean equals(Object other) {
        if(other.getClass() == this.getClass())
        {
            PredicateAtom o = (PredicateAtom)other;
            if((o.name.equals(this.name)) && (o.args.equals(this.args)))
                return true;
        }
        return false;
    }

    public Set<String> constants()
    {
        Set<String> retv = new HashSet<>();
        for(Constant c: args)
            retv.add(c.name());
        return retv;
    }

    public Set<String> predicates()
    {
        Set<String> retv = new HashSet<>();
        retv.add(name);
        return retv;
    }

    public List<Formula> subfs() {
        return new ArrayList<Formula>();
    }

    String name() {
        return name;
    }

    List<Constant> arguments() {
        return args;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        boolean first = true;
        for(Constant c: args)
        {
            if(!first)
                sb.append(",");
            else
                first = false;
            sb.append(c.name());
        }
        sb.append(")");
        return sb.toString();
    }

    public boolean isTrue(Structure m)
    {
        //vytvorime si nas zoznam hodnot, ktory reprezentujeme
        List<String> myVals = new ArrayList<String>();
        for(Constant cn: args)
        {
            myVals.add(m.iC(cn.name()));
        }
        //vypytame si vsetky zoznamy konstant pre ktore je nas predikat pravdivy
        Set<List<String>> vals = m.iP(name);
        //skusime ci niektory z tych zoznamov je zhodny s nasim zoznamom argumentov
        for(List<String> vl: vals)
        {
            if(vl.equals(myVals))
                return true;
        }
        return false;
    }
}

class EqualityAtom extends AtomicFormula {
    private Constant left;
    private Constant right;
    public EqualityAtom(Constant left, Constant right) {
        this.left = left;
        this.right = right;
    }

    public Set<String> constants()
    {
        Set<String> retv = new HashSet<>();
        retv.add(left.name());
        retv.add(right.name());
        return retv;
    }

    public Set<String> predicates()
    {
        return new HashSet<String>();
    }

    public boolean equals(Object other) {
        if(other.getClass() == this.getClass())
        {
            EqualityAtom o = (EqualityAtom)other;
            if((o.left.equals(this.left)) && (o.right.equals(this.right)))
                return true;
        }
        return false;
    }

    public List<Formula> subfs() {
        return new ArrayList<Formula>();
    }

    Constant left() {
        return left;
    }

    Constant right()
    {
        return right;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(left.name());
        sb.append("=");
        sb.append(right.name());
        return sb.toString();
    }

    public boolean isTrue(Structure m) {
        String lv = m.iC(left.name());
        String rv = m.iC(right.name());
        if(lv.equals(rv))
            return true;
        return false;
    }

}

class Negation extends Formula {
    private Formula original;
    Negation(Formula originalFormula) {
        this.original = originalFormula;
    }

    public int deg() {
        return 1 + original.deg();
    }

    public Set<String> predicates()
    {
        return original.predicates();
    }

    public Set<String> constants()
    {
        return original.constants();
    }

    public Set<AtomicFormula> atoms()
    {
        return original.atoms();
    }

    public boolean equals(Object other) {
        if(other.getClass() == this.getClass())
        {
            Negation o = (Negation)other;
            if(o.original.equals(this.original))
                return true;
        }
        return false;
    }

    public List<Formula> subfs() {
        List<Formula> retv = new ArrayList<>();
        retv.add(original);
        return retv;
    }

    public Formula originalFormula() {
        return original;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("-");
        sb.append(original.toString());
        return sb.toString();
    }

    public boolean isTrue(Structure m)
    {
        if(original.isTrue(m))
            return false;
        else
            return true;
    }
}

class Disjunction extends Formula {
    private List<Formula> disj;
    Disjunction(List<Formula> disjuncts) {
        this.disj = disjuncts;
    }

    public int deg() {
        int d = 0;
        for(Formula f: disj)
            d += f.deg();
        return 1 + d;
    }

    public Set<String> constants()
    {
        Set<String> retv = new HashSet<>();
        for(Formula f: disj)
            retv.addAll(f.constants());
        return retv;
    }

    public Set<String> predicates()
    {
        Set<String> retv = new HashSet<>();
        for(Formula f: disj)
            retv.addAll(f.predicates());
        return retv;
    }

    public Set<AtomicFormula> atoms()
    {
        Set<AtomicFormula> retv = new HashSet<>();
        for(Formula f: disj)
            retv.addAll(f.atoms());
        return retv;
    }

    public List<Formula> subfs() {
        return disj;
    }

    public boolean equals(Object other) {
        if(other.getClass() == this.getClass())
        {
            Disjunction o = (Disjunction)other;
            if(o.disj.equals(this.disj))
                return true;
        }
        return false;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        boolean first = true;
        for(Formula f: disj)
        {
            if(!first)
                sb.append("|");
            else
                first = false;
            sb.append(f.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    public boolean isTrue(Structure m)
    {
        for(Formula f:disj)
        {
            if(f.isTrue(m))
                return true;
        }
        return false;
    }
}

class Conjunction extends Formula {
    private List<Formula> conj;
    Conjunction(List<Formula> conjuncts) {
        this.conj = conjuncts;
    }

    public int deg() {
        int d = 0;
        for(Formula f: conj)
            d += f.deg();
        return 1 + d;
    }

    public Set<String> constants()
    {
        Set<String> retv = new HashSet<>();
        for(Formula f: conj)
            retv.addAll(f.constants());
        return retv;
    }

    public Set<String> predicates()
    {
        Set<String> retv = new HashSet<>();
        for(Formula f: conj)
            retv.addAll(f.predicates());
        return retv;
    }

    public Set<AtomicFormula> atoms()
    {
        Set<AtomicFormula> retv = new HashSet<>();
        for(Formula f: conj)
            retv.addAll(f.atoms());
        return retv;
    }

    public boolean equals(Object other) {
        if(other.getClass() == this.getClass())
        {
            Conjunction o = (Conjunction)other;
            if(o.conj.equals(this.conj))
                return true;
        }
        return false;
    }

    public List<Formula> subfs() {
        return conj;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        boolean first = true;
        for(Formula f: conj)
        {
            if(!first)
                sb.append("&");
            else
                first = false;
            sb.append(f.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    public boolean isTrue(Structure m)
    {
        for(Formula f:conj)
        {
            if(!f.isTrue(m))
                return false;
        }
        return true;
    }
}

class BinaryFormula extends Formula {
    protected Formula left;
    protected Formula right;
    BinaryFormula(Formula left, Formula right) {
        this.left = left;
        this.right = right;
    }

    public int deg() {
        return 1 + left.deg() + right.deg();
    }

    public Set<AtomicFormula> atoms()
    {
        Set<AtomicFormula> retv = new HashSet<>();
        retv.addAll(left.atoms());
        retv.addAll(right.atoms());
        return retv;
    }

    public Set<String> constants()
    {
        Set<String> retv = new HashSet<>();
        retv.addAll(left.constants());
        retv.addAll(right.constants());
        return retv;
    }

    public Set<String> predicates()
    {
        Set<String> retv = new HashSet<>();
        retv.addAll(left.predicates());
        retv.addAll(right.predicates());
        return retv;
    }

    public boolean equals(Object other) {
        return false;
    }

    public List<Formula> subfs() {
        List<Formula> retv = new ArrayList<>();
        retv.add(left);
        retv.add(right);
        return retv;
    }

    public Formula leftSide() {
        return left;
    }

    public Formula rightSide() {
        return right;
    }
}

class Implication extends BinaryFormula {
    Implication(Formula left, Formula right) {
        super(left, right);
    }

    public boolean equals(Object other) {
        if(other.getClass() == this.getClass())
        {
            Implication o = (Implication)other;
            if(o.left.equals(this.left) && o.right.equals(this.right))
                return true;
        }
        return false;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(left.toString()).append("->").append(right.toString()).append(")");
        return sb.toString();
    }

    public boolean isTrue(Structure m)
    {
        //implikacia: ak plati lava strana, musi platit prava strana
        boolean lv = left.isTrue(m);
        if(!lv)
            return true;
        else
            return right.isTrue(m);
    }
}

class Equivalence extends BinaryFormula {
    Equivalence(Formula left, Formula right) {
        super(left, right);
    }

    public boolean equals(Object other) {
        if(other.getClass() == this.getClass())
        {
            Equivalence o = (Equivalence)other;
            if(o.left.equals(this.left) && o.right.equals(this.right))
                return true;
        }
        return false;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(left.toString()).append("<->").append(right.toString()).append(")");
        return sb.toString();
    }

    public boolean isTrue(Structure m)
    {
        //ekvivalencia: prava strana plati prave vtedy, ak plati lava strana
        boolean lv = left.isTrue(m);
        boolean rv = right.isTrue(m);
        return (lv == rv);

    }
}
