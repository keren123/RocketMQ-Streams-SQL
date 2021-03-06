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

import org.apache.rocketmq.streams.common.configure.ConfigureFileKey;
import org.apache.rocketmq.streams.common.topology.model.IWindow;
import com.alibaba.rsqldb.parser.parser.builder.SelectSQLBuilder;
import com.alibaba.rsqldb.parser.parser.result.IParseResult;
import com.alibaba.rsqldb.parser.parser.result.VarParseResult;
import org.apache.rocketmq.streams.window.builder.WindowBuilder;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlIntervalLiteral;
import org.apache.calcite.sql.SqlNode;

public class HopParser extends TumbleParser {

    @Override
    public IParseResult parse(SelectSQLBuilder builder, SqlBasicCall sqlBasicCall) {
        SqlNode[] operands = sqlBasicCall.getOperands();
        IParseResult fieldName = parseSqlNode(builder, operands[0]);
        SqlIntervalLiteral slide = (SqlIntervalLiteral)operands[1];
        SqlIntervalLiteral size = (SqlIntervalLiteral)operands[2];
        createWindowBuilder(builder, size, slide, fieldName.getReturnValue());
        return new VarParseResult(null);
    }

    public static com.alibaba.rsqldb.parser.parser.builder.WindowBuilder createWindowBuilder(SelectSQLBuilder builder) {
        /**
         * ????????????group by??????????????????????????????????????????????????????????????????????????????????????????1?????????
         */
        int inteval = WindowBuilder.getIntValue(ConfigureFileKey.DIPPER_WINDOW_DEFAULT_INERVAL_SIZE, 60);
        com.alibaba.rsqldb.parser.parser.builder.WindowBuilder
            windowBuilder = new com.alibaba.rsqldb.parser.parser.builder.WindowBuilder();
        windowBuilder.setType(IWindow.HOP_WINDOW);
        windowBuilder.setOwner(builder);
        windowBuilder.setSize(inteval);
        windowBuilder.setSlide(inteval);
        windowBuilder.setTimeFieldName("");
        builder.setWindowBuilder(windowBuilder);
        return windowBuilder;
    }

    public static com.alibaba.rsqldb.parser.parser.builder.WindowBuilder createWindowBuilder(SelectSQLBuilder builder, SqlIntervalLiteral size,
                                                                                                   SqlIntervalLiteral slide,
                                                                                                   String timeFieldName) {
        com.alibaba.rsqldb.parser.parser.builder.WindowBuilder
            windowBuilder = new com.alibaba.rsqldb.parser.parser.builder.WindowBuilder();
        windowBuilder.setType(IWindow.HOP_WINDOW);
        windowBuilder.setOwner(builder);
        setWindowParameter(false, windowBuilder, slide);
        setWindowParameter(true, windowBuilder, size);
        windowBuilder.setTimeFieldName(timeFieldName);
        builder.setWindowBuilder(windowBuilder);
        return windowBuilder;
    }

    @Override
    public boolean support(Object sqlNode) {
        if (sqlNode instanceof SqlBasicCall) {
            SqlBasicCall sqlBasicCall = (SqlBasicCall)sqlNode;
            if (sqlBasicCall.getOperator().getName().toLowerCase().equals("hop")) {
                return true;
            }
        }
        return false;
    }
}
