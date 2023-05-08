package lk.ise.pos.bo.custom;

import lk.ise.pos.bo.SuperBO;
import lk.ise.pos.dto.CustomerDto;

import java.sql.SQLException;
import java.util.List;

public interface CustomerBO extends SuperBO {
    public boolean saveCustomer(CustomerDto dto) throws Exception;
    public boolean updateCustomer(CustomerDto dto) throws Exception;
    public CustomerDto findCustomer(String id) throws Exception;
    public boolean deleteCustomer(String id) throws Exception;
    public List<CustomerDto> findAllCustomers() throws Exception;

    public List<String> loadCustomerIds() throws SQLException, ClassNotFoundException;
}
