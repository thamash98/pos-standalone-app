package lk.ise.pos.bo.custom;

import lk.ise.pos.bo.SuperBO;
import lk.ise.pos.dto.OrderDetailsDto;

public interface OrderDetailsBo extends SuperBO {
    public boolean saveOrderDetails(OrderDetailsDto dto) throws Exception;
}
