/*
 * Copyright (C) 2015 Joshua
 *
 * This file is a part of pircboty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ae97.pokebot.config.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.ae97.pokebot.config.Configuration;

public class MySQLConfiguration implements Configuration {

    private final String host, port, database, table, prefix, username, password;
    private final Map<String, CachedValue> cache = new HashMap<>();
    private final int cacheTime;

    public MySQLConfiguration(String host, String port, String database, String table, String prefix, String username, String password, int cacheTime) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.table = table;
        if (prefix == null || prefix.trim().isEmpty()) {
            this.prefix = null;
        } else {
            this.prefix = prefix.replace(" ", "_").toLowerCase();
        }
        this.username = username;
        this.password = password;
        this.cacheTime = cacheTime;
    }

    @Override
    public int getInt(String key) {
        return getInt(key, 0);
    }

    @Override
    public int getInt(String key, int def) {
        String obj = get(key, "0");
        if (obj != null) {
            return Integer.parseInt(obj);
        } else {
            return def;
        }
    }

    @Override
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        String obj = get(key, "false");
        if (obj != null) {
            return Boolean.getBoolean(obj);
        } else {
            return def;
        }
    }

    @Override
    public String getString(String key) {
        return getString(key, null);
    }

    @Override
    public String getString(String key, String def) {
        String obj = get(key, def);
        if (obj != null) {
            return obj;
        } else {
            return def;
        }
    }

    @Override
    public List<String> getStringList(String key) {
        String obj = get(key, "");
        if (obj != null) {
            return Arrays.asList(obj.split(","));
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void set(String key, Object value) {
    }

    @Override
    public void reload() {
        cache.clear();
    }

    private String get(String key, String def) {
        CachedValue cachedValue = cache.get(key);
        if (cachedValue == null || cachedValue.getTime() + cacheTime < System.currentTimeMillis()) {
            String val;
            try (Connection conn = openConnection()) {
                PreparedStatement stmt = conn.prepareStatement("SELECT val FROM " + table + " WHERE `id` = ?");
                stmt.setString(1, prefixKey(key));
                ResultSet result = stmt.executeQuery();
                if (result.first()) {
                    val = result.getString("val");
                } else {
                    val = def == null ? null : def;
                }
            } catch (SQLException ex) {
                throw new IllegalStateException(ex);
            }
            cachedValue = new CachedValue(val);
            cache.put(key, cachedValue);
        }
        return cachedValue.get();
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
    }

    private String prefixKey(String key) {
        key = key.toLowerCase();
        if (prefix == null || prefix.trim().isEmpty()) {
            return key;
        }
        return prefix + "." + key;
    }

    private class CachedValue {

        private final String value;
        private final long insertTime;

        public CachedValue(String value) {
            this.value = value;
            this.insertTime = System.currentTimeMillis();
        }

        public String get() {
            return value;
        }

        public long getTime() {
            return insertTime;
        }
    }

}
