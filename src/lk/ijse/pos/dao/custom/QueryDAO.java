package lk.ijse.pos.dao.custom;

import lk.ijse.pos.dao.SuperDAO;
import lk.ijse.pos.entity.CustomEntity;

import java.util.List;

public interface QueryDAO extends SuperDAO {

    List<CustomEntity> getOrdersTotal() throws Exception;

}
