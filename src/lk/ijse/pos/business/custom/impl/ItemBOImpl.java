package lk.ijse.pos.business.custom.impl;

import lk.ijse.pos.business.custom.ItemBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.DAOTypes;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.db.EntityManagerUtil;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.entity.Item;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBOImpl implements ItemBO {

    private ItemDAO itemDAO = DAOFactory.getInstance().getDAO(DAOTypes.ITEM);

    public List<ItemDTO> getAllItems() throws Exception {
        EntityManager entityManager =  EntityManagerUtil.getInstance().getEntityManager();
        entityManager.getTransaction().begin();
        itemDAO.setEntityManager(entityManager);
        List<ItemDTO> items = itemDAO.findAll().stream().map(item -> new ItemDTO(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand())).collect(Collectors.toList());
        entityManager.getTransaction().commit();
        entityManager.clear();
        return items;


    }

    public void saveItem(ItemDTO item) throws Exception {
        EntityManager entityManager =  EntityManagerUtil.getInstance().getEntityManager();
        entityManager.getTransaction().begin();
        itemDAO.setEntityManager(entityManager);
        itemDAO.save(new Item(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand()));
        entityManager.getTransaction().commit();
        entityManager.clear();
    }

    public void updateItem(ItemDTO item) throws Exception {
        EntityManager entityManager =  EntityManagerUtil.getInstance().getEntityManager();
        entityManager.getTransaction().begin();
        itemDAO.setEntityManager(entityManager);
        itemDAO.update(new Item(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand()));
        entityManager.getTransaction().commit();
        entityManager.clear();
    }

    public void deleteItem(String code) throws Exception {
        EntityManager entityManager =  EntityManagerUtil.getInstance().getEntityManager();
        entityManager.getTransaction().begin();
        itemDAO.setEntityManager(entityManager);
        itemDAO.delete(code);
        entityManager.getTransaction().commit();
        entityManager.clear();
    }

}
