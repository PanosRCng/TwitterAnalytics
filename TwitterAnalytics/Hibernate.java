package TwitterAnalytics;


import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.Transaction;


public class Hibernate
{
    private Session session = null;


    private Hibernate()
    {
        try
        {
            SessionFactory sessionFactory = new Configuration().configure("TwitterAnalytics/Config/hibernate.cfg.xml").buildSessionFactory();

            this.session = sessionFactory.openSession();
        }
        catch(Exception e)
        {
            System.out.println("Initial SessionFactory creation failed." + e);
        }
    }

    private static class SingletonHelper
    {
        private static final Hibernate INSTANCE = new Hibernate();
    }


    public static Session session()
    {
        return SingletonHelper.INSTANCE.session;
    }


    public static void save(Object obj)
    {
        Transaction transaction = null;

        try
        {
            transaction = Hibernate.session().getTransaction();

            transaction.begin();

            Hibernate.session().persist( obj );

            transaction.commit();
        }
        catch(Exception e)
        {
            if(transaction != null)
            {
                transaction.rollback();
            }

            e.printStackTrace();
        }
    }


    public static Object find(Class obj_class, Long id)
    {
        try
        {
            return Hibernate.session().find(obj_class, id);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


    public static void update(Object obj)
    {
        Transaction transaction = null;

        try
        {
            transaction = Hibernate.session().getTransaction();

            transaction.begin();

            Hibernate.session().update( obj );

            transaction.commit();
        }
        catch(Exception e)
        {
            if(transaction != null)
            {
                transaction.rollback();
            }

            e.printStackTrace();
        }
    }


    public static void delete(Object obj)
    {
        Transaction transaction = null;

        try
        {
            transaction = Hibernate.session().getTransaction();

            transaction.begin();

            Hibernate.session().delete(obj);

            transaction.commit();
        }
        catch(Exception e)
        {
            if(transaction != null)
            {
                transaction.rollback();
            }

            e.printStackTrace();
        }
    }

}