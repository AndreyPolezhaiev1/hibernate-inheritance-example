package core.basesyntax.dao.animal;

import core.basesyntax.dao.AbstractDao;
import core.basesyntax.model.zoo.Animal;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class AnimalDaoImpl extends AbstractDao implements AnimalDao {
    public AnimalDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Animal save(Animal animal) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(animal);
            transaction.commit();
            return animal;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save animal: " + animal, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Animal> findByNameFirstLetter(Character character) {
        String c = "" + character;
        String f1 = c.toLowerCase();
        String s2 = c.toUpperCase();

        try (Session session = sessionFactory.openSession()) {
            Query<Animal> query = session.createQuery("FROM Animal a "
                     + "WHERE a.name LIKE :f1 OR a.name LIKE :s2", Animal.class);
            query.setParameter("f1", f1 + "%");
            query.setParameter("s2", s2 + "%");
            return query.getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Can't get animals by first letter: " + character, e);
        }
    }
}
