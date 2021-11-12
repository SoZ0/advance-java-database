package ca.sozoservers.dev.database.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.annotation.RetentionPolicy;

public class DatabaseModel {
    
    @SqlType(SqlValue.Table)
    public static String sqlTable;
    @SqlType(SqlValue.Set)
    public static String sqlSet;
    @SqlType(SqlValue.Get)
    public static String sqlGet;
    @SqlType(SqlValue.Update)
    public static String sqlUpdate;
    @SqlType(SqlValue.Delete)
    public static String sqlDelete;

    private void createSQL() {
        Class<? extends DatabaseModel> clazz = this.getClass();
        String TableName = clazz.getAnnotation(Table.class).value();
        sqlTable = "CREATE TABLE IF NOT EXISTS " + TableName + "(";
        sqlSet = "INSERT INTO " + TableName + "(";
        sqlGet = "SELECT ";
        sqlUpdate = "UPDATE " + TableName + " SET ";
        sqlDelete = "DELETE FROM " + TableName + " WHERE ";
        String setEnd = "";
        String PrimaryKey = "";
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(DataType.class)) {
                String name = field.getName();
                sqlTable += name + " " + field.getAnnotation(DataType.class).value();
                sqlSet += name + ", ";
                sqlGet += name + ", ";
                setEnd += "?,";
                if (field.isAnnotationPresent(Constraints.class)) {
                    String constraint = field.getAnnotation(Constraints.class).value();
                    sqlTable += " " + constraint;
                    if (constraint.equalsIgnoreCase("PRIMARY KEY")) {
                        sqlDelete += name + " = ?";
                        PrimaryKey = name + " = ?";
                    } else {
                        sqlUpdate += name + " = ?, ";
                    }
                } else {
                    sqlUpdate += name + " = ?, ";
                }
                sqlTable += ", ";
            }
        }
        sqlTable = sqlTable.substring(0, sqlTable.lastIndexOf(",")) + ");";
        setEnd = setEnd.substring(0, setEnd.lastIndexOf(",")) + ")";
        sqlSet = sqlSet.substring(0, sqlSet.lastIndexOf(",")) + ") VALUES(" + setEnd;
        sqlGet = sqlGet.substring(0, sqlGet.lastIndexOf(",")) + " FROM " + TableName + " WHERE " + PrimaryKey;
        sqlUpdate = sqlUpdate.substring(0, sqlUpdate.lastIndexOf(",")) + " WHERE " + PrimaryKey;
    }

    private static boolean verifySQL(){
        return !(sqlDelete == null || sqlGet == null || sqlSet == null || sqlTable == null || sqlUpdate == null);
    }

    public String getSQL(SqlValue type){
        if(!verifySQL()) createSQL();
        switch (type) {
            case Delete:
                return sqlDelete;
            case Get:
                return sqlGet;
            case Set:
                return sqlSet;
            case Table:
                return sqlTable;
            case Update:
                return sqlUpdate;
        }
        return null;
    }

    public String toJSON(){
        String json = new String("{\n");
        Class<? extends DatabaseModel> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(DataType.class)) {
                field.setAccessible(true);
                String name = field.getName();
                try {
                    Object value = field.get(this);
                    json += "\""+name+"\":\""+value+"\",\n";
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        json = json.substring(0, json.lastIndexOf(","));
        json += "\n}";
        return json;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Constraints {
        String value();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DataType {
        String value();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Table {
        String value();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SqlType {
        SqlValue value();
    }

    public enum SqlValue {
        Table, Get, Set, Update, Delete;
    }

}
