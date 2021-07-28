/*
 * Copyright 2019 ~ https://github.com/braver-tool
 */

package com.android.mysimplecalendar.localdb;


import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.sqlcipher.SQLCipherOpenHelper;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.android.dbflow.structure.database.OpenHelper;


@Database(name = LocalDataBase.NAME, version = LocalDataBase.VERSION)
public class LocalDataBase {
    static final String NAME = "MySqlite";
    static final int VERSION = 1;

    /**
     * @param databaseClazz -
     * @param <T>           -
     * @return - Method used to encrypt Local data base
     */
    public static <T> DatabaseConfig getConfig(Class<T> databaseClazz) {
        return new DatabaseConfig.Builder(databaseClazz)
                .openHelper(SQLCipherHelperImpl::new).build();
    }

    public static class SQLCipherHelperImpl extends SQLCipherOpenHelper {
        public SQLCipherHelperImpl(DatabaseDefinition databaseDefinition, DatabaseHelperListener listener) {
            super(databaseDefinition, listener);
        }

        @Override
        protected String getCipherSecret() {
            return "dbflow-rules";
        }
    }
}
