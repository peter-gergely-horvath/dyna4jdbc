/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.ScriptExecutionException;


enum InterpreterCommand {
    SET_SCRIPTENGINE("setScriptEngine") {
        @Override
        protected void parseParametersAndExecute(
                String parameters, InterpreterCommandHandler commandHandler)
                throws ScriptExecutionException {

            String scriptEngineName = parameters.trim();
            if ("".equals(scriptEngineName.trim())) {
                throw new ScriptExecutionException(
                        this.commandName + ": Missing mandatory parameter: ScriptEngineName", this.commandName);
            }


            commandHandler.setScriptEngine(scriptEngineName);
        }
    };

    //CHECKSTYLE.OFF: VisibilityModifier: should be visible to enum fields
    protected final String commandName;
    //CHECKSTYLE.ON: VisibilityModifier

    InterpreterCommand(String commandName) {
        this.commandName = commandName;
    }

    protected boolean canHandle(String commandLine) {
        return commandLine != null && commandLine.trim().startsWith(commandName);
    }

    protected abstract void parseParametersAndExecute(
            String parameters, InterpreterCommandHandler commandHandler)
            throws ScriptExecutionException;
}
