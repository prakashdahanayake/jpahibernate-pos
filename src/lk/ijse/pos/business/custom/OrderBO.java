package lk.ijse.pos.business.custom;

import lk.ijse.pos.business.SuperBO;
import lk.ijse.pos.dto.OrderDTO;

public interface OrderBO extends SuperBO {

    public void placeOrder(OrderDTO order) throws Exception;

    public int generateOrderId() throws Exception;

}
