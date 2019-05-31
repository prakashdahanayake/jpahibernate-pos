package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.custom.QueryDAO;
import lk.ijse.pos.entity.CustomEntity;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class QueryDAOImpl implements QueryDAO {

    private EntityManager entityManager;

    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<CustomEntity> getOrdersTotal() throws Exception {

        List<Object[]> list = entityManager.createNativeQuery("SELECT id, SUM(qty * unitPrice) AS Total FROM `Order` INNER JOIN\n" +
                "  OrderDetail OD on `Order`.id = OD.orderId GROUP BY id").getResultList();

        List<CustomEntity> al = new ArrayList<>();

        for (Object[] objects : list) {
            al.add(new CustomEntity((Integer) objects[0], (Double) objects[1]));
        }

        return al;
    }
}
