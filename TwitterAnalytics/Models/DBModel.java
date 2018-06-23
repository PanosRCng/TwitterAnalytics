package TwitterAnalytics.Models;


import TwitterAnalytics.DB;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;
import java.text.SimpleDateFormat;


class DBModel
{

    public int entry_id;
    public String created_at;
    public String updated_at;



    public DBModel get(int entry_id)
    {
        try
        {
            ResultSet resultSet = DB.query( this.retrieve_query(entry_id) );

            for(HashMap.Entry<String, Field> entry : this.atrribute_fields().entrySet())
            {
                String name = entry.getKey();
                Field field = entry.getValue();

                setValue(field, resultSet.getObject(name));
            }

            if( this.timestamps() )
            {
                setValue(this.fields().get("created_at"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(resultSet.getObject("created_at")));
                setValue(this.fields().get("updated_at"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(resultSet.getObject("updated_at")));
            }

            return this;
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

        return null;
    }


    public int save()
    {
        try
        {
            if( this.entry_id() == 0  )
            {
                int inserted_id = DB.insert( this.insertStatement(this.create_query()) );

                this.setValue(this.fields().get("entry_id"), inserted_id);
                return inserted_id;
            }

            if( DB.update( this.updateStatement(this.update_query()) ) )
            {
                return this.entry_id();
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return -1;
    }


    public boolean delete()
    {
        return DB.delete(this.delete_query());
    }


    protected String create_query()
    {
        String query = null;

        try
        {
            StringBuilder sb = new StringBuilder();
            sb.append("insert into " + this.table_name() + "(");

            for(HashMap.Entry<String, Field> entry : this.atrribute_fields().entrySet())
            {
                if(entry.getKey().equals("entry_id"))
                {
                    continue;
                }

                sb.append(entry.getValue().getName());
                sb.append(", ");
            }

            if( this.timestamps() )
            {
                sb.append("created_at, updated_at, ");
            }

            query = sb.toString();
            query = query.substring(0, query.length()-2);
            query += ")";

            sb = new StringBuilder();
            sb.append(" values(");

            for(HashMap.Entry<String, Field> entry : this.atrribute_fields().entrySet())
            {
                if(entry.getKey().equals("entry_id"))
                {
                    continue;
                }

                sb.append("?, ");
            }

            if( this.timestamps() )
            {
                sb.append("now(), now(), ");
            }

            String query_values = sb.toString();
            query_values = query_values.substring(0, query_values.length()-2);
            query_values += ")";

            query += query_values;
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

        return query;
    }


    protected PreparedStatement insertStatement(String query)
    {
        try
        {
            PreparedStatement preStmt = DB.conn().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int index = 1;

            for(HashMap.Entry<String, Field> entry : this.atrribute_fields().entrySet())
            {
                if(entry.getKey().equals("entry_id"))
                {
                    continue;
                }

                this.getStatementValue(preStmt, index++, entry.getValue());
            }

            return preStmt;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }

        return null;
    }


    protected String retrieve_query(int entry_id)
    {
        String query = null;

        try
        {
            StringBuilder sb = new StringBuilder();
            sb.append("select ");

            for(HashMap.Entry<String, Field> entry : this.atrribute_fields().entrySet())
            {
                sb.append(entry.getKey());
                sb.append(", ");
            }

            if( this.timestamps() )
            {
                sb.append("created_at, updated_at, ");
            }

            query = sb.toString();
            query = query.substring(0, query.length()-2);
            query += " from " + this.table_name();
            query += " where entry_id=" + entry_id;
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

        return query;
    }


    protected String update_query()
    {
        String query = null;

        try
        {
            StringBuilder sb = new StringBuilder();
            sb.append("update " + this.table_name() + " SET ");

            for(HashMap.Entry<String, Field> entry : this.atrribute_fields().entrySet())
            {
                String name = entry.getKey();
                Field field = entry.getValue();

                if(name.equals("entry_id"))
                {
                    continue;
                }

                //sb.append(name + "=" + this.getValue(field) + ", ");
                sb.append(name + "=?, ");
            }

            if( this.timestamps() )
            {
                sb.append("updated_at=now(), ");
            }

            query = sb.toString();
            query = query.substring(0, query.length()-2);
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

        return query;
    }


    protected PreparedStatement updateStatement(String query)
    {
        try
        {
            PreparedStatement preStmt = DB.conn().prepareStatement(query);

            int index = 1;

            for(HashMap.Entry<String, Field> entry : this.atrribute_fields().entrySet())
            {
                if(entry.getKey().equals("entry_id"))
                {
                    continue;
                }

                this.getStatementValue(preStmt, index++, entry.getValue());
            }

            return preStmt;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }

        return null;
    }


    protected String delete_query()
    {
        String query = null;

        try
        {
            query = "delete from " + this.table_name() + " where entry_id=" + this.entry_id();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

        return query;
    }


    private Object getValue(Field field)
    {
        try
        {
            if(field.getType() == String.class)
            {
                return "'" + field.get(this).toString() + "'";
            }

            if(field.getType() == Timestamp.class)
            {
                return "'" + field.get(this).toString() + "'";
            }

            return field.get(this);
        }
        catch(Exception ex)
        {

        }

        return null;
    }


    private PreparedStatement getStatementValue(PreparedStatement preStmt, int index, Field field)
    {
        try
        {
            if(field.getType() == String.class)
            {
                preStmt.setString(index, field.get(this).toString());
            }

            if(field.getType() == Timestamp.class)
            {
                preStmt.setString(index, field.get(this).toString());
            }

            if(field.getType() == int.class)
            {
                preStmt.setInt(index, (int)field.get(this));
            }

            if(field.getType() == long.class)
            {
                preStmt.setLong(index, (long)field.get(this));
            }

            return preStmt;
        }
        catch(Exception ex)
        {

        }

        return null;
    }


    private void setValue(Field field, Object value)
    {
        try
        {
            field.set(this, value);
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }


    private HashMap<String,Field> fields()
    {
        HashMap<String,Field> fields = new HashMap<>();

        try
        {
            for(Field field : this.getClass().getFields())
            {
                fields.put(field.getName(), field);
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

        return fields;
    }


    private HashMap<String,Field> atrribute_fields()
    {
        HashMap<String,Field> fields = new HashMap<>();

        try
        {
            for(Field field : this.getClass().getFields())
            {
                String name = field.getName();

                if(name.equals("table"))
                {
                    continue;
                }

                if(name.equals("timestamps"))
                {
                    continue;
                }

                if(name.equals("created_at"))
                {
                    continue;
                }

                if(name.equals("updated_at"))
                {
                    continue;
                }

                fields.put(name, field);
            }

        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

        return fields;
    }


    private String table_name()
    {
        try
        {
            return this.fields().get("table").get(this).toString();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

        return null;
    }


    private boolean timestamps()
    {
        try
        {
            return (boolean) this.fields().get("timestamps").get(this);
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

        return false;
    }


    private int entry_id()
    {
        return (int) this.getValue( this.fields().get("entry_id") );
    }

}
