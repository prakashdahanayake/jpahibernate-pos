package lk.ijse.pos.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class EntityManagerUtil {
    private static EntityManagerUtil entityManagerUtil;

    private EntityManagerUtil() {

    }
    public static EntityManagerUtil getInstance(){
        if (entityManagerUtil == null){
            entityManagerUtil = new EntityManagerUtil();
        }
        return entityManagerUtil;
    }

    public EntityManagerFactory getFactory() {
        File propFile = new File("resources/application.properties");
        Properties properties = new Properties();
        try {
            FileReader fileReader = new FileReader(propFile);
            properties.load(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("unit1", properties);
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        EntityManagerFactory factory = entityManagerUtil.getFactory();
        EntityManager entityManager = factory.createEntityManager();

        return entityManager;
    }
}
