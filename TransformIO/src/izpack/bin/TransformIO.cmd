@echo off
@start "%{APP_NAME}" /D"%{INSTALL_PATH}" javaw -D%{JVM_CFG_LOG} -Djava.ext.dirs=%{EXT_DIR} -jar "%{INSTALL_PATH}/%{FINAL_NAME}.jar"
@rem vi:set tabstop=4 hardtabs=4 shiftwidth=4:
