package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import com.github.dyna4jdbc.internal.config.Configuration;

public interface ExternalProcessScriptExecutorFactory {

    ExternalProcessScriptExecutor newExternalProcessScriptExecutor(Configuration configuration);

}
