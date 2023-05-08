package lk.ise.pos.bo.custom;

import lk.ise.pos.bo.SuperBO;
import lk.ise.pos.dto.OrderDetailsDto;
import lk.ise.pos.dto.OrderDto;

import java.sql.SQLException;
import java.util.List;

public interface OrderBO extends SuperBO {
    public boolean saveOrder(OrderDto dto, List<OrderDetailsDto> details) throws Exception;
    public String generateOrderId() throws SQLException, ClassNotFoundException;
}
