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
package com.alibaba.rsqldb.parser.parser.function;

import java.util.List;

import com.alibaba.rsqldb.parser.parser.builder.SelectSQLBuilder;
import com.alibaba.rsqldb.parser.parser.result.IParseResult;
import com.alibaba.rsqldb.parser.parser.result.ScriptParseResult;
import com.alibaba.rsqldb.parser.parser.sqlnode.AbstractSelectNodeParser;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlNode;

public class JsonConcatParser extends AbstractSelectNodeParser<SqlBasicCall> {

    @Override
    public IParseResult parse(SelectSQLBuilder tableDescriptor, SqlBasicCall sqlBasicCall) {
        List<SqlNode> sqlNodeList = sqlBasicCall.getOperandList();
        StringBuilder stringBuilder = new StringBuilder("json_concat(");
        boolean isFirst = true;
        for (int i = 0; i < sqlNodeList.size(); i = i + 2) {
            SqlNode keyNode = sqlNodeList.get(i);
            SqlNode valueNode = sqlNodeList.get(i + 1);
            String key = parseSqlNode(tableDescriptor, keyNode).getValueForSubExpression();
            String value = parseSqlNode(tableDescriptor, valueNode).getValueForSubExpression();
            if (isFirst) {
                isFirst = false;
            } else {
                stringBuilder.append(",");
            }
            stringBuilder.append(key + ":" + value);
        }
        stringBuilder.append(");");
        ScriptParseResult scriptParseResult = new ScriptParseResult();
        scriptParseResult.addScript(stringBuilder.toString());
        return scriptParseResult;
    }

    @Override
    public boolean support(Object sqlNode) {
        //if(SqlBasicCall.class.isInstance(sqlNode)){
        //    SqlBasicCall sqlBasicCall=(SqlBasicCall)sqlNode;
        //    if(sqlBasicCall.getOperator().getName().toLowerCase().equals("json_concat")){
        //        return true;
        //    }
        //}
        return false;
    }
}
