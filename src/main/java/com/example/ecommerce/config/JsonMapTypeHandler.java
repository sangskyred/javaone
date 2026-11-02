package com.example.ecommerce.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonMapTypeHandler extends BaseTypeHandler<Map<String, Object>> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    @Override
    public void setNonNullParameter(PreparedStatement ps, int index, Map<String, Object> parameter, JdbcType jdbcType) throws SQLException {
        try {
            String jsonValue = OBJECT_MAPPER.writeValueAsString(parameter);
            String database = ps.getConnection().getMetaData().getDatabaseProductName();
            if ("H2".equalsIgnoreCase(database)) {
                ps.setString(index, jsonValue);
            } else {
                PGobject json = new PGobject();
                json.setType("jsonb");
                json.setValue(jsonValue);
                ps.setObject(index, json);
            }
        } catch (Exception exception) {
            throw new SQLException("Cannot serialize JSON attributes", exception);
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return read(rs.getString(columnName));
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return read(rs.getString(columnIndex));
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return read(cs.getString(columnIndex));
    }

    private Map<String, Object> read(String value) throws SQLException {
        if (value == null || value.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            return OBJECT_MAPPER.readValue(value, MAP_TYPE);
        } catch (Exception exception) {
            throw new SQLException("Cannot deserialize JSON attributes", exception);
        }
    }
}
