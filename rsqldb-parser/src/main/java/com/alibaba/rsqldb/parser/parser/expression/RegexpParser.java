/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.rsqldb.parser.parser.expression;

import java.util.List;

import com.alibaba.rsqldb.parser.parser.builder.SelectSQLBuilder;
import com.alibaba.rsqldb.parser.parser.namecreator.ParserNameCreator;
import com.alibaba.rsqldb.parser.parser.result.IParseResult;
import com.alibaba.rsqldb.parser.parser.result.ScriptParseResult;
import com.alibaba.rsqldb.parser.parser.sqlnode.AbstractSelectNodeParser;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlNode;

public class RegexpParser extends AbstractSelectNodeParser<SqlBasicCall> {

    @Override
    public IParseResult parse(SelectSQLBuilder tableDescriptor, SqlBasicCall sqlBasicCall) {
        List<SqlNode> nodeList = sqlBasicCall.getOperandList();
        String functionName = "regex";
        if (sqlBasicCall.getOperator().getName().toUpperCase().equals("NOT REGEXP")) {
            functionName = "!regex";
        }

        IParseResult varSqlVar = parseSqlNode(tableDescriptor, nodeList.get(0));

        IParseResult valueSqlVar = parseSqlNode(tableDescriptor, nodeList.get(1));
        String varName = varSqlVar.getReturnValue();
        if (tableDescriptor.isSelectStage()) {
            String returnName = ParserNameCreator.createName(functionName, null);
            // String returnName = NameCreator.createNewName("__", "regexp");
            String regex = valueSqlVar.getValueForSubExpression();
            String scriptValue = returnName + "=" + functionName + "(" + varName + "," + regex + ");";
            ScriptParseResult scriptParseResult = new ScriptParseResult();
            scriptParseResult.setReturnValue(returnName);
            scriptParseResult.addScript(tableDescriptor, scriptValue);
            return scriptParseResult;
        }
        return createExpression(varName, functionName, valueSqlVar);
    }

    @Override
    public boolean support(Object sqlNode) {
        if (sqlNode instanceof SqlBasicCall) {
            SqlBasicCall sqlBasicCall = (SqlBasicCall)sqlNode;
            if (sqlBasicCall.getOperator().getName().toUpperCase().equals("REGEXP")) {
                return true;
            }
            if (sqlBasicCall.getOperator().getName().toUpperCase().equals("NOT REGEXP")) {
                return true;
            }
        }
        return false;
    }
}
