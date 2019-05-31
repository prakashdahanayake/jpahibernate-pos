package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.CrudDAOImpl;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.entity.Item;
import org.hibernate.Session;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOImpl extends CrudDAOImpl<Item, String> implements ItemDAO {

}
