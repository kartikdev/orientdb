/* Generated By:JJTree: Do not edit this line. OBinaryCondition.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.db.record.OIdentifiable;

import java.util.Map;

public class OBinaryCondition extends OBooleanExpression {
  protected OExpression            left;
  protected OBinaryCompareOperator operator;
  protected OExpression            right;

  public OBinaryCondition(int id) {
    super(id);
  }

  public OBinaryCondition(OrientSql p, int id) {
    super(p, id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  @Override
  public boolean evaluate(OIdentifiable currentRecord) {
    return false;
  }

  @Override public void replaceParameters(Map<Object, Object> params) {
    left.replaceParameters(params);
    right.replaceParameters(params);
  }

  @Override
  public String toString() {
    return left.toString() + " " + operator.toString() + " " + right.toString();
  }
}
/* JavaCC - OriginalChecksum=99ed1dd2812eb730de8e1931b1764da5 (do not edit this line) */