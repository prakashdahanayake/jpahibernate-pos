package lk.ijse.pos.business.custom;

import lk.ijse.pos.business.SuperBO;
import lk.ijse.pos.dto.ItemDTO;

import java.util.List;

public interface ItemBO extends SuperBO {

    public List<ItemDTO> getAllItems() throws Exception;

    public void saveItem(ItemDTO item) throws Exception;

    public void updateItem(ItemDTO item)throws Exception;

    public void deleteItem(String code) throws Exception;

}
