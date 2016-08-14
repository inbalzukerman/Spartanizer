package il.org.spartan.refactoring.wring;

import static il.org.spartan.Utils.*;
import static il.org.spartan.refactoring.utils.Funcs.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.refactoring.preferences.*;
import il.org.spartan.refactoring.utils.*;

/**
 * A {@link Wring} to convert <code>if (a) { return x; } else { return y;
 * }</code> into <code>if (a) return x; return y;</code> provided that this
 * <code><b>if</b></code> statement is the last statement in a method.
 *
 * @author Yossi Gil
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 * @since 2015-09-09
 */
public class IfLastInMethodElseEndingWithEmptyReturn extends Wring<IfStatement> implements Kind.Simplify {
  @Override String description(final IfStatement s) {
    return "Remove redundant return statement in 'else' branch of if(" + s.getExpression()
        + ") ... statement that terminates this method";
  }
  @Override Rewrite make(final IfStatement s) {
    final Block b = asBlock(s.getParent());
    if (b == null || !(b.getParent() instanceof MethodDeclaration) || !lastIn(s, b.statements()))
      return null;
    final ReturnStatement deleteMe = asReturnStatement(extract.lastStatement(elze(s)));
    return deleteMe == null || deleteMe.getExpression() != null ? null : new Rewrite(description(s), s) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        r.replace(deleteMe, s.getAST().newEmptyStatement(), g);
      }
    };
  }
}