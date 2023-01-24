import org.antlr.v4.runtime.tree.ParseTree;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.BatchUpdateException;
import java.util.*;

public class BigCalcVisitorImpl extends BigCalcBaseVisitor<BigDecimal> {

    private Map<String, BigDecimal> variables = new HashMap<>();

    @Override
    public BigDecimal visitProg(BigCalcParser.ProgContext ctx){
        BigDecimal result = null;
        for (BigCalcParser.ExpressionStatementContext expressionStatement : ctx.expressionStatement()) {
            result = visit(expressionStatement);
        }
        return result;
    }
    @Override
    public BigDecimal visitCondi(BigCalcParser.CondiContext ctx){
        BigDecimal condition = visit(ctx.expression(0)); // evaluate the condition

        if(condition.doubleValue() != 0){ // check if the condition is true
            return visit(ctx.expression(1)); // evaluate and return the first expression
        }else {
            return visit(ctx.expression(2)); // evaluate and return the second expression
        }
    }

    @Override
    public BigDecimal visitExpressionStatement(BigCalcParser.ExpressionStatementContext ctx) {
        //when there are assignemnts
        //we need to convert all of them
        //assignment -> expression -> number
        //call the appropriate methode  number [*|/][+|-] number
        // When there are assignments, convert them to expressions and evaluate them
        BigDecimal result = null;
        for (BigCalcParser.AssignmentContext assignmentCtx : ctx.assignment()) {
            result = visit(assignmentCtx);
        }
        for (BigCalcParser.ExpressionContext expressionCtx : ctx.expression()) {
            result = visit(expressionCtx);
        }
        return result;
    }
    @Override
    public BigDecimal visitAssignment(BigCalcParser.AssignmentContext ctx){
        final String variableName = ctx.ID().getText();
        final BigDecimal value = visit(ctx.expression());
        variables.put(variableName, value);
        return value;
    }

    @Override
    public BigDecimal visitParens(BigCalcParser.ParensContext ctx){
        return visit(ctx.expression());
    }

    @Override
    public BigDecimal visitVar(BigCalcParser.VarContext ctx) {
        if (variables == null) {
            throw new NullPointerException("variables is null");
        }
        String variableName = ctx.id().getText();
        BigDecimal value = variables.get(variableName);
        if (value == null) {
            System.out.println("Warning: undefined variable: " + variableName); //new
            return BigDecimal.ZERO;
        }
        return value;
    }

    @Override
    public BigDecimal visitMulDiv(BigCalcParser.MulDivContext ctx) {
        final BigDecimal left = visit(ctx.expression(0));
        final BigDecimal right = visit(ctx.expression(1));
        if (ctx.op.getText().equals("*")) {
            return left.multiply(right);
        } else {
            return left.divide(right, 10, RoundingMode.HALF_UP);
        }
    }

    @Override
    public BigDecimal visitAddSub(BigCalcParser.AddSubContext ctx) {
        final BigDecimal left = visit(ctx.expression(0));
        final BigDecimal right = visit(ctx.expression(1));
        if (ctx.op.getText().equals("+")) {
            return left.add(right);
        } else {
            return left.subtract(right);
        }
    }

    @Override
    public BigDecimal visitNum(BigCalcParser.NumContext ctx) {
        return new BigDecimal(ctx.Number().getText());
    }
}
