package lk.ise.pos.bo;

import lk.ise.pos.bo.custom.impl.CustomerBoIMPL;
import lk.ise.pos.bo.custom.impl.ItemBoIMPL;
import lk.ise.pos.bo.custom.impl.OrderBoIMPL;
import lk.ise.pos.bo.custom.impl.UserBoIMPL;
import lk.ise.pos.dao.custom.impl.OrderDetailsDaoImpl;
import lk.ise.pos.enums.BoType;

public class BoFactory {
    private static BoFactory boFactory;
    private BoFactory(){}
    public static BoFactory getInstance(){
        return boFactory==null?(boFactory= new BoFactory()):boFactory;
    }
    public <T> T getBo(BoType type){
        switch (type){
            case CUSTOMER:
                return (T) new CustomerBoIMPL();
            case USER:
                return (T) new UserBoIMPL();
            case ITEM:
                return (T) new ItemBoIMPL();
            case ORDER:
                return (T) new OrderBoIMPL();
            case ORDER_DETAIL:
                return (T) new OrderDetailsDaoImpl();
            default:
                return null;
        }
    }
}
