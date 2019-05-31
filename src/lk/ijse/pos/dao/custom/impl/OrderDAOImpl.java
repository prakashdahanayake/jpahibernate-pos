package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.CrudDAOImpl;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.entity.Order;

public class OrderDAOImpl extends CrudDAOImpl<Order, Integer> implements OrderDAO {

    @Override
    public int getLastOrderId() throws Exception {
        return (int) entityManager.createNativeQuery("SELECT id FROM `Order` ORDER BY id DESC LIMIT 1")
                .getSingleResult();
    }
}
